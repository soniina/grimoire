package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.models.Spell
import itmo.infosystems.grimoire.repositories.SpellRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SpellService(private val spellRepository: SpellRepository) {
    fun getAll(pageable: Pageable): Page<Spell> = spellRepository.findAll(pageable)
    fun getSpellBook(wizardId: Long, pageable: Pageable) =
        spellRepository.findSpellBook(wizardId, pageable)
}