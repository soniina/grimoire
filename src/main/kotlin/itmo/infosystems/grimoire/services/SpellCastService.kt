package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.exceptions.SpellCastException
import itmo.infosystems.grimoire.dto.requests.SpellCastRequest
import itmo.infosystems.grimoire.models.SpellCast
import itmo.infosystems.grimoire.models.SpellCastStatus
import itmo.infosystems.grimoire.repositories.HumanRepository
import itmo.infosystems.grimoire.repositories.SpellCastRepository
import itmo.infosystems.grimoire.repositories.SpellRepository
import itmo.infosystems.grimoire.repositories.WizardRepository
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class SpellCastService(
    private val spellCastRepository: SpellCastRepository,
    private val wizardRepository: WizardRepository,
    private val humanRepository: HumanRepository,
    private val spellRepository: SpellRepository
) {

    @Transactional
    fun castSpell(wizardId: Long, request: SpellCastRequest): SpellCast {
        val wizard = wizardRepository.findById(wizardId)
            .orElseThrow { SpellCastException("Wizard not found with id: $wizardId") }
        val victim = humanRepository.findById(requireNotNull(request.victimId))
            .orElseThrow { SpellCastException("Victim not found with id: ${request.victimId}") }
        val spell = spellRepository.findById(requireNotNull(request.spellId))
            .orElseThrow { SpellCastException("Spell not found with id: ${request.spellId}") }

        return try {
            spellCastRepository.save(
                SpellCast(
                    wizard = wizard,
                    victim = victim,
                    spell = spell,
                    expireTime = request.expireTime
                )
            )
        } catch (ex: DataAccessException) {
            val message = ex.message ?: "Неизвестная ошибка"
            throw SpellCastException(message)
        }
    }


    @Transactional
    fun removeSpell(wizardId: Long, spellCastId: Long): SpellCast {
        val wizard = wizardRepository.findById(wizardId)
            .orElseThrow { SpellCastException("Wizard not found with id: $wizardId") }
        val spellCast = spellCastRepository.findById(spellCastId)
            .orElseThrow { SpellCastException("SpellCast not found with id: $spellCastId") }

        return try {
            spellCast.removedByWizard = wizard
            spellCast.status = SpellCastStatus.REMOVED

            spellCastRepository.save(spellCast)
        } catch (ex: DataAccessException) {
            val message = ex.message ?: "Неизвестная ошибка"
            throw SpellCastException(message)
        }
    }

    fun getActiveSpells(wizardId: Long) = spellCastRepository.findByWizardIdAndStatus(wizardId, SpellCastStatus.ACTIVE)

    fun getActiveSpellsFromWizardsWithLowerOrEqualGuildLevel(wizardId: Long): List<SpellCast> {
        val wizard = wizardRepository.findById(wizardId)
            .orElseThrow { SpellCastException("Wizard not found with id: $wizardId") }
        return spellCastRepository.findActiveSpellsFromWizardsWithLowerOrEqualGuildLevel(
            wizard.guild?.level ?: 0,
            wizardId
        )
    }
}
