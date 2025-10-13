package itmo.infosystems.grimoire.models

import jakarta.persistence.*


@Entity
@Table(name = "human")
data class Human (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "human_id")
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "wizard_id")
    val wizard: Wizard? = null,

    val name: String,

    val surname: String,

    val isAlive: Boolean = true
)