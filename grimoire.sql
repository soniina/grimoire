CREATE OR REPLACE FUNCTION get_spellbook(p_wizard_id BIGINT)
    RETURNS TABLE
            (
                spell_id             BIGINT,
                name                 VARCHAR,
                type                 VARCHAR,
                description          TEXT,
                required_guild_level INT,
                victim_type          VARCHAR
            )
AS
$$
DECLARE
    wizard_guild_level INT;
BEGIN
    SELECT COALESCE(g.level, 0)
    INTO wizard_guild_level
    FROM wizard w
             LEFT JOIN guild g ON w.guild_id = g.guild_id
    WHERE w.wizard_id = p_wizard_id;

    RETURN QUERY
        SELECT s.spell_id,
               s.name,
               s.type,
               s.description::text,
               s.required_guild_level,
               s.victim_type
        FROM spell s
                 LEFT JOIN spell_cast sc
                           ON s.spell_id = sc.spell_id
                               AND sc.wizard_id = p_wizard_id
        WHERE s.required_guild_level < wizard_guild_level
           OR (s.required_guild_level = wizard_guild_level AND sc.spell_cast_id IS NOT NULL);
END;
$$ LANGUAGE plpgsql STABLE;


-- ============================================================
-- FUNCTION: check_spellcast_before_insert()
-- ============================================================
CREATE OR REPLACE FUNCTION check_spellcast_before_insert()
    RETURNS trigger AS
$$
DECLARE
    wizard_guild_id      INT;
    wizard_guild_level   INT;
    required_guild_level INT;
    spells_cast_today    INT;
    artifact_count       INT;
    victim_wizard_id     INT;
    victim_wizard_guild  INT;
    victim_guild_level   INT;
    spell_usage_count    INT;
    spell_type           VARCHAR(20);
    victim_type          VARCHAR(20);
    victim_is_alive      BOOLEAN;
    spell_per_day_limit  INT;
