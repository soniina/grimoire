package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.repositories.SpellCastRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
class SpellExpirationService(private val spellCastRepository: SpellCastRepository) {

    @Scheduled(fixedRate = 30_000)
    @Transactional
    fun expireSpells() = spellCastRepository.expireSpells()
}