package itmo.infosystems.grimoire.dto.responses

data class NotificationMessage(
    val type: NotificationType,
    val message: String
)

enum class NotificationType {
    ARTIFACT_AWARDED,
    GUILD_UPGRADE_AVAILABLE
}