BEGIN
    -- Получаем данные о заклинании
    SELECT s.required_guild_level, s.type, s.victim_type
    INTO required_guild_level, spell_type, victim_type
    FROM Spell s
    WHERE s.spell_id = NEW.spell_id;

    -- Данные о маге и его гильдии
    SELECT w.guild_id, COALESCE(g.level, 0), COALESCE(g.spells_per_day_limit, 0)
    INTO wizard_guild_id, wizard_guild_level, spell_per_day_limit
    FROM Wizard w
             LEFT JOIN Guild g ON w.guild_id = g.guild_id
    WHERE w.wizard_id = NEW.wizard_id;

    IF wizard_guild_level < required_guild_level THEN
        RAISE EXCEPTION USING MESSAGE = 'Уровень гильдии мага слишком низок для этого заклинания', ERRCODE = 'P0001';
    END IF;

    -- Проверка дневного лимита
    SELECT COUNT(*)
    INTO spells_cast_today
    FROM spell_cast sc
    WHERE sc.wizard_id = NEW.wizard_id
      AND sc.cast_time::date = NEW.cast_time::date;

    IF spells_cast_today >= spell_per_day_limit THEN
        RAISE EXCEPTION USING MESSAGE = 'Превышен лимит заклинаний мага на день', ERRCODE = 'P0001';
    END IF;

    -- Проверка наличия требуемых артефактов
    IF EXISTS (SELECT 1
               FROM artifact_spell_requirement
               WHERE spell_id = NEW.spell_id
                 AND spell_usage_type != 'REMOVE') THEN
        SELECT COUNT(*)
        INTO artifact_count
        FROM wizard_inventory wa
                 JOIN artifact_spell_requirement sa ON wa.artifact_id = sa.artifact_id
        WHERE wa.wizard_id = NEW.wizard_id
          AND sa.spell_id = NEW.spell_id
          AND sa.spell_usage_type != 'REMOVE';

        IF artifact_count = 0 THEN
            RAISE EXCEPTION USING MESSAGE = 'Отсутствуют требуемые артефакты для этого заклинания', ERRCODE = 'P0001';
        END IF;
    END IF;

    -- Проверка жертвы
    SELECT h.wizard_id, h.is_alive
    INTO victim_wizard_id, victim_is_alive
    FROM Human h
    WHERE h.human_id = NEW.victim_id;

    IF victim_is_alive IS NOT TRUE THEN
        RAISE EXCEPTION USING MESSAGE = 'Нельзя накладывать заклинание на мертвого человека', ERRCODE = 'P0001';
    END IF;

    IF victim_type = 'WIZARD' AND victim_wizard_id IS NULL THEN
        RAISE EXCEPTION USING MESSAGE = 'Заклинание можно накладывать только на магов', ERRCODE = 'P0001';
    ELSIF victim_type = 'HUMAN' AND victim_wizard_id IS NOT NULL THEN
        RAISE EXCEPTION USING MESSAGE = 'Заклинание можно накладывать только на обычных людей', ERRCODE = 'P0001';
    END IF;

    -- Если жертва - маг
    IF victim_wizard_id IS NOT NULL THEN
        SELECT w.guild_id, COALESCE(g.level, 0)
        INTO victim_wizard_guild, victim_guild_level
        FROM Wizard w
                 LEFT JOIN Guild g ON w.guild_id = g.guild_id
        WHERE w.wizard_id = victim_wizard_id;

        IF victim_wizard_id = NEW.wizard_id THEN
            RAISE EXCEPTION USING MESSAGE = 'Нельзя накладывать заклинание на самого себя', ERRCODE = 'P0001';
        END IF;

        IF victim_wizard_guild = wizard_guild_id THEN
            RAISE EXCEPTION USING MESSAGE = 'Жертва состоит в той же гильдии, что и маг', ERRCODE = 'P0001';
        END IF;

        IF victim_guild_level >= wizard_guild_level THEN
            RAISE EXCEPTION USING MESSAGE = 'Уровень гильдии жертвы не ниже уровня гильдии мага', ERRCODE = 'P0001';
        END IF;
    END IF;

    -- Проверка повторного активного заклинания
    SELECT COUNT(*)
    INTO spell_usage_count
    FROM spell_cast
    WHERE victim_id = NEW.victim_id
      AND spell_id = NEW.spell_id
      AND status = 'ACTIVE';

    IF spell_usage_count > 0 THEN
        RAISE EXCEPTION USING MESSAGE = 'Такое же активное заклинание уже наложено на жертву', ERRCODE = 'P0001';
    END IF;

    -- Проверка защитных заклинаний
    IF EXISTS (SELECT 1
               FROM spell_cast sc
                        JOIN Spell s ON s.spell_id = sc.spell_id
               WHERE sc.victim_id = NEW.victim_id
                 AND sc.status = 'ACTIVE'
                 AND s.type = 'DEFENSE') THEN
        RAISE EXCEPTION USING MESSAGE = 'На жертве уже есть активное защитное заклинание', ERRCODE = 'P0001';
    END IF;

    -- Проверка запрета магии
    IF EXISTS (SELECT 1
               FROM spell_cast sc
                        JOIN Spell s ON s.spell_id = sc.spell_id
               WHERE sc.victim_id IN (SELECT human_id FROM Human WHERE wizard_id = NEW.wizard_id)
                 AND sc.status = 'ACTIVE'
                 AND s.type = 'FORBIDDEN_MAGIC') THEN
        RAISE EXCEPTION USING MESSAGE = 'На мага наложено заклинание запрета магии', ERRCODE = 'P0001';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_spellcast_before_insert
    BEFORE INSERT
    ON spell_cast
    FOR EACH ROW
EXECUTE FUNCTION check_spellcast_before_insert();



-- ============================================================
-- FUNCTION: check_spellcast_before_update()
-- ============================================================
CREATE OR REPLACE FUNCTION check_spellcast_before_update()
    RETURNS trigger AS
$$
DECLARE
    wizard_guild_level   INT;
    required_guild_level INT;
    spells_cast_today    INT;
    artifact_count       INT;
    spell_per_day_limit  INT;
    victim_wizard_id     INT;
    victim_guild_level   INT;
