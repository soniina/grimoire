package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.dto.responses.WizardResponse
import itmo.infosystems.grimoire.models.Wizard
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

interface WizardRepository : JpaRepository<Wizard, Long> {
    fun existsByLogin(login: String): Boolean
    fun findByLogin(login: String): Wizard?

    @Query(
        """
        SELECT new itmo.infosystems.grimoire.dto.responses.WizardResponse(
            w.login, 
            g.name, g.level, g.spellsPerDayLimit, g.artifactsInventoryLimit, g.spellsForArtifact,
            h.name, h.surname
        )
        FROM Wizard w 
        LEFT JOIN Human h ON h.wizard.id = w.id 
        LEFT JOIN w.guild g 
        WHERE w.id = :id
    """
    )
    fun findWizardProfile(id: Long): Optional<WizardResponse>
}