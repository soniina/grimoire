package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.repositories.InventoryRepository
import org.springframework.stereotype.Service


@Service
class InventoryService(private val inventoryRepository: InventoryRepository) {
    fun getInventory(wizardId: Long) = inventoryRepository.findArtifactsByWizardId(wizardId)
}