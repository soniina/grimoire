package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.Wizard
import org.springframework.data.jpa.repository.JpaRepository

interface WizardRepository : JpaRepository<Wizard, Long> {
    fun existsByLogin(login: String): Boolean
    fun findByLogin(login: String): Wizard?
}