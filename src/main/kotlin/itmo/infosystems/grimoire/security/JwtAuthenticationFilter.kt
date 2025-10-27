package itmo.infosystems.grimoire.security

import itmo.infosystems.grimoire.services.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(private val jwtService: JwtService) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization") ?: return filterChain.doFilter(request, response)
        if (!authHeader.startsWith("Bearer ")) return filterChain.doFilter(request, response)

        val token = authHeader.substring(7)
        if (jwtService.validateToken(token)) {
            val wizardId = jwtService.getWizardId(token) ?: filterChain.doFilter(request, response)

            val authentication = UsernamePasswordAuthenticationToken(
                wizardId.toString(), null, emptyList()
            ).apply {
                details = WebAuthenticationDetailsSource().buildDetails(request)
            }
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}