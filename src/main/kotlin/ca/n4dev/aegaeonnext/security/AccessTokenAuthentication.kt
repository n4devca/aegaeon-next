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

package ca.n4dev.aegaeonnext.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

/**
 *
 * AccessTokenAuthentication.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 05 - 2019
 *
 */
class AccessTokenAuthentication(val accessToken: String) : Authentication {

    //    private val userId: Long?
//    private val uniqueIdentifier: String
    //private val scopes: Set<ScopeView>
    private var authenticated = false
//    private var authorities: Collection<GrantedAuthority>? = null


    override fun getAuthorities(): MutableCollection<out GrantedAuthority>? {
        return null
    }

    override fun setAuthenticated(auth: Boolean) {
        authenticated = auth
    }

    override fun getName(): String? {
        return null
    }

    override fun getCredentials(): Any? {
        return null
    }

    override fun getPrincipal(): Any? {
        return null
    }

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    override fun getDetails(): Any? {
        return null
    }
}