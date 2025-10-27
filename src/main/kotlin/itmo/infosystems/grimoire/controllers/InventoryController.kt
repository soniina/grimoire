package itmo.infosystems.grimoire.controllers

import itmo.infosystems.grimoire.models.Artifact
import itmo.infosystems.grimoire.services.InventoryService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/inventory")
class InventoryController(private val inventoryService: InventoryService) {

    @GetMapping
    fun getInventory(@AuthenticationPrincipal principal: Principal, pageable: Pageable): Page<Artifact> {
        return inventoryService.getInventory(principal.name.toLong(), pageable)
    }
}