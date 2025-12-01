package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.Human
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface HumanRepository : JpaRepository<Human, Long> {
    fun existsByNameAndSurname(name: String, surname: String): Boolean

    @Query(
        value = "SELECT * FROM available_victims(:wizardId)",
        nativeQuery = true
    )
    fun findAvailable(@Param("wizardId") wizardId: Long): List<Human>
}