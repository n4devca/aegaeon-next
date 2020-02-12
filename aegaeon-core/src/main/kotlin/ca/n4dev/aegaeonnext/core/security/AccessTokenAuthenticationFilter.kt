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

package ca.n4dev.aegaeonnext.core.security

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AccessTokenAuthenticationFilter(private val authenticationManager: AuthenticationManager,
                                      private val authenticationEntryPoint: AuthenticationEntryPoint) : GenericFilterBean() {

    private val AUTH_HEADER_SCHEMA = "Bearer"
    private val AUTH_HEADER_SCHEMA_LENGTH = AUTH_HEADER_SCHEMA.length
    private val AUTH_HEADER_NAME = "Authorization"
    private val AUTH_PARAM_NAME = "access_token"


    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    override fun doFilter(pServletRequest: ServletRequest, pServletResponse: ServletResponse, pFilterChain: FilterChain) {

        val request = pServletRequest as HttpServletRequest
        val response = pServletResponse as HttpServletResponse

        val header = request.getHeader(AUTH_HEADER_NAME)
        val param = request.getParameter(AUTH_PARAM_NAME)
        var accessToken: String? = null

        if (header != null && header.isNotEmpty()) {
            accessToken = extractAccessToken(header)
        } else if (param != null && param.isNotEmpty()) {
            accessToken = param
        }

        if (accessToken != null && accessToken.isNotEmpty()) {

            try {
                // Attempt authentication
                val auth = attemptAuthentication(accessToken)
                SecurityContextHolder.getContext().authentication = auth

            } catch (ae: AuthenticationException) {
                this.authenticationEntryPoint.commence(request, response, ae)
                return
            } catch (e: Exception) {
                this.authenticationEntryPoint.commence(request,
                    response,
                    AccessTokenAuthenticationException("AccessTokenAuthenticationFilter exception", e))
                return
            }

        }

        pFilterChain.doFilter(pServletRequest, pServletResponse)
    }


    private fun attemptAuthentication(pAccessToken: String): Authentication {
        return this.authenticationManager.authenticate(AccessTokenAuthentication(pAccessToken))
    }

    private fun extractAccessToken(pAuthorizationHeader: String): String? {

        if (pAuthorizationHeader.startsWith(AUTH_HEADER_SCHEMA)) {
            return pAuthorizationHeader.substring(AUTH_HEADER_SCHEMA_LENGTH).trim()
        }

        return null
    }

}