package itmo.infosystems.grimoire.models

import jakarta.persistence.*

@Entity
@Table(name = "spell")
data class Spell (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spell_id")
    val id: Long = 0,

    @Column(unique = true)
    val spellName: String,

    @Enumerated(EnumType.STRING)
    val spellType: SpellType,

    val spellDescription: String? = null,

    val requiredGuildLevel: Int,

    @Enumerated(EnumType.STRING)
    val victimType: VictimType
)