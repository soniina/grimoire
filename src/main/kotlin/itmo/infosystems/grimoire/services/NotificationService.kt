package itmo.infosystems.grimoire.services


import itmo.infosystems.grimoire.dto.responses.NotificationMessage
import itmo.infosystems.grimoire.dto.responses.NotificationType
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.postgresql.PGConnection
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import java.sql.Connection
import java.time.Duration
import javax.sql.DataSource


@Service
class NotificationService(
    private val dataSource: DataSource,
    private val messagingTemplate: SimpMessagingTemplate
) {
    private var connection: Connection? = null
    private var pgConn: PGConnection? = null
    private val disposable = Flux.interval(Duration.ofMillis(500))
        .publishOn(Schedulers.boundedElastic())
        .subscribe {
            val notifications = pgConn?.notifications ?: return@subscribe
            notifications.forEach { notification ->
                val wizardId = notification.parameter?.toString() ?: return@forEach
                when (notification.name) {
                    "artifact_awarded" ->
                        sendToUser(
                            wizardId,
                            NotificationMessage(
                                NotificationType.ARTIFACT_AWARDED,
                                "You have been awarded an artifact!"
                            )
                        )

                    "guild_upgrade_available" ->
                        sendToUser(
                            wizardId,
                            NotificationMessage(
                                NotificationType.GUILD_UPGRADE_AVAILABLE,
                                "Your guild can now be upgraded!"
                            )
                        )
                }
            }
        }

    private fun sendToUser(wizardId: String, message: NotificationMessage) {
        messagingTemplate.convertAndSendToUser(
            wizardId,
            "/queue/notifications",
            message
        )
    }

    @PostConstruct
    fun listenToNotifications() {
        connection = dataSource.connection
        pgConn = connection?.unwrap(PGConnection::class.java)

        connection?.createStatement()?.use { stmt ->
            stmt.execute("LISTEN artifact_awarded")
            stmt.execute("LISTEN guild_upgrade_available")
        }
    }

    @PreDestroy
    fun cleanup() {
        disposable.dispose()
        connection?.close()
    }

}
