package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.models.Spell
import itmo.infosystems.grimoire.security.WizardPrincipal
import itmo.infosystems.grimoire.services.SpellService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class SpellBookController(private val spellService: SpellService) {
    @GetMapping
    fun getSpellBook(pageable: Pageable): Page<Spell> {
        return spellService.getAll(pageable)
    }

    @GetMapping("/my-spellbook")
    fun getMySpellBook(
        @AuthenticationPrincipal principal: WizardPrincipal,
        pageable: Pageable
    ): Page<Spell> {
        return spellService.getSpellBook(principal.id, pageable)
    }
}