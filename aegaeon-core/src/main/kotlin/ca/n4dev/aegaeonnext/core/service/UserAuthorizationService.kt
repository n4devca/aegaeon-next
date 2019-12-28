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

import ca.n4dev.aegaeonnext.common.model.UserAuthorization
import ca.n4dev.aegaeonnext.common.repository.UserAuthorizationRepository
import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * UserAuthorizationService.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 06 - 2019
 *
 */
@Service
class UserAuthorizationService(private val userAuthorizationRepository: UserAuthorizationRepository,
                               private val clientService: ClientService,
                               private val userService: UserService,
                               private val scopeService: ScopeService) {


    @Transactional(readOnly = true)
    fun isAuthorized(authentication: Authentication?,
                     clientPublicId: String?,
                     clientRedirectionUrl: String?,
                     rawScopeParam: String): Boolean {

        return isAuthorized(authentication,
                            clientPublicId,
                            clientRedirectionUrl,
                            scopeService.validate(rawScopeParam).validScopes)
    }

    @Transactional(readOnly = true)
    fun isAuthorized(authentication: Authentication?,
                     clientPublicId: String?,
                     clientRedirectionUrl: String?,
                     requestedScopes: Set<ScopeDto>): Boolean {

        if (!clientPublicId.isNullOrBlank() && !clientRedirectionUrl.isNullOrBlank()
            && authentication != null
            && authentication.principal is AegaeonUserDetails) {

            val userDetails = authentication.principal as AegaeonUserDetails
            val client = this.clientService.getByPublicId(clientPublicId) ?: return false

            // It's load from the persistence layer, id is never null
            val clientId = requireNotNull(client.id)

            // Redirection should be allowed by the client
            if (!clientService.hasRedirectionUri(clientId, clientRedirectionUrl)) {
                return false;
            }

            // finally, Validate scopes
            val userAuthorization = getUserAuthorization(userDetails, client) ?: return false
            return scopeService.isPartOf(userAuthorization.scopes, requestedScopes)
        }

        return false

    }

    @Transactional
    @PreAuthorize("#userDetails.id == principal.id")
    fun createOneUserAuthorization(userDetails: AegaeonUserDetails, clientPublicId: String, scopes: String): Long? {

        val client = clientService.getByPublicId(clientPublicId)
        val user = userService.getUserById(userDetails.id)
        val scopeSet = scopeService.validate(scopes, emptySet())

        if (client != null && user != null) {
            // Load from persistence, cannot be null
            val userId = requireNotNull(user.id)
            val clientId = requireNotNull(client.id)
            val scopes = addOpenIdScope(scopeSet.validScopes.joinToString(" ") { scopeDto -> scopeDto.name })
            val userAuthorization = UserAuthorization(null, userId, clientId, scopes)
            return userAuthorizationRepository.create(userAuthorization)
        }

        return null
    }

    private fun getUserAuthorization(pUserDetails: AegaeonUserDetails, pClient: ClientDto): UserAuthorization? {

        if (pUserDetails.id != null) {
            return userAuthorizationRepository.getByUserIdAndClientId(pUserDetails.id, pClient.id!!)
        } else if (!pUserDetails.username.isNullOrBlank()) {
            return userAuthorizationRepository.getByUserNameAndClientId(pUserDetails.username, pClient.id!!)
        }

        return null
    }


    private fun addOpenIdScope(acceptedScopes: String): String {
        return if (!acceptedScopes.contains("openid")) {
            "openid $acceptedScopes"
        } else {
            acceptedScopes
        }
    }
}