package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.Guild
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface GuildRepository : JpaRepository<Guild, Long> {
    @Query(
        value = "SELECT * FROM available_guilds(:wizardId)",
        nativeQuery = true
    )
    fun findAvailableGuilds(@Param("wizardId") wizardId: Long): List<Guild>
}
