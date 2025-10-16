package itmo.infosystems.grimoire.repositories

import itmo.infosystems.grimoire.models.Spell
import org.springframework.data.jpa.repository.JpaRepository

interface SpellRepository: JpaRepository<Spell, Long>