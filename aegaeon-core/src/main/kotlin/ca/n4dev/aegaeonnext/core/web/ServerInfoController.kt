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

import ca.n4dev.aegaeonnext.common.utils.QueryResult
import ca.n4dev.aegaeonnext.common.utils.pageOf
import ca.n4dev.aegaeonnext.core.config.AegaeonServerInfo
import ca.n4dev.aegaeonnext.core.loggerFor
import ca.n4dev.aegaeonnext.core.service.ScopeDto
import ca.n4dev.aegaeonnext.core.service.ScopeService
import ca.n4dev.aegaeonnext.core.token.TokenFactory
import ca.n4dev.aegaeonnext.core.web.view.ServerInfoResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.servlet.http.HttpServletRequest

/**
 *
 * ServerInfoController.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 02 - 2019
 *
 */

const val ServerInfoControllerURL = "/.well-known/openid-configuration"

@RestController
@RequestMapping(ServerInfoControllerURL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = ["information"], havingValue = "true", matchIfMissing = true)
class ServerInfoController(private val serverInfo: AegaeonServerInfo,
                           private val scopeService: ScopeService,
                           private val tokenFactory: TokenFactory) {

    private val LOGGER = loggerFor(javaClass)

    @GetMapping("")
    fun info(httpServletRequest: HttpServletRequest): ServerInfoResponse? {

        val issuer = serverInfo.issuer
        val ctx = httpServletRequest.contextPath
        val path = issuer + ctx
        val supportedAlgorithm = tokenFactory.getSupportedAlgorithm()

        val scopes: QueryResult<ScopeDto> = scopeService.getScopes(pageOf(0, 100))
        val scopeCodeList = scopes.result.map { scopeDto -> scopeDto.code }

        return ServerInfoResponse(issuer = issuer,
                                  authorizationEndpoint = path + AuthorizationControllerURL,
                                  tokenEndpoint = path + TokensControllerURL,
                                  userinfoEndpoint = path + UserInfoControllerURL,
                                  jwksUri = path + JwkControllerURL,
                                  tokenEndpointAuthMethodsSupported = listOf("client_secret_basic"),
                                  scopesSupported = scopeCodeList,
                                  responseTypesSupported = listOf("code", "code id_token", "id_token", "token id_token"),
                                  subjectTypesSupported = listOf("public"),
                                  userinfoSigningAlgValuesSupported = supportedAlgorithm,
                                  idTokenSigningAlgValuesSupported = supportedAlgorithm,
                                  displayValuesSupported = listOf("page"),
                                  claimTypesSupported = listOf("normal"),
                                  claimsParameterSupported = false,
                                  uiLocalesSupported = listOf(Locale.ENGLISH.toString(), Locale.CANADA_FRENCH.toString()))

    }
}
