package itmo.infosystems.grimoire.models

import jakarta.persistence.*

@Entity
@Table(name = "artifact")
data class Artifact(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artifact_id")
    val id: Long = 0,

    val name: String,

    val rarity: Int
)