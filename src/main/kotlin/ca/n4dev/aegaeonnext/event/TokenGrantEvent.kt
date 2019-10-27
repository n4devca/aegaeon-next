package ca.n4dev.aegaeonnext.event

import org.springframework.context.ApplicationEvent

/**
 *
 * TokenGrantEvent.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 24 - 2019
 *
 */
data class TokenGrantEvent(
    val source: String,
    val clientId: String,
    val requestedScope: String,
    val allowedScope: String,
    val code: String?,
    val userId: String,
    val grantType: String,
    val idToken: Boolean = false,
    val accessToken: Boolean = false,
    val refreshToken: Boolean = false

) : ApplicationEvent(source)