package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.models.Wizard
import itmo.infosystems.grimoire.services.WizardService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/wizards")
class WizardController(private val wizardService: WizardService) {

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal wizardId: String): Wizard {
        return wizardService.getWizard(wizardId.toLong())
    }

    @GetMapping("/{id}")
    fun getWizard(@PathVariable id: Long): Wizard {
        return wizardService.getWizard(id)
    }

    @PutMapping("/join/{guildId}")
    fun joinGuild(
        @AuthenticationPrincipal wizardId: String,
        @PathVariable guildId: Long
    ): Wizard {
        val updatedWizard = wizardService.joinGuild(wizardId.toLong(), guildId)
        return wizardService.getWizard(updatedWizard.id)
    }
}