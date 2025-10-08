package itmo.infosystems.grimoire.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "spell_log")
data class SpellLog (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spell_log_id")
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "wizard_id")
    val wizard: Wizard,

    val eventTime: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    val eventType: EventType
)