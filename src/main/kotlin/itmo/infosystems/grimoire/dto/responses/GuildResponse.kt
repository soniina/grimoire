package itmo.infosystems.grimoire.dto.responses

data class GuildResponse(
    val name: String,
    val level: Int,
    val spellsPerDayLimit: Int,
    val artifactsInventoryLimit: Int,
    val spellsForArtifact: Int
)
