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

package ca.n4dev.aegaeonnext.data.db.repositories

import ca.n4dev.aegaeonnext.common.model.User
import ca.n4dev.aegaeonnext.common.repository.UserRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import java.util.*

/**
 * UserRepositoryImplTest.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 24 - 2019
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
internal class UserRepositoryImplTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun getUserInfoByUserName() {

        val userName = "admin@localhost"
        val user = userRepository.getUserByUserName(userName)

        assertNotNull(user, "User ${userName} cannot be found")
    }

    @Test
    fun createUser() {
        val userId = userRepository.create("test_user@junit",
                                           UUID.randomUUID().toString(),
                                           "test",
                                           "{bcrypt}password",
                                           true)
        assertNotNull(userId, "New user id")

        val createdUser = userRepository.getUserById(userId)
        assertNotNull(createdUser, "Cannot load user ${userId}")
    }
}