BEGIN
    -- Проверка только при снятии заклинания
    IF OLD.status <> 'REMOVED' AND NEW.status = 'REMOVED' THEN
        -- Данные о маге, снимающем заклинание
        SELECT COALESCE(g.level, 0), COALESCE(g.spells_per_day_limit, 0)
        INTO wizard_guild_level, spell_per_day_limit
        FROM Wizard w
                 LEFT JOIN Guild g ON w.guild_id = g.guild_id
        WHERE w.wizard_id = NEW.removed_by_wizard_id;

        -- Требуемый уровень гильдии
        SELECT s.required_guild_level
        INTO required_guild_level
        FROM Spell s
        WHERE s.spell_id = NEW.spell_id;

        IF wizard_guild_level < required_guild_level THEN
            RAISE EXCEPTION USING MESSAGE =
                    'Уровень гильдии мага слишком низок для снятия этого заклинания', ERRCODE = 'P0001';
        END IF;

        -- Проверка дневного лимита
        SELECT COUNT(*)
        INTO spells_cast_today
        FROM spell_cast sc
        WHERE sc.wizard_id = NEW.removed_by_wizard_id
          AND sc.cast_time::date = NEW.cast_time::date;

        IF spells_cast_today >= spell_per_day_limit THEN
            RAISE EXCEPTION USING MESSAGE = 'Превышен дневной лимит заклинаний для мага', ERRCODE = 'P0001';
        END IF;

        -- Проверка наличия артефактов для снятия
        IF EXISTS (SELECT 1
                   FROM artifact_spell_requirement
                   WHERE spell_id = NEW.spell_id
                     AND spell_usage_type != 'CAST') THEN
            SELECT COUNT(*)
            INTO artifact_count
            FROM wizard_inventory wa
                     JOIN artifact_spell_requirement sa ON wa.artifact_id = sa.artifact_id
            WHERE wa.wizard_id = NEW.removed_by_wizard_id
              AND sa.spell_id = NEW.spell_id
              AND sa.spell_usage_type != 'CAST';

            IF artifact_count = 0 THEN
                RAISE EXCEPTION USING MESSAGE =
                        'У мага отсутствуют необходимые артефакты для снятия заклинания', ERRCODE = 'P0001';
            END IF;
        END IF;

        -- Проверка гильдий мага и жертвы
        SELECT h.wizard_id
        INTO victim_wizard_id
        FROM Human h
        WHERE h.human_id = NEW.victim_id;

        IF victim_wizard_id IS NOT NULL THEN
            SELECT COALESCE(g.level, 0)
            INTO victim_guild_level
            FROM Wizard w
                     LEFT JOIN Guild g ON w.guild_id = g.guild_id
            WHERE w.wizard_id = victim_wizard_id;

            IF wizard_guild_level < victim_guild_level THEN
                RAISE EXCEPTION USING MESSAGE =
                        'Нельзя снимать заклинание с мага более высокого уровня гильдии', ERRCODE = 'P0001';
            END IF;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_spellcast_before_update
    BEFORE UPDATE
    ON spell_cast
    FOR EACH ROW
EXECUTE FUNCTION check_spellcast_before_update();



-- ============================================================
-- FUNCTION: log_spellcast_event()
-- ============================================================
CREATE OR REPLACE FUNCTION handle_spellcast_rewards()
    RETURNS TRIGGER AS
$$
DECLARE
    spells_for_artifact          INT;
    artifact_count               INT;
    artifact_id                  BIGINT;
    current_guild_id             BIGINT;
    last_award_time              TIMESTAMP;
    spells_cast_since_last_award INT;
    spells_count                 INT;
    spells_cast                  INT;
    current_guild_level          INT;
