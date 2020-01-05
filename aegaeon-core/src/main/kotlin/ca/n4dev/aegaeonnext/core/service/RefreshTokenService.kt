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

import ca.n4dev.aegaeonnext.common.model.RefreshToken
import ca.n4dev.aegaeonnext.common.repository.RefreshTokenRepository
import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import ca.n4dev.aegaeonnext.core.token.TokenFactory
import org.springframework.stereotype.Service
import java.time.Instant

/**
 *
 * RefreshTokenService
 *
 * Handle refresh token access and creation.
 *
 * @author rguillemette
 * @since Dec 29 - 2019
 *
 */
@Service
class RefreshTokenService(private val refreshTokenRepository: RefreshTokenRepository,
                          private val clientService: ClientService,
                          private val tokenFactory: TokenFactory) : BaseTokenService() {


    fun createToken(userDto: UserDto, scopes: Set<ScopeDto>, userDetails: AegaeonUserDetails): TokenDto? {

        if (containsOfflineScope(scopes)) {

            val userId = requireNotNull(userDto.id)
            val clientId = requireNotNull(userDetails.id)
            val clientDto = requireNotNull(clientService.getById(clientId))

            val scopeString = scopes.joinToString(" ") { scopeDto ->
                scopeDto.code
            }

            val validUntil = Instant.now().plusSeconds(clientDto.refreshTokenSeconds)
            val tokenValue = tokenFactory.uniqueCode()
            val refreshToken = RefreshToken(null,
                                            tokenValue,
                                            userId,
                                            clientId,
                                            scopeString,
                                            validUntil,
                                            Instant.now())

            val tokenId = refreshTokenRepository.create(refreshToken)

            return TokenDto(tokenId, tokenValue, TokenType.REFRESH_TOKEN, scopeString, validUntil)
        }

        return null
    }

    private fun containsOfflineScope(scopes: Set<ScopeDto>): Boolean {
        return scopes.any { scopeDto -> scopeDto.code == OFFLINE_SCOPE }
    }

    override fun getManagedTokenType() = TokenType.REFRESH_TOKEN
}