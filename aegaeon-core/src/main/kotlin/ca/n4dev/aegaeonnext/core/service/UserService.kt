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

import ca.n4dev.aegaeonnext.common.model.ROLE_USER
import ca.n4dev.aegaeonnext.common.model.User
import ca.n4dev.aegaeonnext.common.model.UserInfo
import ca.n4dev.aegaeonnext.common.repository.UserRepository
import ca.n4dev.aegaeonnext.core.token.OAuthUser
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant


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
                  private val scopeService: ScopeService) {

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserDto? {
        return userRepository.getUserById(id)?.let { user -> userToUserDto(user) }
    }

    @Transactional(readOnly = true)
    fun getUserAuthoritiesByUserId(userId: Long): List<UserAuthorityDto> {
        return userRepository.getUserAuthorities(userId).map { authority -> UserAuthorityDto(authority.code) }
    }

    /*
    *
    profile: name, family_name, given_name, middle_name, nickname, preferred_username, profile, picture,
            website, gender, birthdate, zoneinfo, locale, and updated_at.
    email: email and email_verified Claims.
    address: address Claim.
    phone: phone_number and phone_number_verified Claims.
    * */

    @Transactional(readOnly = true)
    fun createPayload(userDto: UserDto, scopes: Set<ScopeDto>): Map<String, String> {

        return if (scopes.isNotEmpty()) {

            val userId = requireNotNull(userDto.id)
            val scopeIds = scopes.map { scopeDto -> scopeDto.id }.toSet()
            val claims = scopeService.getDistinctClaimsByScopes(scopeIds)
            val userInfos: List<UserInfo> = userRepository.getUserInfoByUserId(userId)

            val filterInfos: List<UserInfo> = userInfos.filter { userInfo ->
                claims.any { claimDto -> claimDto.id == userInfo.claimId }
            }

            val payloadFromInfos = filterInfos.filter { userInfo ->
                userInfo.claimCode != null || userInfo.customName != null
            }.map { userInfo: UserInfo ->
                val name = userInfo.claimCode ?: userInfo.customName
                name!! to userInfo.claimValue
            }.toMap().toMutableMap()

            // TODO(RG) Add some values from user directly?
            payloadFromInfos
        } else {
            emptyMap()
        }

    }
}

data class UserDto(
    val id: Long,
    val userName: String,
    val uniqueIdentifier: String,
    val name: String,
    val picture: String? = null,
    val locale: String? = null,
    val enabled: Boolean = true,
    val locked: Boolean = false,
    val lastLoginDate: Instant? = null,
    val version: Int = 0
)

data class UserAuthorityDto(
    val code: String
)

fun asOAuthUser(userDto: UserDto): OAuthUser {
    return OAuthUser(userDto.id, userDto.uniqueIdentifier, userDto.name, ROLE_USER)
}