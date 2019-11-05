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
package ca.n4dev.aegaeonnext.service

import ca.n4dev.aegaeonnext.loggerFor
import ca.n4dev.aegaeonnext.model.entities.AccessToken
import ca.n4dev.aegaeonnext.model.mapper.UserMapper
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * AuthenticationService.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 29 - 2019
 *
 */

@Service
class AuthenticationService(private val accessTokenService: AccessTokenService,
                            private val scopeService: ScopeService,
                            private val tokenFactory: TokenFactory,
                            private val userMapper: UserMapper) {

    private val LOGGER = loggerFor(javaClass)

    @Transactional(readOnly = true)
    fun authenticate(pAccessToken: String?): Authentication {

        var accessToken: AccessToken? = null
        val tokenStr = pAccessToken ?: "-"

        try {
            if (!pAccessToken.isNullOrBlank()) {

                // Should be parseable
                SignedJWT.parse(pAccessToken)

                // Now Get it
                accessToken = this.accessTokenService.findByToken(pAccessToken)
            }

            // Exists ?
            if (accessToken == null) {
                throw AuthenticationCredentialsNotFoundException("$tokenStr is invalid or has been revoked.")
            }

            // Still Valid ?
            if (!Utils.isAfterNow(accessToken!!.getValidUntil())) {
                throw AuthenticationCredentialsNotFoundException("$tokenStr is expired.")
            }

            // Validate
            if (!this.tokenFactory.validate(accessToken!!.getClient(), pAccessToken)) {
                throw AccessTokenAuthenticationException("The JWT is not valid")
            }

            val u = accessToken!!.getUser()
            val roles = MutableList<String>()

            if (u.getAuthorities() != null) {
                u.getAuthorities().forEach { a -> roles.add(a.getCode()) }
            }
            val uv = this.userMapper.toDto(u)

            return AccessTokenAuthentication(
                uv,
                pAccessToken,
                scopeService.getValidScopes(accessToken!!.getScopes()),
                roles)

        } catch (pe: ParseException) {
            LOGGER.info("AccessTokenAuthenticationProvider#authenticate: unable to parse as JWT")
            throw AccessTokenAuthenticationException("AccessTokenAuthenticationProvider#authenticate: unable to parse as JWT")
        } catch (e: Exception) {
            LOGGER.info("AccessTokenAuthenticationProvider#authenticate: Error checking JWT token")
            throw AccessTokenAuthenticationException("Error checking JWT token")
        }

    }

}