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

import ca.n4dev.aegaeonnext.model.repositories.ClientRepository
import ca.n4dev.aegaeonnext.security.AegaeonUserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 *
 * SpringClientDetailsService.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 28 - 2019
 *
 */
@Service("clientDetailsService")
class SpringAuthClientDetailsService(private val clientRepository: ClientRepository) : UserDetailsService {

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    override fun loadUserByUsername(pUsername: String): UserDetails {

        clientRepository.getClientByPublicId(pUsername)?.let {
            val aegaeonUserDetails = AegaeonUserDetails(it.id,
                pUsername,
                "{noop}" + it.secret,
                enable = true,
                nonLocked = true,
                authorities = listOf(SimpleGrantedAuthority("ROLE_CLIENT")))

            aegaeonUserDetails.allowIntrospection = it.allowIntrospect

            return aegaeonUserDetails
        }

        throw UsernameNotFoundException("$pUsername not found")
    }

}
