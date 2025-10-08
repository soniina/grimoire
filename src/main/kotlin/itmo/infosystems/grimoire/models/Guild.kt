package itmo.infosystems.grimoire.models

import jakarta.persistence.*

@Entity
@Table(name = "guilds")
data class Guild (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guild_id")
    val id: Long = 0,

    val level: Int,

    val spellsPerDayLimit: Int,

    val artifactsInventoryLimit: Int,

    val guildName: String,

    val spellsForArtifact: Int
)