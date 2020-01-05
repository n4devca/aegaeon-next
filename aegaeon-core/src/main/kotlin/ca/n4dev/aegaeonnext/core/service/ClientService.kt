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

import ca.n4dev.aegaeonnext.common.model.*
import ca.n4dev.aegaeonnext.common.repository.ClientRepository
import ca.n4dev.aegaeonnext.common.utils.isOneTrue
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * ClientService.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 22 - 2019
 *
 */


private fun clientToClientDto(client: Client,
                              clientScopes: List<ClientScope> = emptyList(),
                              clientFlows: List<ClientFlow> = emptyList(),
                              clientRedirections: List<ClientRedirection> = emptyList()): ClientDto {

    val scopeMap = clientScopes.associate { it.scopeCode to it.id }
    val flowMap = clientFlows.associate { it.flow to it.id }
    val redirectionMap = clientRedirections.associate { it.url to it.id }

    return ClientDto(client.id,
                     client.publicId,
                     client.secret,
                     client.name,
                     client.logoUrl,
                     client.idTokenSeconds,
                     client.accessTokenSeconds,
                     client.refreshTokenSeconds,
                     client.allowIntrospect,
                     client.idTokenSignedResponseAlg,
                     client.tokenEndpointAuthSigningAlg,
                     scopeMap,
                     flowMap,
                     redirectionMap)
}

@Service
class ClientService(private val clientRepository: ClientRepository) {

    @Transactional(readOnly = true)
    fun getById(id: Long): ClientDto? {
        return clientRepository.getClientById(id)?.let { loadClientInfo(it) }
    }

    @Transactional(readOnly = true)
    fun getByPublicId(publicId: String): ClientDto? {
        return clientRepository.getClientByPublicId(publicId)?.let { loadClientInfo(it) }
    }

    @Transactional
    fun create(clientDto: ClientDto): ClientDto {
        throw UnsupportedOperationException("Not implemented yet")
    }

    @Transactional
    fun update(id: Long, clientDto: ClientDto): ClientDto {
        throw UnsupportedOperationException("Not implemented yet")
    }

    @Transactional(readOnly = true)
    fun hasRedirectionUri(pClientId: Long, pRedirectionUri: String?): Boolean {

        if (!pRedirectionUri.isNullOrBlank()) {
            val clientRedirections = clientRepository.getClientRedirectionByClientId(pClientId)
            return isOneTrue(clientRedirections, { r -> r.url.equals(pRedirectionUri) })
        }
        return false
    }

    @Transactional(readOnly = true)
    fun hasFlow(pClientId: Long, flow: Flow): Boolean {
        val clientFlows = clientRepository.getClientFlowByClientId(pClientId)
        return clientFlows.any { clientFlow -> clientFlow.flow == flow }
    }

    @Transactional(readOnly = true)
    fun isClientInfoValid(pClientPublicId: String?, pRedirectionUrl: String?): Boolean {

        if (!pClientPublicId.isNullOrBlank() && !pRedirectionUrl.isNullOrBlank()) {
            // Get client by public id and check if exists
            val client = getByPublicId(pClientPublicId)
            return client?.id?.let { id -> hasRedirectionUri(id, pRedirectionUrl) } ?: false
        }

        return false
    }

    private fun loadClientInfo(client: Client): ClientDto {

        return client.id?.let { id ->

            val clientScopes = clientRepository.getClientScopesByClientId(id)
            val clientFlows = clientRepository.getClientFlowByClientId(id)
            val clientRedirections = clientRepository.getClientRedirectionByClientId(id)

            return clientToClientDto(client, clientScopes, clientFlows, clientRedirections)

        } ?: clientToClientDto(client)
    }

}

/**
 *
 */
data class ClientDto(
    val id: Long?,
    val publicId: String,
    val secret: String,
    val name: String,
    val logoUrl: String?,
    val idTokenSeconds: Long,
    val accessTokenSeconds: Long,
    val refreshTokenSeconds: Long,
    val allowIntrospect: Boolean,
    val idTokenSignedResponseAlg: TokenProviderType,
    val tokenEndpointAuthSigningAlg: TokenProviderType,
    val scopes: Map<String, Long?> = emptyMap(),
    val flows: Map<Flow, Long?> = emptyMap(),
    val redirections: Map<String, Long?> = emptyMap()
) {
    override fun toString(): String {
        return "ClientDto(id=$id, publicId='$publicId', name='$name')"
    }
}
