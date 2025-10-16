package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.models.Spell
import itmo.infosystems.grimoire.services.SpellService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/spells")
class SpellController(private val spellService: SpellService) {

    @GetMapping
    fun getSpells(pageable: Pageable): Page<Spell> {
        return spellService.getAll(pageable)
    }
}