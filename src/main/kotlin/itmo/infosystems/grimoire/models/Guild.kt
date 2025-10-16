package itmo.infosystems.grimoire.models

import jakarta.persistence.*

@Entity
@Table(name = "guild")
data class Guild(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guild_id")
    val id: Long = 0,

    val name: String,

    val level: Int,

    val spellsPerDayLimit: Int,

    val artifactsInventoryLimit: Int,

    val spellsForArtifact: Int
)