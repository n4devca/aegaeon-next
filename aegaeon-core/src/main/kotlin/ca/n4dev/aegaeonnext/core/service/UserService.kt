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

import ca.n4dev.aegaeonnext.common.model.User
import ca.n4dev.aegaeonnext.common.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime


private fun userToUserDto(user: User): UserDto {
    TODO()
}

/**
 * UserService.java
 * @author rguillemette
 * @since 2.0.0 - Dec 22 - 2019
 */
@Service
class UserService(private val userRepository: UserRepository) {

    fun getUserById(id: Long): UserDto? {
        return userRepository.getUserById(id)?.let {user ->  userToUserDto(user) }
    }

    fun createPayload(userDto: UserDto, scopes: Set<String>): Map<String, Map<String, Object>> {

        TODO()
    }
}

data class UserDto(val id: Long?,
                   val userName: String,
                   val uniqueIdentifier: String,
                   val name: String,
                   val picture: String? = null,
                   val locale: String? = null,
                   val enabled: Boolean = true,
                   val locked: Boolean = false,
                   val lastLoginDate: LocalDateTime? = null,
                   val version: Int = 0)