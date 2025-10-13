package itmo.infosystems.grimoire.dto.responses

data class WizardResponse(
    val login: String,
    val guild: GuildResponse?,
    val human: HumanResponse
) {
    constructor(
        login: String,
        guildName: String?,
        guildLevel: Int?,
        guildSpellsPerDayLimit: Int?,
        guildArtifactsInventoryLimit: Int?,
        guildSpellsForArtifact: Int?,
        humanName: String,
        humanSurname: String
    ) : this(
        login = login,
        guild = guildName?.let {
            GuildResponse(
                name = guildName,
                level = guildLevel ?: 0,
                spellsPerDayLimit = guildSpellsPerDayLimit ?: 0,
                artifactsInventoryLimit = guildArtifactsInventoryLimit ?: 0,
                spellsForArtifact = guildSpellsForArtifact ?: 0
            )
        },
        human = HumanResponse(
            name = humanName,
            surname = humanSurname
        )
    )
}
