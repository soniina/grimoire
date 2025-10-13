package itmo.infosystems.grimoire.dto.requests

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class LoginRequest(
    @field:NotNull
    @field:NotBlank
    @field:Size(max = 100)
    val login: String? = null,

    @field:NotNull
    @field:NotBlank
    @field:Size(min = 6, max = 100)
    val password: String? = null
)
