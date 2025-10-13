package itmo.infosystems.grimoire.dto.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class HumanRequest(
    @field:NotNull
    @field:NotBlank
    val name: String? = null,

    @field:NotNull
    @field:NotBlank
    val surname: String? = null,

    @field:NotNull
    val isAlive: Boolean = true
)