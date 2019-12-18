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

package ca.n4dev.aegaeonnext.data.db.repositories

import ca.n4dev.aegaeonnext.common.model.Client
import ca.n4dev.aegaeonnext.common.repository.ClientRepository
import ca.n4dev.aegaeonnext.common.repository.ScopeRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * ClientRepositoryImplTest.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 27 - 2019
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
internal open class ClientRepositoryImplTest {

    @Autowired
    lateinit var clientRepository: ClientRepository

    @Autowired
    lateinit var scopeRepository: ScopeRepository

    private fun newClient() : Client =  Client(null,
            "c-test-001", "secret", "a-client", null, null,
            "RSA256", 60, 300, 600, false, createdBy = "junit")

    @Test
    @Transactional
    open fun testSuccessfulCreateClient() {

        val clientId = createClient()

        val savedClient = clientRepository.getClientById(clientId)
        Assertions.assertNotNull(savedClient, "Client id=$clientId cannot be found.")
    }

    @Test
    @Transactional
    open fun testSuccessfulUpdateClient() {
        val clientId = createClient()

        // Update name and access token limit
        val newName = "A new client name"
        val newLimit: Long = 6000
        val updateClient = newClient().copy(name = newName, accessTokenSeconds = newLimit)

        clientRepository.update(clientId, updateClient);

        // Fetch
        val savedClient = clientRepository.getClientById(clientId)
        Assertions.assertNotNull(savedClient, "Client id=$clientId cannot be found.")
        Assertions.assertEquals(savedClient?.name, newName);
        Assertions.assertEquals(savedClient?.accessTokenSeconds, newLimit);
    }


    @Test
    @Transactional
    open fun testGetCompleteClient() {
        val clientId = createClient()

        // Fetch all
        val client = clientRepository.getClientById(clientId)
        val flows = clientRepository.getClientFlowByClientId(clientId)
        val redirection = clientRepository.getClientRedirectionByClientId(clientId)
        val scopes = clientRepository.getClientScopesByClientId(clientId)

        Assertions.assertTrue(flows.isNotEmpty())
        Assertions.assertTrue(redirection.isNotEmpty())
        Assertions.assertTrue(scopes.isNotEmpty())
    }

    @Test
    @Transactional
    open fun testSuccessfulAddScopeToClient() {
        val clientId = createClient()
        val openidScope = requireNotNull(scopeRepository.getByName("openid"))

        clientRepository.addScopeToClient(clientId, openidScope)

        // Fetch and check if OK
        val clientScopes = clientRepository.getClientScopesByClientId(clientId)

        Assertions.assertTrue(clientScopes.isNotEmpty(), "The client scopes should not be empty.")
        Assertions.assertTrue(clientScopes.any { clientScope -> clientScope.scopeCode == "openid" })
    }

    private fun createClient(): Long {
        val aClient = newClient()
        val clientId = clientRepository.create(aClient)
        Assertions.assertTrue(clientId > 0, "The client id should be a positive number")
        return clientId
    }
}