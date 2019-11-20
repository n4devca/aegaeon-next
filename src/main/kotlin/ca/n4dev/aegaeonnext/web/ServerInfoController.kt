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

package ca.n4dev.aegaeonnext.web

import ca.n4dev.aegaeonnext.config.AegaeonServerInfo
import ca.n4dev.aegaeonnext.loggerFor
import ca.n4dev.aegaeonnext.token.TokenFactory
import ca.n4dev.aegaeonnext.web.view.ServerInformation
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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
class ServerInfoController(aegaeonServerInfo: AegaeonServerInfo /*,scopeServive: ScopeServive*/, tokenFactory: TokenFactory) {

    private val LOGGER = loggerFor(javaClass)

    @GetMapping("")
    fun info(httpServletRequest: HttpServletRequest): ServerInformation? {

        val contextPath = httpServletRequest.contextPath

        //return ServerInformation(aegaeonServerInfo.issuer, "", "")
        return null
    }
}