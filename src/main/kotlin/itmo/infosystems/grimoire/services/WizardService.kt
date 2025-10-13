package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.dto.WizardResponse
import itmo.infosystems.grimoire.repositories.WizardRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service

@Service
class WizardService(private val wizardRepository: WizardRepository) {

    fun getWizard(id: Long): WizardResponse {
        return wizardRepository.findWizardProfile(id)
            .orElseThrow { EntityNotFoundException("Wizard with id $id not found") }
    }
}