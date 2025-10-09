package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.Guild
import org.springframework.data.jpa.repository.JpaRepository

interface GuildRepository: JpaRepository<Guild, Long>