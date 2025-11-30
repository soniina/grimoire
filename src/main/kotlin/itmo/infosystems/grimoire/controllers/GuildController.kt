package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.services.GuildService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.security.core.annotation.AuthenticationPrincipal
import itmo.infosystems.grimoire.models.Guild

@RestController
@RequestMapping("/guilds")
class GuildController(private val guildService: GuildService) {

    @GetMapping("/available")
    fun getAvailableGuilds(@AuthenticationPrincipal wizardId: String): List<Guild> {
        return guildService.getAvailableGuilds(wizardId.toLong())
    }

}
