package ca.n4dev.aegaeonnext.event

import org.springframework.context.ApplicationEvent

/**
 *
 * IntrospectEvent.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 24 - 2019
 *
 */
data class IntrospectEvent (
    val source: String,
    val userId: String,
    val clientId: String,
    val clientAllowed: Boolean,
    val valueReturned: String) : ApplicationEvent(source)