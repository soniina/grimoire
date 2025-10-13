package itmo.infosystems.grimoire.dto.requests

import jakarta.validation.constraints.*

data class RegisterRequest(
    @field:NotNull
    @field:NotBlank
    val name: String? = null,

    @field:NotNull
    @field:NotBlank
    val surname: String? = null,

    @field:Positive
    val guildId: Long? = null,

    @field:NotNull
    @field:NotBlank
    @field:Size(max = 100)
    val login: String? = null,

    @field:NotNull
    @field:NotBlank
    @field:Size(min = 6, max = 100)
    val password: String? = null
)