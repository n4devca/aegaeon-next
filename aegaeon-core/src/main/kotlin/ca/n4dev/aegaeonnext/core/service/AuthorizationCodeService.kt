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

import ca.n4dev.aegaeonnext.common.model.AuthorizationCode
import ca.n4dev.aegaeonnext.common.model.Client
import ca.n4dev.aegaeonnext.common.model.User
import ca.n4dev.aegaeonnext.common.repository.AuthorizationCodeRepository
import ca.n4dev.aegaeonnext.common.repository.ClientRepository
import ca.n4dev.aegaeonnext.common.repository.UserRepository
import ca.n4dev.aegaeonnext.core.token.TokenFactory
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 *
 * AuthorizationCodeService
 *
 * Handle Authorization Code access and creation.
 *
 * @author rguillemette
 * @since 2.0.0 - Dec 21 - 2019
 *
 */
@Service
class AuthorizationCodeService(private val authorizationCodeRepository: AuthorizationCodeRepository,
                               private val userRepository: UserRepository,
                               private val clientRepository: ClientRepository,
                               private val scopeService: ScopeService,
                               private val tokenFactory: TokenFactory) {


    @Transactional
    @PreAuthorize("isAuthenticated() and principal.id == #userId")
    fun create(userId: Long,
               clientId: Long, redirectUrl: String,
               scopes: String, responseType: String,
               nonce: String): AuthorizationCodeDto? {

        val validScopes = scopeService.getValidScopes(scopes)
        val user = userRepository.getUserById(userId)
        val client = clientRepository.getClientById(clientId)
        if (user != null && client != null) {
            val authCodeId = create(user, client, redirectUrl, validScopes, responseType, nonce)
            val authorizationCode = authorizationCodeRepository.getById(authCodeId)
            return authorizationCodeToDto(authorizationCode!!)
        }
        return null;
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or @authorizationCodeService.isCodeOfClient(principal.id, #authCodeId)")
    fun delete(authCodeId: Long) = authorizationCodeRepository.delete(authCodeId)

    @Transactional(readOnly = true)
    fun getByCode(code: String): AuthorizationCodeDto? {
        val authorizationCode = authorizationCodeRepository.getByCode(code)
        if (authorizationCode != null) {
            return authorizationCodeToDto(authorizationCode)
        }
        return null;
    }

    @Transactional(readOnly = true)
    fun isCodeOfClient(clientId: Long?, authCodeId: Long?): Boolean {

        if (authCodeId != null && clientId != null) {
            val code: AuthorizationCode? = authorizationCodeRepository.getById(authCodeId)
            if (code != null && code.clientId.equals(clientId)) {
                return true
            }
        }

        return false
    }

    private fun create(user: User,
                       client: Client,
                       redirectUrl: String,
                       scopes: Set<ScopeDto>,
                       responseType: String,
                       nonce: String): Long {

        val validUntil = Instant.now().plus(3L, ChronoUnit.MINUTES)
        val scopesAsString = scopes.joinToString(separator = " ") { scopeDto -> scopeDto.code }
        val clientId = requireNotNull(client.id)
        val userId = requireNotNull(user.id)

        val authorizationCode = AuthorizationCode(null,
                                                  tokenFactory.uniqueCode(),
                                                  clientId,
                                                  userId,
                                                  validUntil,
                                                  scopesAsString,
                                                  redirectUrl,
                                                  responseType,
                                                  nonce,
                                                  Instant.now())

        return authorizationCodeRepository.create(authorizationCode)
    }


    private val authorizationCodeToDto = { authorizationCode: AuthorizationCode ->

        AuthorizationCodeDto(authorizationCode.id!!,
                             authorizationCode.code,
                             authorizationCode.validUntil,
                             authorizationCode.clientId,
                             authorizationCode.userId,
                             authorizationCode.nonce,
                             authorizationCode.redirectUrl,
                             scopeService.getValidScopes(authorizationCode.scopes))
    }
}


data class AuthorizationCodeDto(
    val id: Long,
    val code: String,
    val validUntil: Instant,
    val clientId: Long,
    val userId: Long,
    val nonce: String,
    val redirectUrl: String,
    val scopes: Set<ScopeDto>)
