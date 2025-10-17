package itmo.infosystems.grimoire.dto.responses

data class HumanResponse(
    val name: String,
    val surname: String,
    val isWizard: Boolean = false
)
