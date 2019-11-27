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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * ClientRepositoryImplTest.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 27 - 2019
 */
internal class ClientRepositoryImplTest {

    @Autowired
    lateinit var clientRepository: ClientRepository

    @Test
    fun getClientById() {
    }

    @Test
    fun create() {

        val aClient = Client(null,
            "c-test-001", "secret", "a-client", null, null,
            "RSA256", 60, 300, 600, false, createdBy = "junit")

        val clientId = clientRepository.create(aClient)

        Assertions.assertTrue(clientId > 0, "The client id should be a positive number")

        val savedClient = clientRepository.getClientById(clientId)
        Assertions.assertNotNull(savedClient, "Client id=$clientId cannot be found.")

    }

    @Test
    fun update() {
    }


}