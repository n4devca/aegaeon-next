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

package ca.n4dev.aegaeonnext.core.service

import ca.n4dev.aegaeonnext.common.model.AccessToken
import ca.n4dev.aegaeonnext.common.repository.AccessTokenRepository
import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import ca.n4dev.aegaeonnext.core.token.OAuthClient
import ca.n4dev.aegaeonnext.core.token.Token
import ca.n4dev.aegaeonnext.core.token.TokenFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * AccessTokenService
 *
 * Handle access token access and creation.
 *
 * @author rguillemette
 * @since Dec 29 - 2019
 *
 */
@Service
class AccessTokenService(private val accessTokenRepository: AccessTokenRepository,
                         private val clientService: ClientService,
                         private val tokenFactory: TokenFactory) : BaseTokenService() {

    private fun accessTokenToTokenDto(accessToken: AccessToken) =
        TokenDto(
            accessToken.id!!,
            accessToken.token,
            getManagedTokenType(),
            accessToken.clientId,
            accessToken.userId,
            accessToken.scopes,
            accessToken.validUntil
        )

    @Transactional(readOnly = true)
    fun findByToken(pTokenValue: String): TokenDto? =
        accessTokenRepository.getByToken(pTokenValue)?.let { accessToken -> accessTokenToTokenDto(accessToken) }

    fun createToken(userDto: UserDto,
                    scopes: Set<ScopeDto>,
                    payload: Map<String, String>,
                    userDetails: AegaeonUserDetails): TokenDto? {

        val userId = requireNotNull(userDto.id)
        val clientId = requireNotNull(userDetails.id)
        val scopeString = scopes.joinToString(" ") { scopeDto -> scopeDto.code }
        val clientDto = requireNotNull(clientService.getById(clientId))

        val token: Token = tokenFactory.createToken(asOAuthUser(userDto),
                                                    OAuthClient(clientDto.publicId, clientDto.tokenEndpointAuthSigningAlg),
                                                    TokenType.ACCESS_TOKEN,
                                                    clientDto.accessTokenSeconds,
                                                    ChronoUnit.SECONDS,
                                                    payload)

        val accessToken = AccessToken(null, token.value, userId, clientId, scopeString, token.validUntil, Instant.now())
        val accessTokenId = accessTokenRepository.create(accessToken)
        return TokenDto(accessTokenId, token.value, TokenType.ACCESS_TOKEN, clientId, userId, scopeString, token.validUntil)
    }

    override fun getManagedTokenType() = TokenType.ACCESS_TOKEN
}
