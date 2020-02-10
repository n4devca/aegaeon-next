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

import ca.n4dev.aegaeonnext.common.model.Prompt
import ca.n4dev.aegaeonnext.common.utils.areOneEmpty
import ca.n4dev.aegaeonnext.core.loggerFor
import ca.n4dev.aegaeonnext.core.service.ClientService
import ca.n4dev.aegaeonnext.core.service.UserAuthorizationService
import ca.n4dev.aegaeonnext.core.utils.*
import ca.n4dev.aegaeonnext.core.web.AuthorizationControllerURL
import ca.n4dev.aegaeonnext.core.web.ErrorInterceptorController
import ca.n4dev.aegaeonnext.core.web.view.AuthRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 *
 * PromptAwareAuthenticationFilter.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 06 - 2019
 *
 */
class PromptAwareAuthenticationFilter(private val errorInterceptorController: ErrorInterceptorController,
                                      private val userAuthorizationService: UserAuthorizationService,
                                      private val clientService: ClientService) : GenericFilterBean() {

    private val LOGGER = loggerFor(javaClass)

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(pServletRequest: ServletRequest, pServletResponse: ServletResponse, pFilterChain: FilterChain) {

        val request = pServletRequest as HttpServletRequest
        val response = pServletResponse as HttpServletResponse

        // Only on /authorize
        val requestedPath = request.requestURI

        if (AuthorizationControllerURL.startsWith(requestedPath)) {

            val prompt = request.getParameter(URI_PARAM_PROMPT)
            val clientIdStr = request.getParameter(URI_PARAM_CLIENT_ID)
            val redirectionUrl = request.getParameter(URI_PARAM_REDIRECTION_URL)
            val responseType = request.getParameter(URI_PARAM_RESPONSE_TYPE)
            val state = request.getParameter(URI_PARAM_STATE)
            val nonce = request.getParameter(URI_PARAM_NONCE)
            val scope = request.getParameter(URI_PARAM_SCOPE)


            val authRequest = AuthRequest(responseType = responseType,
                scope = scope,
                clientId = clientIdStr,
                redirectUri = redirectionUrl,
                state = state,
                nonce = nonce,
                prompt = prompt)

            // none
            // => check session, client and redirection
            // login
            // => force sign-in

            // Client id and url need to be valid, otherwise, we don't redirect or response
            if (isValidRequest(authRequest)) {

                if (prompt == Prompt.none.toString()) {

                    if (!isAuthorizedAlready(clientIdStr, redirectionUrl, scope)) {
                        handleError(authRequest, request, response)
                        return
                    }

                } else if (prompt == Prompt.login.toString()) {
                    clearUserContext(request)
                    // Nothing else to do, next filter should ask user to login
                }
            }
        }

        pFilterChain.doFilter(pServletRequest, pServletResponse)
    }

    private fun handleError(pAuthRequest: AuthRequest,
                            pHttpServletRequest: HttpServletRequest,
                            pHttpServletResponse: HttpServletResponse) {

        try {

            LOGGER.error("PromptAwareAuthenticationFilter#handleError")
//            val grantType = flowOfResponseType(pAuthRequest.responseType)
//
//            val response = this.controllerErrorInterceptor
//                .openIdException(OpenIdExceptionBuilder()
//                    .code(ServerExceptionCode.USER_UNAUTHENTICATED)
//                    .redirection(pAuthRequest.redirectUri)
//                    .from(grantType)
//                    .state(pAuthRequest.state)
//                    .build(),
//                    Locale.ENGLISH,
//                    pHttpServletRequest,
//                    pHttpServletResponse)
//
//            // TODO(RG) : other response ?
//            if (response is RedirectView) {
//                pHttpServletResponse.sendRedirect((response as RedirectView).url)
//            } else if (response is ModelAndView) {
//                // Redirect to error
//                val mv = response as ModelAndView
//                val params = LinkedMultiValueMap<String, String>()
//                mv.model.forEach { (pK, pV) ->
//
//                    // Convert String params
//                    if (pV is String) {
//                        params.add(pK.toLowerCase(), pV)
//                    }
//                }
//
//                val url = UriBuilder.build("/" + mv.viewName!!, params, false)
//                pHttpServletResponse.sendRedirect(url)
//            }

        } catch (pException: Exception) {
            LOGGER.error("PromptAwareAuthenticationFilter#handleError has failed", pException)
        }

    }

    private fun isAuthorizedAlready(pClientId: String, pRedirectionUrl: String, pScopeParam: String): Boolean {
        val existingAuth = SecurityContextHolder.getContext().authentication

        return if (existingAuth == null) {
            userAuthorizationService.isAuthorized(existingAuth, pClientId, pRedirectionUrl, pScopeParam)
        } else false

    }

    private fun isValidRequest(pAuthRequest: AuthRequest): Boolean {

        val hasProperParams =
            !areOneEmpty(pAuthRequest.prompt,
                pAuthRequest.clientId,
                pAuthRequest.redirectUri,
                pAuthRequest.responseType)

        return if (hasProperParams) {
            // OK, then, check the client
            clientService.isClientInfoValid(pAuthRequest.clientId, pAuthRequest.redirectUri)
        } else false

    }

    private fun clearUserContext(pRequest: HttpServletRequest) {
        val session = pRequest.getSession(false)
        session?.invalidate()
        SecurityContextHolder.clearContext()
    }
}