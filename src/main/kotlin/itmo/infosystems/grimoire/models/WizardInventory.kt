package itmo.infosystems.grimoire.models

import jakarta.persistence.*

@Entity
@Table(
    name = "wizard_inventory",
    uniqueConstraints = [UniqueConstraint(columnNames = ["wizard_id", "artifact_id"])]
)
data class WizardInventory (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wizard_inventory_id")
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "wizard_id")
    val wizard: Wizard,

    @ManyToOne
    @JoinColumn(name = "artifact_id")
    val artifact: Artifact
)
