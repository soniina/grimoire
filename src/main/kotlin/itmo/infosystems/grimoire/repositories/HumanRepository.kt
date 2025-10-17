package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.Human
import org.springframework.data.jpa.repository.JpaRepository

interface HumanRepository : JpaRepository<Human, Long> {
    fun existsByNameAndSurname(name: String, surname: String): Boolean
}