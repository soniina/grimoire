package itmo.infosystems.grimoire.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "spell_cast")
data class SpellCast(
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

    val castTime: LocalDateTime = LocalDateTime.now(),

    val expireTime: LocalDateTime? = null,

    @ManyToOne
    @JoinColumn(name = "removed_by_wizard_id")
    var removedByWizard: Wizard? = null,

    @Enumerated(EnumType.STRING)
    var status: SpellCastStatus = SpellCastStatus.ACTIVE
)