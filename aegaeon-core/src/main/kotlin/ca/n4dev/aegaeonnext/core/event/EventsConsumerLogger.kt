/*
 * Copyright 2019 Remi Guillemette - n4dev.ca
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package ca.n4dev.aegaeonnext.core.event

import ca.n4dev.aegaeonnext.common.utils.asString
import ca.n4dev.aegaeonnext.common.utils.join
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