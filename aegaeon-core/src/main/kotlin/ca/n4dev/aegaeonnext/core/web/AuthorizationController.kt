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

import ca.n4dev.aegaeonnext.common.utils.splitStringOn
import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import ca.n4dev.aegaeonnext.core.service.AuthorizationCodeService
import ca.n4dev.aegaeonnext.core.service.UserAuthorizationService
import ca.n4dev.aegaeonnext.core.service.UserService
import ca.n4dev.aegaeonnext.core.web.view.AuthRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

/**
 *
 * AuthorizationController.java
 *
 * Controller used to either return an access token (implicit) or
 * an authorize code.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 06 - 2019
 *
 */
const val AuthorizationControllerURL = "/authorize"


@RestController
@RequestMapping(AuthorizationControllerURL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = ["oauth"], havingValue = "true", matchIfMissing = true)
class AuthorizationController(private val userAuthorizationService: UserAuthorizationService,
                              private val authorizationCodeService: AuthorizationCodeService,
                              private val userService: UserService) {

    private val consentView = "consent"

    @RequestMapping("")
    fun authorize(@RequestParam(value = "response_type", required = false) responseType: String,
                  @RequestParam(value = "scope", required = false) scope: String,
                  @RequestParam(value = "client_id", required = false) clientPublicId: String,
                  @RequestParam(value = "redirect_uri", required = false) redirectUri: String,
                  @RequestParam(value = "state", required = false) state: String,
                  @RequestParam(value = "nonce", required = false) nonce: String,
                  @RequestParam(value = "display", required = false) display: String,
                  @RequestParam(value = "prompt", required = false) prompt: String,
                  @RequestParam(value = "id_token_hint", required = false) idTokenHint: String,
                  authentication: Authentication,
                  requestMethod: RequestMethod) : ModelAndView {



        TODO("not implemented yet")
    }



    /**
     * Create a page to ask user consent.
     * See authorize endpoint.
     *
     * @param pAuthentication   The authenticated user.
     * @param authRequest The authorization request.
     * @return A model and view.
     */
    private fun consentPage(authentication: Authentication,
                            authRequest: AuthRequest): ModelAndView {

        val authPage = ModelAndView(consentView);

        authPage.addObject("client_id", authRequest.clientId);
        authPage.addObject("redirect_uri", authRequest.redirectUri);
        authPage.addObject("scope", authRequest.scope);
        authPage.addObject("state", authRequest.state);
        authPage.addObject("nonce", authRequest.nonce);
        authPage.addObject("response_type", authRequest.responseType);
        authPage.addObject("display", authRequest.display);
        authPage.addObject("prompt", authRequest.prompt);

        val scopeList = splitStringOn(authRequest.scope)
        val user = requireNotNull(userService.getUserById((authentication.principal as AegaeonUserDetails).id))
        val payload: Map<String, Map<String, Object>> = userService.createPayload(user, scopeList.toSet())
        authPage.addObject("claims", payload);

        return authPage;
    }
}