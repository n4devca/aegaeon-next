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

package ca.n4dev.aegaeonnext.service

import ca.n4dev.aegaeonnext.model.dto.ClientDto
import ca.n4dev.aegaeonnext.model.entities.UserAuthorization
import ca.n4dev.aegaeonnext.model.repositories.UserAuthorizationRepository
import ca.n4dev.aegaeonnext.security.AegaeonUserDetails
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
                               private val scopeService: ScopeService) {


    @Transactional(readOnly = true)
    fun isAuthorized(pAuthentication: Authentication?,
                     pClientPublicId: String?,
                     pClientRedirectionUrl: String?,
                     pRawScopeParam: String): Boolean {

        if (!pClientPublicId.isNullOrBlank() && !pClientRedirectionUrl.isNullOrBlank()
            && pAuthentication != null
            && pAuthentication.principal is AegaeonUserDetails) {

            val userDetails = pAuthentication.principal as AegaeonUserDetails
            val client = this.clientService.getByPublicId(pClientPublicId) ?: return false

            // Redirection should be allowed by the client
            if (client.id == null || !clientService.hasRedirectionUri(client.id, pClientRedirectionUrl)) {
                return false
            }

            // finally, Validate scopes
            val userAuthorization = getUserAuthorization(userDetails, client) ?: return false

            return scopeService.isPartOf(userAuthorization.scopes, pRawScopeParam)

        }

        return false
    }

    @Transactional(readOnly = true)
    fun isClientInfoValid(pClientPublicId: String?, pRedirectionUrl: String?): Boolean {

        if (!pClientPublicId.isNullOrBlank() && !pRedirectionUrl.isNullOrBlank()) {

            // Get client by public id and check if exists
            val client = this.clientService.getByPublicId(pClientPublicId)

            if (client?.id != null) {
                // And then, check if it is a valid redirection
                return clientService.hasRedirectionUri(client.id, pRedirectionUrl)
            }
        }

        return false
    }

    private fun getUserAuthorization(pUserDetails: AegaeonUserDetails, pClient: ClientDto): UserAuthorization? {

        if (pUserDetails.id != null) {
            return userAuthorizationRepository.getByUserIdAndClientId(pUserDetails.id, pClient.id!!)
        } else if (!pUserDetails.username.isNullOrBlank()) {
            return userAuthorizationRepository.getByUserNameAndClientId(pUserDetails.username, pClient.id!!)
        }

        return null
    }
}