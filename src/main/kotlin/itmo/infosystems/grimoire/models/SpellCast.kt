package itmo.infosystems.grimoire.models

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "spell_cast")
data class SpellCast (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spell_cast_id")
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "wizard_id")
    val wizard: Wizard,

    @ManyToOne
    @JoinColumn(name = "victim_id")
    val victim: Human,

    @ManyToOne
    @JoinColumn(name = "spell_id")
    val spell: Spell,

    @Column(name = "cast_time")
    val castTime: LocalDate = LocalDate.now(),

    val duration: Int,

    @Enumerated(EnumType.STRING)
    val status: SpellCastStatus
)