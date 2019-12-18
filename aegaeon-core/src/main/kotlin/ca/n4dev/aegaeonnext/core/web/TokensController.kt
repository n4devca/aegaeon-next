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

import ca.n4dev.aegaeonnext.core.web.view.TokenRequest
import ca.n4dev.aegaeonnext.core.web.view.TokenResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 *
 * TokensController.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 28 - 2019
 *
 */
const val TokensControllerURL = "/token"

@RestController
@RequestMapping(TokensControllerURL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = ["oauth"], havingValue = "true", matchIfMissing = true)
class TokensController {


    @RequestMapping(value = [""])
    @ResponseBody
    fun token(@RequestParam(value = "grant_type", required = false) pGrantType: String,
              @RequestParam(value = "code", required = false) pCode: String,
              @RequestParam(value = "redirect_uri", required = false) pRedirectUri: String,
              @RequestParam(value = "client_id", required = false) pClientPublicId: String,
              @RequestParam(value = "scope", required = false) pScope: String,
              @RequestParam(value = "refresh_token", required = false) pRefreshToken: String,
              pAuthentication: Authentication ,
              pRequestMethod: RequestMethod) : ResponseEntity<TokenResponse> {


        val response: TokenResponse? = null

        val clientPublicId: String = pClientPublicId ?: pAuthentication.name

        val tokenRequest = TokenRequest(pGrantType, pCode, pRefreshToken, clientPublicId, pRedirectUri, pScope, pRequestMethod)


        return ResponseEntity.noContent().build()
    }


}
