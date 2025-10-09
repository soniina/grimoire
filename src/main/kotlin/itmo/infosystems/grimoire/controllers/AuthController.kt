package itmo.infosystems.grimoire.controllers

import RegisterRequest
import itmo.infosystems.grimoire.dto.*
import itmo.infosystems.grimoire.services.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: RegisterRequest): AuthResponse {
        return authService.register(request)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse {
        return authService.login(request)
    }
}