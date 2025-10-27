package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.models.Wizard
import itmo.infosystems.grimoire.services.WizardService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/wizards")
class WizardController(private val wizardService: WizardService) {

    @GetMapping("/me")
    fun getMe(@AuthenticationPrincipal principal: Principal): Wizard {
        return wizardService.getWizard(principal.name.toLong())
    }

    @GetMapping("/{id}")
    fun getWizard(@PathVariable id: Long): Wizard {
        return wizardService.getWizard(id)
    }

    @PutMapping("/join/{guildId}")
    fun joinGuild(
        @AuthenticationPrincipal principal: Principal,
        @PathVariable guildId: Long
    ): Wizard {
        val updatedWizard = wizardService.joinGuild(principal.name.toLong(), guildId)
        return wizardService.getWizard(updatedWizard.id)
    }
}