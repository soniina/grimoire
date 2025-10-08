package itmo.infosystems.grimoire.models

import jakarta.persistence.*
import org.hibernate.annotations.Immutable

@Entity
@Table(name = "spell_book")
@Immutable
data class SpellBook (
    @Id
    val spellBookId: Long,
    val wizardId: Long,
    val spellId: Long
)