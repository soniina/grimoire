package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.Spell
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SpellRepository : JpaRepository<Spell, Long> {

    @Query(
        value = """
            SELECT *
            FROM get_spellbook(:wizardId)
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM get_spellbook(:wizardId)
        """,
        nativeQuery = true
    )
    fun findSpellBook(wizardId: Long, pageable: Pageable): Page<Spell>
}