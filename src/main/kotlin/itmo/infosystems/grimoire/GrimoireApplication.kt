package itmo.infosystems.grimoire

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class GrimoireApplication

fun main(args: Array<String>) {
    runApplication<GrimoireApplication>(*args)
}
