package itmo.infosystems.grimoire.models

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "wizard")
data class Wizard(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wizard_id")
    val id: Long = 0,

    @Column(unique = true)
    val login: String,

    val password: String,

    @ManyToOne
    @JoinColumn(name = "guild_id")
    var guild: Guild? = null,

    var lastArtifactAwardTime: LocalDateTime? = null
)