BEGIN
    -- Логируем действия мага
    IF TG_OP = 'INSERT' THEN
        INSERT INTO spell_log (event_time, event_type, wizard_id)
        VALUES (NOW(), 'CAST', NEW.wizard_id);

    ELSIF TG_OP = 'UPDATE' THEN
        IF NEW.status = 'REMOVED' THEN
            INSERT INTO spell_log (event_time, event_type, wizard_id)
            VALUES (NOW(), 'REMOVE', NEW.removed_by_wizard_id);
        END IF;
    END IF;


    IF TG_OP = 'INSERT' THEN
        -- Получаем параметры гильдии
        SELECT w.guild_id, w.last_artifact_award_time, g.spells_for_artifact, g.level
        INTO current_guild_id, last_award_time, spells_for_artifact, current_guild_level
        FROM wizard w
                 JOIN guild g ON g.guild_id = w.guild_id
        WHERE w.wizard_id = NEW.wizard_id;

        IF current_guild_id IS NULL THEN
            RAISE NOTICE 'Wizard % has no guild, skipping.', NEW.wizard_id;
            RETURN NEW;
        END IF;

        -- Сколько заклинаний было с последней награды
        SELECT COUNT(*)
        INTO spells_cast_since_last_award
        FROM spell_cast sc
        WHERE sc.wizard_id = NEW.wizard_id
          AND (last_award_time IS NULL OR sc.cast_time >= last_award_time);

        RAISE NOTICE '✨ Wizard % cast % spells since last award (needs %)',
            NEW.wizard_id, spells_cast_since_last_award, spells_for_artifact;

        -- Проверяем — пора ли выдать артефакт
        IF spells_cast_since_last_award >= spells_for_artifact THEN
            SELECT COUNT(*)
            INTO artifact_count
            FROM wizard_inventory
            WHERE wizard_id = NEW.wizard_id;

            SELECT a.artifact_id
            INTO artifact_id
            FROM artifact a
            WHERE a.artifact_id NOT IN (SELECT wi.artifact_id
                                        FROM wizard_inventory wi
                                        WHERE wi.wizard_id = NEW.wizard_id)
            ORDER BY ABS(a.rarity - (RANDOM() * 100))
            LIMIT 1;

            IF artifact_id IS NOT NULL THEN
                INSERT INTO wizard_inventory(wizard_id, artifact_id)
                VALUES (NEW.wizard_id, artifact_id);

                UPDATE wizard
                SET last_artifact_award_time = NOW()
                WHERE wizard_id = NEW.wizard_id;

                RAISE NOTICE '🏅 Artifact awarded to wizard %', NEW.wizard_id;

                PERFORM pg_notify('artifact_awarded', NEW.wizard_id::text);
            END IF;
        END IF;


        -- Считаем заклинания для текущего уровня гильдии
        SELECT COUNT(*)
        INTO spells_count
        FROM spell
        WHERE required_guild_level = current_guild_level;

        SELECT COUNT(DISTINCT sc.spell_id)
        INTO spells_cast
        FROM spell_cast sc
                 JOIN spell s ON s.spell_id = sc.spell_id
        WHERE sc.wizard_id = NEW.wizard_id
          AND s.required_guild_level = current_guild_level;

        RAISE NOTICE 'Checking upgrade: spells_cast=% vs spells_count=%',
            spells_cast, spells_count;

        -- Проверяем апгрейд гильдии
        IF spells_cast >= spells_count THEN
            RAISE NOTICE 'Guild upgrade available for wizard %', NEW.wizard_id;
            PERFORM pg_notify('guild_upgrade_available', NEW.wizard_id::text);
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер
DROP TRIGGER IF EXISTS trg_handle_spellcast_rewards ON spell_cast;
CREATE TRIGGER trg_handle_spellcast_rewards
    AFTER INSERT OR UPDATE
    ON spell_cast
    FOR EACH ROW
EXECUTE FUNCTION handle_spellcast_rewards();



CREATE OR REPLACE FUNCTION update_last_award_time_on_guild_change()
    RETURNS TRIGGER AS
$$
BEGIN
    IF TG_OP = 'UPDATE' AND OLD.guild_id IS DISTINCT FROM NEW.guild_id THEN
        UPDATE wizard
        SET last_artifact_award_time = NOW()
        WHERE wizard_id = NEW.wizard_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_last_award_time_on_guild_change
    AFTER UPDATE
    ON wizard
    FOR EACH ROW
EXECUTE FUNCTION update_last_award_time_on_guild_change();



CREATE OR REPLACE FUNCTION check_wizard_inventory_limit() RETURNS trigger AS
$$
DECLARE
    guild_limit        INT;
    current_count      INT;
    random_artifact_id INT;
BEGIN
    -- Получение лимита артефактов по гильдии мага
    SELECT g.artifacts_inventory_limit
    INTO guild_limit
    FROM Guild g
             JOIN Wizard w ON w.guild_id = g.guild_id
    WHERE w.wizard_id = NEW.wizard_id;

    IF guild_limit IS NULL THEN
        RAISE EXCEPTION 'У мага нет гильдии, нельзя добавлять артефакты';
    END IF;

    -- Текущее количество артефактов у мага
    SELECT COUNT(*)
    INTO current_count
    FROM wizard_inventory
    WHERE wizard_id = NEW.wizard_id;


    -- Если добавление нового артефакта превышает лимит
    IF current_count >= guild_limit THEN
        -- Выбор случайного артефакта для удаления
        SELECT artifact_id
        INTO random_artifact_id
        FROM wizard_inventory
        WHERE wizard_id = NEW.wizard_id
        ORDER BY random()
        LIMIT 1;


        -- Удаляем выбранный артефакт
        DELETE
        FROM wizard_inventory
        WHERE wizard_id = NEW.wizard_id
          AND artifact_id = random_artifact_id;
    END IF;


    -- Разрешаем вставку нового артефакта
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_check_wizardartifact_inventory_limit
    BEFORE INSERT
    ON wizard_inventory
    FOR EACH ROW
