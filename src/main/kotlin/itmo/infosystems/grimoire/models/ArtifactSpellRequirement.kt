package itmo.infosystems.grimoire.models

import jakarta.persistence.*

@Entity
@Table(name = "artifact_spell_requirement")
data class ArtifactSpellRequirement (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artifact_spell_requirement_id")
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "artifact_id")
    val artifact: Artifact,

    @ManyToOne
    @JoinColumn(name = "spell_id")
    val spell: Spell,

    @Enumerated(EnumType.STRING)
    val spellUsageType: SpellUsageType
)