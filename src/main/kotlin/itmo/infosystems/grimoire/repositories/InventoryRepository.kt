package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.dto.responses.ArtifactResponse
import itmo.infosystems.grimoire.models.WizardInventory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


interface InventoryRepository : JpaRepository<WizardInventory, Long> {
    @Query(
        """
        SELECT new itmo.infosystems.grimoire.dto.responses.ArtifactResponse(a.name, a.rarity) 
        FROM WizardInventory wi JOIN wi.artifact a WHERE wi.wizard.id = :wizardId
        """
    )
    fun findArtifactsByWizardId(wizardId: Long): List<ArtifactResponse>
}