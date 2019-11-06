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

import ca.n4dev.aegaeonnext.service.AuthenticationService
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication

class AccessTokenAuthenticationProvider(private val authenticationService: AuthenticationService) : AuthenticationProvider {

    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
     */
    override fun authenticate(pAuthentication: Authentication): Authentication {
        val accessTokenAuthentication = pAuthentication as AccessTokenAuthentication
        return this.authenticationService.authenticate(accessTokenAuthentication.accessToken)
    }


    /* (non-Javadoc)
     * @see org.springframework.security.authentication.AuthenticationProvider#supports(java.lang.Class)
     */
    override fun supports(pAuthentication: Class<*>): Boolean {
        return AccessTokenAuthentication::class.java == pAuthentication
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(AccessTokenAuthenticationProvider::class.java)
    }

}
