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

import ca.n4dev.aegaeonnext.model.dto.ClientDto
import ca.n4dev.aegaeonnext.model.entities.Client
import ca.n4dev.aegaeonnext.model.entities.ClientFlow
import ca.n4dev.aegaeonnext.model.entities.ClientRedirection
import ca.n4dev.aegaeonnext.model.entities.ClientScope
import ca.n4dev.aegaeonnext.model.repositories.ClientRepository
import ca.n4dev.aegaeonnext.utils.isOneTrue
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


fun clientToClientDto(client: Client,
                      clientScopes: List<ClientScope>,
                      clientFlows: List<ClientFlow>,
                      clientRedirections: List<ClientRedirection>): ClientDto {

    val scopeMap = clientScopes.associate { it.scopeCode to it.id }
    val flowMap = clientFlows.associate { it.flow to it.id }
    val redirectionMap = clientRedirections.associate { it.url to it.id }

    return ClientDto(client.id, client.publicId, client.secret, client.name, client.logoUrl, scopeMap, flowMap, redirectionMap)
}

@Service
class ClientService(private val clientRepository: ClientRepository) {

    @Transactional(readOnly = true)
    fun getById(id: Long) : ClientDto? {
        return clientRepository.getClientById(id)?.let { loadClientInfo(it) }
    }

    @Transactional(readOnly = true)
    fun getByPublicId(publicId: String) : ClientDto? {
        return clientRepository.getClientByPublicId(publicId)?.let { loadClientInfo(it) }
    }

    @Transactional
    fun create(clientDto: ClientDto) : ClientDto {
        throw UnsupportedOperationException("Not implemented yet")
    }

    @Transactional
    fun update(id: Long, clientDto: ClientDto) : ClientDto {
        throw UnsupportedOperationException("Not implemented yet")
    }

    @Transactional(readOnly = true)
    fun hasRedirectionUri(pClientId: Long, pRedirectionUri: String): Boolean {

        if (pRedirectionUri.isNotBlank()) {
            val clientRedirections = clientRepository.getClientRedirectionByClientId(pClientId)
            return isOneTrue(clientRedirections, { r -> r.url.equals(pRedirectionUri) })
        }
        return false
    }

    private fun loadClientInfo(client: Client) : ClientDto? {

        var clientScopes = emptyList<ClientScope>()
        var clientFlows = emptyList<ClientFlow>()
        var clientRedirections = emptyList<ClientRedirection>()
        client.id?.let {
            clientScopes = clientRepository.getClientScopesByClientId(it)
            clientFlows = clientRepository.getClientFlowByClientId(it)
            clientRedirections = clientRepository.getClientRedirectionByClientId(it)
        }

        return clientToClientDto(client, clientScopes, clientFlows, clientRedirections)
    }
}