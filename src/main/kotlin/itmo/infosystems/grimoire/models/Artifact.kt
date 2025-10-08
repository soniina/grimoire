package itmo.infosystems.grimoire.models

import jakarta.persistence.*

@Entity
@Table(name = "artifact")
data class Artifact (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artifact_id")
    val id: Long = 0,

    val artifactName: String,

    val rarity: Int
)