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

import ca.n4dev.aegaeonnext.common.repository.UserRepository
import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 *
 * SpringUserDetailService.java
 * Spring Service Security to load user by user name.
 * @author rguillemette
 * @since 2.0.0 - Oct 28 - 2019
 *
 */
@Service("userDetailsService")
class SpringUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    override fun loadUserByUsername(pUsername: String): UserDetails {

        val user = userRepository.getUserByUserName(pUsername)

        if (user != null) {
            val userAuthorities = userRepository.getUserAuthorities(user.id!!)
            val authorities = userAuthorities.map { SimpleGrantedAuthority(it.code) }
            return AegaeonUserDetails(user.id!!, user.userName, user.password, user.enabled, !user.locked, authorities)
        }

        throw UsernameNotFoundException("$pUsername not found")
    }

}
