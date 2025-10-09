package itmo.infosystems.grimoire.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JwtService {

    @Value("\${jwt.secret}")
    private lateinit var secretKey: String

    @Value("\${jwt.expiration}")
    private var expiration: Long = 86400000

    private fun getSigningKey(): SecretKey {
        return Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    fun generateToken(id: Long, login: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .setSubject(login)
            .claim("id", id)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()
    }

    fun getLoginFromToken(token: String): String? {
        return getClaimsFromToken(token)?.subject
    }

    fun getWizardIdFromToken(token: String): Long? {
        return getClaimsFromToken(token)?.get("id", Long::class.java)
    }

    fun validateToken(token: String): Boolean {
        return try {
            getClaimsFromToken(token)?.expiration?.after(Date()) ?: false
        } catch (ex: Exception) {
            false
        }
    }

    private fun getClaimsFromToken(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .body
        } catch (ex: Exception) {
            null
        }
    }
}