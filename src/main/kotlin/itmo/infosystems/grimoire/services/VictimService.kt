package itmo.infosystems.grimoire.services

import itmo.infosystems.grimoire.dto.requests.HumanRequest
import itmo.infosystems.grimoire.models.Human
import itmo.infosystems.grimoire.repositories.HumanRepository
import org.springframework.stereotype.Service

@Service
class VictimService(private val humanRepository: HumanRepository) {

    fun getAll(): List<Human> = humanRepository.findAll()

    fun create(request: HumanRequest): Human {
        if (humanRepository.existsByNameAndSurname(requireNotNull(request.name), requireNotNull(request.surname)))
            throw IllegalArgumentException("Victim with the same name and surname already exists")

        return humanRepository.save(
            Human(
                name = requireNotNull(request.name),
                surname = requireNotNull(request.surname),
                isAlive = request.isAlive,
            )
        )
    }

}