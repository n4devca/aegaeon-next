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

import ca.n4dev.aegaeonnext.common.model.IdToken
import ca.n4dev.aegaeonnext.common.repository.IdTokenRepository
import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import ca.n4dev.aegaeonnext.core.token.OAuthClient
import ca.n4dev.aegaeonnext.core.token.Token
import ca.n4dev.aegaeonnext.core.token.TokenFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 *
 * IdTokenService
 *
 * Handle id token access and creation.
 *
 * @author rguillemette
 * @since Dec 29 - 2019
 *
 */
@Service
class IdTokenService(private val idTokenRepository: IdTokenRepository,
                     private val clientService: ClientService,
                     private val tokenFactory: TokenFactory) : BaseTokenService() {


    // TODO(RG): dont forget nonce

    @Transactional
    @PreAuthorize("isAuthenticated() and #userDetails.id == principal.id")
    fun createToken(userDto: UserDto,
                    scopes: Set<ScopeDto>,
                    nonce: String?,
                    payload: Map<String, String>,
                    userDetails: AegaeonUserDetails): TokenDto? {

        if (containsOpenIdScope(scopes)) {

            val userId = requireNotNull(userDto.id)
            val clientId = requireNotNull(userDetails.id)
            val scopeString = scopes.joinToString(" ") { scopeDto -> scopeDto.code }
            val clientDto = requireNotNull(clientService.getById(clientId))

            val payloadMap = payload.toMutableMap()
            nonce?.apply { payloadMap["nonce"] = this }

            val token: Token = tokenFactory.createToken(asOAuthUser(userDto),
                                                        OAuthClient(clientDto.publicId, clientDto.idTokenSignedResponseAlg),
                                                        TokenType.ID_TOKEN,
                                                        clientDto.idTokenSeconds,
                                                        ChronoUnit.SECONDS,
                                                        payloadMap)

            val idToken = IdToken(null,
                                  token.value,
                                  userId,
                                  clientId,
                                  scopeString,
                                  token.validUntil,
                                  Instant.now())
            val idTokenId: Long = idTokenRepository.create(idToken)

            return TokenDto(idTokenId, token.value, TokenType.ID_TOKEN, clientId, userId, scopeString, token.validUntil)
        }

        return null;
    }

    override fun getManagedTokenType() = TokenType.ID_TOKEN

    private fun containsOpenIdScope(scopes: Set<ScopeDto>): Boolean {
        return scopes.any { scopeDto -> scopeDto.code == OPENID_SCOPE }
    }

}