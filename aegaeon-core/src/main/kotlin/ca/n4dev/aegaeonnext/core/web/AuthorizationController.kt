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

import ca.n4dev.aegaeonnext.common.model.Separator
import ca.n4dev.aegaeonnext.common.utils.trim
import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import ca.n4dev.aegaeonnext.core.service.*
import ca.n4dev.aegaeonnext.core.utils.URI_PARAM_CODE
import ca.n4dev.aegaeonnext.core.utils.URI_PARAM_ERROR
import ca.n4dev.aegaeonnext.core.utils.URI_PARAM_STATE
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.core.Authentication
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.view.RedirectView
import org.springframework.web.util.UriComponentsBuilder

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
                              private val userService: UserService,
                              private val clientService: ClientService,
                              private val scopeService: ScopeService,
                              private val tokenServicesFacade: TokenServicesFacade) {

    private val consentView = "consent"

    // TODO(Rg): Validate requestMethod and return error code
    @RequestMapping(value = [""], method = [RequestMethod.GET, RequestMethod.POST])
    fun authorize(@RequestParam(value = "response_type", required = false) responseTypeParam: String?,
                  @RequestParam(value = "scope", required = false) scopeParam: String?,
                  @RequestParam(value = "client_id", required = false) clientIdParam: String?,
                  @RequestParam(value = "redirect_uri", required = false) clientRedirectParam: String?,
                  @RequestParam(value = "state", required = false) stateParam: String?,
                  @RequestParam(value = "nonce", required = false) nonceParam: String?,
                  @RequestParam(value = "display", required = false) displayParam: String?,
                  @RequestParam(value = "prompt", required = false) promptParam: String?,
                  @RequestParam(value = "id_token_hint", required = false) idTokenHintParam: String?,
                  authentication: Authentication,
                  requestMethod: RequestMethod): ModelAndView {

        val userDetails = authentication.principal as AegaeonUserDetails

        val response =
            tokenServicesFacade.handleAuthorizeRequest(responseTypeParam,
                                                       scopeParam,
                                                       clientIdParam,
                                                       clientRedirectParam,
                                                       stateParam,
                                                       nonceParam,
                                                       displayParam,
                                                       promptParam,
                                                       idTokenHintParam,
                                                       userDetails)


        // Errors
        return when (response) {
            is Response.InternalServerError,
            is Response.InvalidClientId,
            is Response.InvalidClientRedirection,
            is Response.ValidationError -> redirectToErrorPage(response)

            is Response.ClientError -> redirectErrorToClient(response)

            is Response.AuthCode -> redirectAuthCodeToClient(response)
            is Response.ImplicitToken -> TODO()
            is Response.HybridToken -> TODO()

            is Response.UserConsentRequired ->
                consentPage(responseTypeParam,
                            scopeParam,
                            clientIdParam,
                            clientRedirectParam,
                            stateParam,
                            nonceParam,
                            displayParam,
                            promptParam,
                            userDetails)
        }

//        // Check not empty client info
//        val clientPublicId = trim(clientIdParam) ?: return InternalError.InvalidClientId(clientIdParam)
//        val clientRedirection = trim(clientRedirectParam) ?: return InternalError.InvalidClientRedirection(clientPublicId, clientRedirectParam)
//
//        // Get client
//        val client = clientService.getByPublicId(clientPublicId) ?: return InternalError.InvalidClientId(clientIdParam)
//        val clientId = requireNotNull(client.id) // Coming from persistence, OK
//
//        if (!clientService.hasRedirectionUri(clientId, clientRedirection)) {
//            return InternalError.InvalidClientRedirection(clientPublicId, clientRedirection)
//        }
//
//        // Parse requested response type and validate
//        val responseType = trim(responseTypeParam) ?:
//            return ClientError.UnsupportedResponseType(clientRedirection, stateParam, Separator.QUESTION_MARK)
//        val responseTypes = responseTypesFromParams(responseType);
//        if (responseTypes.isEmpty()) {
//            return ClientError.UnsupportedResponseType(clientRedirection, stateParam, Separator.QUESTION_MARK)
//        }
//        val separator = getSeparatorForResponseType(responseTypes)
//
//        // Method used
//        if (requestMethod != RequestMethod.GET && requestMethod != RequestMethod.POST) {
//            return ClientError.InvalidRequest(clientRedirection, stateParam, separator)
//        }
//
//        // Nonce is mandatory
//        val nonce = trim(nonceParam) ?: return ClientError.InvalidRequest(clientRedirection, stateParam, separator)
//
//        // Check scope
//        val flow = responseTypesToFlow(responseTypes)
//        val requestScopes = trim(scopeParam) ?: return ClientError.InvalidScope(clientRedirection, stateParam, separator)
//        val scopeSet = scopeService.validate(requestScopes, flow)
//
//        if (scopeSet.invalidScopes.isNotEmpty()) {
//            return ClientError.InvalidScope(clientRedirection, stateParam, separator)
//        }
//
//        // Check if this client is allowed to use this flow
//        if(!clientService.hasFlow(clientId, flow)) {
//            return ClientError.UnauthorizedClient(clientRedirection, stateParam, separator)
//        }
//
//        val userDetails = authentication.principal as AegaeonUserDetails
//
//        val alreadyAuthorizedByUser =
//            userAuthorizationService.isAuthorized(userDetails, clientPublicId, clientRedirection, scopeSet.validScopes)
//        val prompt = promptFromString(promptParam)
//        if (prompt == Prompt.none && !alreadyAuthorizedByUser) {
//            // return directly to client
//            return ClientError.InteractionRequired(clientRedirection, stateParam, separator)
//
//        } else if (!alreadyAuthorizedByUser || prompt == Prompt.login || prompt == Prompt.login) {
//            // need to get consent
//            return consentPage(responseType,
//                scopeSet.validScopes,
//                clientPublicId,
//                clientRedirection,
//                stateParam,
//                nonce,
//                displayParam,
//                promptParam,
//                authentication)
//        }
//
//
//
//        // Good to go!!!
//        return when(flow) {
//            Flow.IMPLICIT -> ModelAndView(implicitResponse())
//            Flow.AUTHORIZATION_CODE -> ModelAndView(authorizationCodeResponse(userDetails,
//                clientId,
//                clientRedirection,
//                scopeSet.validScopes,
//                responseType, nonce, stateParam, separator))
//            Flow.HYBRID -> ModelAndView(hybridResponse())
//            else -> InternalError.ServerError("Cannot handle flow $flow")
//        }
    }

    @PostMapping("/consent")
    fun addUserAuthorization(@RequestParam(value = "response_type", required = false) responseTypeParam: String,
                             @RequestParam(value = "scope", required = false) scopeParam: String,
                             @RequestParam(value = "client_id", required = false) clientIdParam: String,
                             @RequestParam(value = "redirect_uri", required = false) clientRedirectParam: String,
                             @RequestParam(value = "state", required = false) stateParam: String?,
                             @RequestParam(value = "nonce", required = false) nonceParam: String,
                             @RequestParam(value = "display", required = false) displayParam: String?,
                             @RequestParam(value = "prompt", required = false) promptParam: String?,
                             @RequestParam(value = "id_token_hint", required = false) idTokenHintParam: String?,
                             @RequestParam(value = "accept_scopes", required = false) acceptedScopesParam: List<String>,
                             authentication: Authentication): ModelAndView {

        val userDetails: AegaeonUserDetails = authentication.principal as AegaeonUserDetails
        val clientId = trim(clientIdParam) ?: return InternalError.InvalidClientId(clientIdParam)
        val acceptedScopes = acceptedScopesParam.joinToString(" ")

        userAuthorizationService.createOrUpdateOneUserAuthorization(userDetails, clientId, acceptedScopes)
            ?: return InternalError.ServerError("Unable to create UserAuthorization.");

        // Go back to authorize
        return authorize(responseTypeParam,
                         acceptedScopes,
                         clientId,
                         clientRedirectParam,
                         stateParam,
                         nonceParam,
                         displayParam,
                         promptParam,
                         idTokenHintParam,
                         authentication,
                         RequestMethod.GET)
    }


    /**
     * Create a page to ask user consent.
     * See authorize endpoint.
     *
     * @param pAuthentication   The authenticated user.
     * @param authRequest The authorization request.
     * @return A model and view.
     */
    private fun consentPage(responseTypeParam: String?,
                            scopeParam: String?,
                            clientIdParam: String?,
                            clientRedirectParam: String?,
                            stateParam: String?,
                            nonceParam: String?,
                            displayParam: String?,
                            promptParam: String?,
                            userDetails: AegaeonUserDetails): ModelAndView {

        val authPage = ModelAndView(consentView);

        authPage.addObject("client_id", clientIdParam);
        authPage.addObject("redirect_uri", clientRedirectParam);
        authPage.addObject("scope", scopeParam);
        authPage.addObject("state", stateParam);
        authPage.addObject("nonce", nonceParam);
        authPage.addObject("response_type", responseTypeParam);
        authPage.addObject("display", displayParam);
        authPage.addObject("prompt", promptParam);

        val user = requireNotNull(userService.getUserById(userDetails.id))
        val validScopes = scopeService.getValidScopes(scopeParam)
        val payload: Map<String, String> = userService.createPayload(user, validScopes)

        authPage.addObject("claims", payload);
        authPage.addObject("validScopes", validScopes);

        return authPage;
    }

    private fun redirectAuthCodeToClient(authCode: Response.AuthCode): ModelAndView {

        val params = LinkedMultiValueMap<String, String>()

        authCode.code.apply { params.add(URI_PARAM_CODE, this) }
        authCode.state?.apply { params.add(URI_PARAM_STATE, this) }

        val url = authCode.url

        val builder = if (authCode.separator == Separator.FRAGMENT) {
            val queryParams = UriComponentsBuilder.fromHttpUrl(url).queryParams(params).build().query
            UriComponentsBuilder.fromHttpUrl(url).fragment(queryParams)
        } else {
            UriComponentsBuilder.fromHttpUrl(url).queryParams(params)
        }

        val uriComponents = builder.build()
        return ModelAndView(RedirectView(uriComponents.toUri().toString()))
    }

    private fun redirectErrorToClient(clientError: Response.ClientError): ModelAndView {

        val params = LinkedMultiValueMap<String, String>()

        clientError.error.apply { params.add(URI_PARAM_ERROR, this.toString()) }
        clientError.state?.apply { params.add(URI_PARAM_STATE, this) }

        val url = clientError.url

        val builder = if (clientError.separator == Separator.FRAGMENT) {
            val queryParams = UriComponentsBuilder.fromHttpUrl(url).queryParams(params).build().query
            UriComponentsBuilder.fromHttpUrl(url).fragment(queryParams)
        } else {
            UriComponentsBuilder.fromHttpUrl(url).queryParams(params)
        }

        val uriComponents = builder.build()
        return ModelAndView(RedirectView(uriComponents.toUri().toString()))
    }

    private fun redirectToErrorPage(response: Response): ModelAndView {
        val params = LinkedMultiValueMap<String, String>()
        params.add(ERROR_CODE_PARAM, response.javaClass.simpleName)
        val uriComponents = UriComponentsBuilder.fromPath(ErrorControllerURL).queryParams(params).build()
        return ModelAndView(RedirectView(uriComponents.toUri().toString()))
    }


    private fun append(pParams: MultiValueMap<String, String>, pKey: String, pValue: String?) {
        if (!pKey.isNullOrBlank() && !pValue.isNullOrBlank()) {
            pParams.add(pKey, pValue)
        }
    }
}

