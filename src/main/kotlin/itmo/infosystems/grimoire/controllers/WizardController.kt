package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.dto.responses.WizardResponse
import itmo.infosystems.grimoire.security.WizardPrincipal
import itmo.infosystems.grimoire.services.WizardService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/wizards")
class WizardController(private val wizardService: WizardService) {

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal principal: WizardPrincipal): WizardResponse {
        return wizardService.getWizard(principal.id)
    }

    @GetMapping("/{id}")
    fun getWizard(@PathVariable id: Long): WizardResponse {
        return wizardService.getWizard(id)
    }

    @PutMapping("/join/{guildId}")
    fun joinGuild(
        @AuthenticationPrincipal principal: WizardPrincipal,
        @PathVariable guildId: Long
    ): WizardResponse {
        val updatedWizard = wizardService.joinGuild(principal.id, guildId)
        return wizardService.getWizard(updatedWizard.id)
    }
}