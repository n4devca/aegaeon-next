package ca.n4dev.aegaeonnext.event

import org.springframework.context.ApplicationEvent

/**
 *
 * UserInfoEvent.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 24 - 2019
 *
 */
data class UserInfoEvent(
    val source: String,
    val clientId: Long,
    val userId: Long,
    val scopes: Set<String>) : ApplicationEvent(source)