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

package ca.n4dev.aegaeonnext.core.web

import ca.n4dev.aegaeonnext.common.model.Flow
import ca.n4dev.aegaeonnext.common.model.flowFromName
import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import ca.n4dev.aegaeonnext.core.service.*
import ca.n4dev.aegaeonnext.core.service.TokenResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.*

/**
 *
 * TokensController
 *
 * Controller returning jwt token.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 28 - 2019
 *
 */
const val TokensControllerURL = "/token"

private const val HEADER_CACHE_CONTROL_KEY = "Cache-Control"
private const val HEADER_CACHE_CONTROL_VALUE = "no-store"
private const val HEADER_PRAGMA_KEY = "Pragma"
private const val HEADER_PRAGMA_VALUE = "no-cache"

@RestController
@RequestMapping(TokensControllerURL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = ["oauth"], havingValue = "true", matchIfMissing = true)
class TokensController(private val authorizationCodeService: AuthorizationCodeService,
                       private val userService: UserService,
                       private val clientService: ClientService,
                       private val scopeService: ScopeService,
                       private val tokenServicesFacade: TokenServicesFacade) {

    // TODO(Rg): Validate requestMethod and return error code
    @RequestMapping("")
    @ResponseBody
    fun token(@RequestParam(value = "grant_type", required = false) grantTypeParam: String?,
              @RequestParam(value = "code", required = false) codeParam: String?,
              @RequestParam(value = "client_id", required = false) clientIdParam: String?,
              @RequestParam(value = "redirect_uri", required = false) clientRedirectParam: String?,
              @RequestParam(value = "scope", required = false) scopeParam: String?,
              @RequestParam(value = "refresh_token", required = false) refreshTokenParam: String?,
              authentication: Authentication,
              requestMethod: RequestMethod): ResponseEntity<TokenResponse> {

        val flow = flowFromName(grantTypeParam) ?: return ResponseEntity(TokenResponse.InvalidRequest(), HttpStatus.BAD_REQUEST);
        val userDetails = authentication.principal as AegaeonUserDetails

        if (requestMethod != RequestMethod.POST) {
            return createResponseEntity(TokenResponse.InvalidRequest(), HttpStatus.BAD_REQUEST)
        }

        // Either auth code or refresh token
        val tokenResponse: TokenResponse = when (flow) {

            Flow.AUTHORIZATION_CODE ->
                tokenServicesFacade.handleTokenRequestWithAuthCode(codeParam, clientRedirectParam, scopeParam, userDetails)

            Flow.REFRESH_TOKEN ->
                tokenServicesFacade.handleRefreshTokenRequest(refreshTokenParam, clientRedirectParam, scopeParam, userDetails)

            Flow.CLIENT_CREDENTIALS -> tokenServicesFacade.handleClientCredTokenRequest(clientRedirectParam, scopeParam, userDetails)

            //Flow.HYBRID -> tokenServicesFacade.handleHybridTokenRequest(clientRedirectParam, scopeParam, userDetails)

            else -> TokenResponse.InvalidGrant()
        }

        return createResponseEntity(tokenResponse, getHttpStatus(tokenResponse))
    }

    private fun <R> createResponseEntity(response: R, httpStatus: HttpStatus): ResponseEntity<R> {
        val headers = LinkedMultiValueMap<String, String>()
        headers[HEADER_CACHE_CONTROL_KEY] = HEADER_CACHE_CONTROL_VALUE
        headers[HEADER_PRAGMA_KEY] = HEADER_PRAGMA_VALUE

        return ResponseEntity(response, headers, httpStatus)
    }

    private fun getHttpStatus(tokenResponse: TokenResponse): HttpStatus = when (tokenResponse) {
        is TokenResponse.Token -> HttpStatus.OK
        is TokenResponse.ServerError -> HttpStatus.INTERNAL_SERVER_ERROR
        else -> HttpStatus.BAD_REQUEST
    }
}
