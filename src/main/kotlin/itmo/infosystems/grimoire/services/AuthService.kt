package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.dto.requests.RegisterRequest
import itmo.infosystems.grimoire.dto.responses.AuthResponse
import itmo.infosystems.grimoire.dto.requests.LoginRequest
import itmo.infosystems.grimoire.models.Human
import itmo.infosystems.grimoire.models.Wizard
import itmo.infosystems.grimoire.repositories.GuildRepository
import itmo.infosystems.grimoire.repositories.HumanRepository
import itmo.infosystems.grimoire.repositories.WizardRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val humanRepository: HumanRepository,
    private val wizardRepository: WizardRepository,
    private val guildRepository: GuildRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtService: JwtService,
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        if (wizardRepository.existsByLogin(requireNotNull(request.login)))
            throw IllegalArgumentException("Login already exists")

        val wizard = wizardRepository.save(Wizard(
            login = request.login,
            password = passwordEncoder.encode(requireNotNull(request.password)),
            guild = request.guildId?.let { guildRepository.findByIdOrNull(it) }
        ))

        humanRepository.save(
            Human(
                name = requireNotNull(request.name),
                surname = requireNotNull(request.surname),
                wizard = wizard
            )
        )

        return AuthResponse(jwtService.generateToken(wizard.id, wizard.login))
    }

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val wizard = wizardRepository.findByLogin(requireNotNull(request.login))
            ?: throw IllegalArgumentException("Invalid login or password")

        if (!passwordEncoder.matches(
                request.password,
                wizard.password
            )
        ) throw IllegalArgumentException("Invalid login or password")

        return AuthResponse(jwtService.generateToken(wizard.id, wizard.login))
    }
}