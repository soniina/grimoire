package itmo.infosystems.grimoire.models

import jakarta.persistence.*

@Entity
@Table(name = "spell")
data class Spell(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spell_id")
    val id: Long = 0,

    @Column(unique = true)
    val name: String,

    @Enumerated(EnumType.STRING)
    val type: SpellType,

    val description: String? = null,

    val requiredGuildLevel: Int,

    @Enumerated(EnumType.STRING)
    val victimType: VictimType
)