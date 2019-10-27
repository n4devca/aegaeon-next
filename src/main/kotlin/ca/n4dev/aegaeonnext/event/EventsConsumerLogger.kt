package ca.n4dev.aegaeonnext.event

import ca.n4dev.aegaeonnext.utils.asString
import ca.n4dev.aegaeonnext.utils.join
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

/**
 *
 * EventsConsumerLogger.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 24 - 2019
 *
 */
@Component
class EventsConsumerLogger {

    private val AUTHENTICATION_LOGGER = LoggerFactory.getLogger(AuthenticationEvent::class.java)
    private val INTROSPECT_LOGGER = LoggerFactory.getLogger(IntrospectEvent::class.java)
    private val USER_INFO_LOGGER = LoggerFactory.getLogger(UserInfoEvent::class.java)
    private val TOKENGRANT_LOGGER = LoggerFactory.getLogger(TokenGrantEvent::class.java)

    @Async
    @EventListener
    open fun logUserInfoEvent(pUserInfoEvent: UserInfoEvent) {
        try {

            val clientId = asString(pUserInfoEvent.clientId)
            val scopes = "[" + pUserInfoEvent.scopes.joinToString() + "]"
            val userId = asString(pUserInfoEvent.userId)

            MDC.put("clientId", clientId)
            MDC.put("scopes", scopes)
            MDC.put("userId", userId)

            USER_INFO_LOGGER.info(join(clientId, scopes, userId))

        } finally {
            MDC.clear()
        }
    }
}