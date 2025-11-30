package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.dto.requests.SpellCastRequest
import itmo.infosystems.grimoire.models.SpellCast
import itmo.infosystems.grimoire.services.SpellCastService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/spells")
class SpellCastController(private val spellCastService: SpellCastService) {

    @PostMapping("/cast")
    fun castSpell(
        @AuthenticationPrincipal principal: Principal,
        @RequestBody request: SpellCastRequest
    ): SpellCast {
        return spellCastService.castSpell(principal.name.toLong(), request)
    }

    @PutMapping("/remove/{id}")
    fun removeSpell(@PathVariable id: Long, @AuthenticationPrincipal principal: Principal): SpellCast {
        return spellCastService.removeSpell(principal.name.toLong(), id)
    }

    @GetMapping("/active/mine")
    fun getMyActiveSpells(@AuthenticationPrincipal wizardId: String): List<SpellCast> {
        return spellCastService.getActiveSpells(wizardId.toLong())
    }

    @GetMapping("/active/others")
    fun getOthersActiveSpells(@AuthenticationPrincipal wizardId: String): List<SpellCast> {
        return spellCastService.getActiveSpellsFromWizardsWithLowerOrEqualGuildLevel(wizardId.toLong())
    }
}
