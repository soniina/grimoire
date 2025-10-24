package itmo.infosystems.grimoire.dto.requests

import jakarta.validation.constraints.*
import java.time.LocalDateTime

data class SpellCastRequest(
    @field:NotNull
    @field:Positive
    val victimId: Long? = null,

    @field:NotNull
    @field:Positive
    val spellId: Long? = null,

    val expireTime: LocalDateTime? = null
)
