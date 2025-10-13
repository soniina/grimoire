package itmo.infosystems.grimoire.dto

data class GuildResponse(
    val name: String,
    val level: Int,
    val spellsPerDayLimit: Int,
    val artifactsInventoryLimit: Int,
    val spellsForArtifact: Int
)