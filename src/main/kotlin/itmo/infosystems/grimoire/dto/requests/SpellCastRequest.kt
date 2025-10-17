package itmo.infosystems.grimoire.dto.requests

import jakarta.validation.constraints.*

data class SpellCastRequest(
    @field:NotNull
    @field:Positive
    val victimId: Long? = null,

    @field:NotNull
    @field:Positive
    val spellId: Long? = null,

    @field:NotNull
    @field:Positive
    val duration: Int? = null
)
