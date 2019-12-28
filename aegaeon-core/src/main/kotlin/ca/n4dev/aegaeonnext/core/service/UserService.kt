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
import ca.n4dev.aegaeonnext.common.repository.ScopeRepository
import ca.n4dev.aegaeonnext.common.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


private fun userToUserDto(user: User): UserDto =
    // TODO(RG): It's mainly a copy, see possible transformation
    UserDto(user.id,
        user.userName,
        user.uniqueIdentifier,
        user.name,
        user.picture,
        user.locale,
        user.enabled,
        user.locked,
        user.lastLoginDate,
        user.version)


/**
 * UserService.java
 * @author rguillemette
 * @since 2.0.0 - Dec 22 - 2019
 */
@Service
class UserService(private val userRepository: UserRepository,
                  private val scopeRepository: ScopeRepository) {

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserDto? {
        return userRepository.getUserById(id)?.let {user ->  userToUserDto(user) }
    }

    @Transactional(readOnly = true)
    fun createPayload(userDto: UserDto, scopes: Set<ScopeDto>): Map<ScopeDto, List<ClaimDto>> {

        val userId = requireNotNull(userDto.id)

        val payload =
            scopes.associateBy ({ scopeDto -> scopeDto }, { mutableListOf<ClaimDto>() })

        val requestedScopeIds =
            scopeRepository.getByNames(scopes.map { scopeDto -> scopeDto.name }.toSet())
                .map { scope -> scope.id!! }.toSet()


        val userInfos = userRepository.getUserInfoByUserIdAndScopeIds(userId, requestedScopeIds)

        payload.forEach { scopeAndClaims ->
            val claims = userInfos.filter { userInfo -> userInfo.scopeId == scopeAndClaims.key.id }
                .map { userInfo -> ClaimDto(userInfo.claimName, userInfo.claimValue) }

            scopeAndClaims.value.addAll(claims)
        }

//        return userInfos.groupBy( { userInfo -> requireNotNull(userInfo.scopeName) },
//                                  {userInfo -> userInfo.claimName to userInfo.claimValue} )

        return payload
    }
}

data class ClaimDto(val name: String, val value: String)

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