EXECUTE FUNCTION check_wizard_inventory_limit();



CREATE OR REPLACE FUNCTION available_guilds(p_wizard_id BIGINT)
    RETURNS TABLE
            (
                guild_id                  BIGINT,
                level                     INT,
                spells_per_day_limit      INT,
                artifacts_inventory_limit INT,
                name                      VARCHAR,
                spells_for_artifact       INT
            )
AS
$$
DECLARE
    current_guild_level INT;
    current_guild_id    INT;
    spells_count        INT;
    spells_cast         INT;
BEGIN
    -- Получение гильдии и уровня мага
    SELECT g.guild_id, g.level
    INTO current_guild_id, current_guild_level
    FROM Guild g
             JOIN Wizard w ON w.guild_id = g.guild_id
    WHERE w.wizard_id = p_wizard_id;

    -- Если маг без гильдии, возвращаем гильдии 1 уровня
    IF current_guild_id IS NULL THEN
        RETURN QUERY
            SELECT g.guild_id,
                   g.level,
                   g.spells_per_day_limit,
                   g.artifacts_inventory_limit,
                   g.name,
                   g.spells_for_artifact
            FROM Guild g
            WHERE g.level = 1;
        RETURN;
    END IF;

    -- Количество доступных заклинаний для текущего уровня гильдии
    SELECT COUNT(*)
    INTO spells_count
    FROM Spell
    WHERE required_guild_level = current_guild_level;


    -- Количество заклинаний, которые маг уже наложил из этого уровня
    SELECT COUNT(DISTINCT sc.spell_id)
    INTO spells_cast
    FROM spell_cast sc
    WHERE sc.wizard_id = p_wizard_id
      AND sc.status = 'active'
      AND sc.spell_id IN (SELECT spell.spell_id FROM Spell WHERE required_guild_level = current_guild_level);


    -- Если все заклинания текущего уровня выполнены
    IF spells_cast >= spells_count THEN
        RETURN QUERY
            SELECT *
            FROM Guild
            WHERE level = current_guild_level + 1;
    ELSE
        RETURN;
    END IF;


END;
$$ LANGUAGE plpgsql STABLE;


CREATE OR REPLACE FUNCTION available_victims(p_wizard_id BIGINT)
    RETURNS SETOF human AS
$$
DECLARE
    v_req_guild_id    BIGINT;
    v_req_guild_level INT;
BEGIN
    SELECT w.guild_id,
           COALESCE(g.level, 0)
    INTO
        v_req_guild_id,
        v_req_guild_level
    FROM wizard w
             LEFT JOIN guild g ON w.guild_id = g.guild_id
    WHERE w.wizard_id = p_wizard_id;

    IF NOT FOUND THEN
        RETURN;
    END IF;

    RETURN QUERY
        SELECT h.*
        FROM human h
                 LEFT JOIN wizard w_target ON h.wizard_id = w_target.wizard_id
                 LEFT JOIN guild g_target ON w_target.guild_id = g_target.guild_id
        WHERE h.is_alive = TRUE
          AND h.wizard_id IS DISTINCT FROM p_wizard_id
          AND (
            h.wizard_id IS NULL
                OR
            (
                (v_req_guild_id IS NULL OR w_target.guild_id IS NULL OR w_target.guild_id <> v_req_guild_id)
                    AND
                COALESCE(g_target.level, 0) <= v_req_guild_level
                )
            );
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION expire_spells()
    RETURNS integer AS
$$
DECLARE
    expired RECORD;
    cnt     INT := 0;
BEGIN
    FOR expired IN
        SELECT spell_cast_id, wizard_id
        FROM spell_cast
        WHERE status = 'ACTIVE'
          AND expire_time <= NOW()
        LOOP
            UPDATE spell_cast
            SET status = 'EXPIRED'
            WHERE spell_cast_id = expired.spell_cast_id;

            cnt := cnt + 1;
        END LOOP;
    RETURN cnt;
END;
$$ LANGUAGE plpgsql;


NOTIFY guild_upgrade_available, '1'