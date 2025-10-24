package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.SpellCast
import itmo.infosystems.grimoire.models.SpellCastStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface SpellCastRepository : JpaRepository<SpellCast, Long> {
    fun findByWizardIdAndStatus(wizardId: Long, status: SpellCastStatus): List<SpellCast>

    @Query(
        """
        SELECT sc FROM SpellCast sc 
        JOIN sc.wizard w LEFT JOIN w.guild g 
        WHERE sc.status = 'ACTIVE' AND (g.level IS NULL OR g.level <= :guildLevel)
        AND sc.wizard.id != :wizardId
        """
    )
    fun findActiveSpellsFromWizardsWithLowerOrEqualGuildLevel(guildLevel: Int, wizardId: Long): List<SpellCast>

    @Modifying
    @Query(
        value = """
    UPDATE spell_cast
    SET status = 'EXPIRED'
    WHERE status = 'ACTIVE' AND expire_time <= NOW()
""", nativeQuery = true
    )
    fun expireSpells()

}
