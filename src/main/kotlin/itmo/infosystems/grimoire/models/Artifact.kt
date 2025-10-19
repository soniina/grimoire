package itmo.infosystems.grimoire.models

import jakarta.persistence.*
import org.hibernate.annotations.Check

@Entity
@Table(name = "artifact")
@Check(constraints = "rarity >= 0 AND rarity <= 100")
data class Artifact(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artifact_id")
    val id: Long = 0,

    val name: String,

    val rarity: Int
)