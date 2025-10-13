package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.dto.requests.HumanRequest
import itmo.infosystems.grimoire.models.Human
import itmo.infosystems.grimoire.services.VictimService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/victims")
class VictimController(private val victimService: VictimService) {

    @GetMapping
    fun getAllVictims(): List<Human> {
        return victimService.getAll()
    }

    @PostMapping
    fun createVictim(@RequestBody @Valid request: HumanRequest): Human {
        return victimService.create(request)
    }
}