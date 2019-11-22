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

package ca.n4dev.aegaeonnext.core.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class AegaeonUserDetails(val id: Long?,
                         private val username: String,
                         private val password: String,
                         private val enable: Boolean,
                         private val nonLocked: Boolean,
                         private val authorities: Collection<GrantedAuthority>) : UserDetails {

    /**
     * @return the allowIntrospection
     */
    /**
     * @param pAllowIntrospection the allowIntrospection to set
     */
    var allowIntrospection = false


    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return this.authorities
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    override fun getPassword(): String {
        return this.password
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getUserId()
     */
    override fun getUsername(): String {
        return this.username
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     */
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     */
    override fun isAccountNonLocked(): Boolean {
        return this.nonLocked
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     */
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    override fun isEnabled(): Boolean {
        return this.enable
    }

    override fun toString(): String {
        return "$id,$username,$enable,$nonLocked,$allowIntrospection"
    }
}