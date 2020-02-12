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
 */

package ca.n4dev.aegaeonnext.core.security

import ca.n4dev.aegaeonnext.core.loggerFor
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *
 * AccessTokenEntryPoint
 *
 * A spring EntryPoint returning directly a json.
 *
 * @author rguillemette
 * @since Feb 09 - 2020
 *
 */
class AccessTokenEntryPoint : AuthenticationEntryPoint {

    private val logger = loggerFor(AccessTokenEntryPoint::class.java)

    override fun commence(request: HttpServletRequest?,
                          response: HttpServletResponse?,
                          authenticationException: AuthenticationException?) {

        logger.warn("AccessTokenEntryPoint#commence")

        response?.let {
            val errorMessage = authenticationException?.message ?: "UNAUTHORIZED"
            it.status = HttpServletResponse.SC_UNAUTHORIZED
            it.contentType = "application/json"
            it.characterEncoding = "UTF-8"
            it.outputStream.println("{\"error\":\"$errorMessage\"}")
        }

    }
}