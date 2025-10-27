package itmo.infosystems.grimoire.security

import itmo.infosystems.grimoire.services.JwtService
import org.springframework.stereotype.Component

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.net.URLDecoder
import java.security.Principal

@Component
class JwtHandshakeInterceptor(
    private val jwtService: JwtService
) : HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val query = (request as? ServletServerHttpRequest)?.uri?.query
        val token = query?.split("=")?.getOrNull(1)?.let { URLDecoder.decode(it, "UTF-8") }
            ?: return false

        val wizardId = jwtService.getWizardId(token) ?: return false
        attributes["user"] = Principal { wizardId.toString() }
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
    }
}
