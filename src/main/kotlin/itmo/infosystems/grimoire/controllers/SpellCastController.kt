package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.dto.requests.SpellCastRequest
import itmo.infosystems.grimoire.models.SpellCast
import itmo.infosystems.grimoire.security.WizardPrincipal
import itmo.infosystems.grimoire.services.SpellCastService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/spells")
class SpellCastController(private val spellCastService: SpellCastService) {

    @PostMapping("/cast")
    fun castSpell(
        @AuthenticationPrincipal principal: WizardPrincipal,
        @RequestBody request: SpellCastRequest
    ): SpellCast {
        return spellCastService.castSpell(principal.id, request)
    }

    @GetMapping("/remove/{id}")
    fun removeSpell(@PathVariable id: Long, @AuthenticationPrincipal principal: WizardPrincipal): SpellCast {
        return spellCastService.removeSpell(principal.id, id)
    }

    @GetMapping("/active/mine")
    fun getMyActiveSpells(@AuthenticationPrincipal principal: WizardPrincipal): List<SpellCast> {
        return spellCastService.getActiveSpells(principal.id)
    }

    @GetMapping("/active/others")
    fun getOthersActiveSpells(@AuthenticationPrincipal principal: WizardPrincipal): List<SpellCast> {
        return spellCastService.getActiveSpellsFromWizardsWithLowerOrEqualGuildLevel(principal.id)
    }
}
