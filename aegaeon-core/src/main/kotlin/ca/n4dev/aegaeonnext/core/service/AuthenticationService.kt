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
package ca.n4dev.aegaeonnext.core.service

import ca.n4dev.aegaeonnext.common.utils.isAfterNow
import ca.n4dev.aegaeonnext.core.loggerFor
import ca.n4dev.aegaeonnext.core.security.AccessTokenAuthentication
import ca.n4dev.aegaeonnext.core.security.AccessTokenAuthenticationException
import ca.n4dev.aegaeonnext.core.token.OAuthClient
import ca.n4dev.aegaeonnext.core.token.TokenFactory
import com.nimbusds.jwt.SignedJWT
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.text.ParseException

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
                            private val clientService: ClientService,
                            private val userService: UserService,
                            private val scopeService: ScopeService,
                            private val tokenFactory: TokenFactory) {

    private val LOGGER = loggerFor(javaClass)

    @Transactional(readOnly = true)
    fun authenticate(pAccessToken: String?): Authentication {

        try {
            val tokenStr = pAccessToken ?: "-"

            if (!pAccessToken.isNullOrBlank()) {

                // Should be parseable
                SignedJWT.parse(pAccessToken)

                // Now Get it
                var accessToken: TokenDto = this.accessTokenService.findByToken(pAccessToken)
                    ?: throw AuthenticationCredentialsNotFoundException("This access token is invalid or has been revoked.")

                // Still Valid ?
                if (!isAfterNow(accessToken.validUntil)) {
                    throw CredentialsExpiredException("This access token is expired.")
                }

                // Get client
                val clientDto = clientService.getById(accessToken.clientId)
                    ?: throw CredentialsExpiredException("This client is invalid.")

                // Validate
                if (!this.tokenFactory.validate(OAuthClient(clientDto.publicId, clientDto.tokenEndpointAuthSigningAlg), pAccessToken)) {
                    throw AccessTokenAuthenticationException("The JWT is not valid")
                }

                val user: UserDto = userService.getUserById(accessToken.userId)
                    ?: throw CredentialsExpiredException("This user is invalid.")

                val roles: List<UserAuthorityDto> = userService.getUserAuthoritiesByUserId(user.id)

                return AccessTokenAuthentication(
                    pAccessToken,
                    user.id,
                    user.uniqueIdentifier,
                    roles,
                    scopeService.getValidScopes(accessToken.scopes))
            }


        } catch (pe: ParseException) {
            LOGGER.warn("AccessTokenAuthenticationProvider#authenticate: unable to parse as JWT")
            throw AccessTokenAuthenticationException("Error parsing JWT token")
        } catch (authException : AuthenticationException) {
            LOGGER.warn("AuthenticationException", authException)
            throw authException;
        } catch (e: Exception) {
            LOGGER.info("AccessTokenAuthenticationProvider#authenticate: Error checking JWT token", e)
        }


        throw AccessTokenAuthenticationException("Error checking JWT token")
    }

}