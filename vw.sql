CREATE OR REPLACE VIEW spell_book AS
SELECT w.wizard_id,
       s.spell_id,
       s.name                 as spell_name,
       s.required_guild_level as spell_level,
       CASE
           WHEN sc.spell_cast_id IS NOT NULL THEN true
           ELSE false
           END                as is_learned
FROM wizard
         JOIN
     guild g ON w.guild_id = g.guild_id
         CROSS JOIN
     spell s
         LEFT JOIN
     spell_cast sc ON w.wizard_id = sc.wizard_id
         AND s.spell_id = sc.spell_id
WHERE s.required_guild_level < g.level
   OR (s.required_guild_level = g.level AND sc.spell_cast_id IS NOT NULL);