package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.Artifact
import itmo.infosystems.grimoire.models.WizardInventory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface InventoryRepository : JpaRepository<WizardInventory, Long> {
    @Query(
        """
            SELECT a
            FROM WizardInventory wi
            JOIN wi.artifact a
            WHERE wi.wizard.id = :wizardId
    """
    )
    fun findArtifactsByWizardId(wizardId: Long, pageable: Pageable): Page<Artifact>

}