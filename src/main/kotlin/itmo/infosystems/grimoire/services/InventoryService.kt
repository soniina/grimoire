package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.repositories.InventoryRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service


@Service
class InventoryService(private val inventoryRepository: InventoryRepository) {
    fun getInventory(wizardId: Long, pageable: Pageable) =
        inventoryRepository.findArtifactsByWizardId(wizardId, pageable)
}