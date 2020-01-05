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

package ca.n4dev.aegaeonnext.core.service

import ca.n4dev.aegaeonnext.common.model.*
import ca.n4dev.aegaeonnext.common.utils.isAfterNow
import ca.n4dev.aegaeonnext.common.utils.trim
import ca.n4dev.aegaeonnext.core.loggerFor
import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import ca.n4dev.aegaeonnext.core.service.interceptor.AuthorizationValidator
import ca.n4dev.aegaeonnext.core.token.TokenFactory
import ca.n4dev.aegaeonnext.core.web.view.TokenResponse
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate


/**
 *
 * TokenServiceFacade
 *
 * @author rguillemette
 * @since Dec 29 - 2019
 *
 */
@Service
class TokenServicesFacade(private val tokenFactory: TokenFactory,
                          private val accessTokenService: AccessTokenService,
                          private val idTokenService: IdTokenService,
                          private val refreshTokenService: RefreshTokenService,
                          private val clientService: ClientService,
                          private val userService: UserService,
                          private val scopeService: ScopeService,
                          private val authorizationCodeService: AuthorizationCodeService,
                          private val userAuthorizationService: UserAuthorizationService,
                          private val authorizationInterceptor: AuthorizationValidator,
                          private val transactionManager: PlatformTransactionManager) {

    private val LOGGER = loggerFor(javaClass)

    private val transactionTemplate: TransactionTemplate = TransactionTemplate(transactionManager)

    //@Transactional
    @PreAuthorize("isAuthenticated() and #userDetails.id == principal.id")
    fun handleTokenRequestWithAuthCode(authCode: String?,
                                       clientRedirection: String?,
                                       scopes: String?,
                                       userDetails: AegaeonUserDetails): TokenResponse {

        try {

            val t: TokenResponse? = transactionTemplate.execute(fun(status: TransactionStatus): TokenResponse {
                val code = trim(authCode) ?: return TokenResponse.InvalidGrant()
                val clientDto = clientService.getById(userDetails.id) ?: return TokenResponse.InvalidClient()
                val redirectUri = trim(clientRedirection) ?: return TokenResponse.InvalidClient()
                val clientId = requireNotNull(clientDto.id) // Safe, coming from persistence

                val authorizationCode = authorizationCodeService.getByCode(code) ?: return TokenResponse.InvalidGrant()

                try {

                    // Validate client
                    if (authorizationCode.clientId != clientId
                        || authorizationCode.redirectUrl != redirectUri
                        || !clientService.hasRedirectionUri(clientId, redirectUri)) {
                        return TokenResponse.InvalidClient()
                    }

                    // Check if the code is still valid
                    if (!isAfterNow(authorizationCode.validUntil)) {
                        return TokenResponse.InvalidGrant()
                    }

                    // Get user and make sure it exists
                    val userDto = userService.getUserById(authorizationCode.userId) ?: return TokenResponse.InvalidGrant()

                    // OK, create the tokens
                    val requestedScopes = authorizationCode.scopes
                    val payload = userService.createPayload(userDto, requestedScopes)

                    val idTokenDto = idTokenService.createToken(userDto, requestedScopes, authorizationCode.nonce, payload, userDetails)
                    val accessTokenDto = accessTokenService.createToken(userDto, requestedScopes, payload, userDetails)
                    val refreshTokenDto = refreshTokenService.createToken(userDto, requestedScopes, userDetails)

                    return TokenResponse.Token(idTokenDto?.token,
                                               accessTokenDto?.token,
                                               accessTokenDto?.validUntil?.epochSecond ?: 0L,
                                               requestedScopes.joinToString(" ") { scopeDto -> scopeDto.code },
                                               refreshTokenDto?.token)

                } finally {
                    // Always delete the authorization code.
                    authorizationCodeService.delete(authorizationCode.id)
                }
            })

            return t ?: TokenResponse.ServerError()

        } catch (exception: Exception) {
            LOGGER.error("TokenServicesFacade#handleTokenRequestWithAuthCode error: ", exception)
            return TokenResponse.ServerError()
        }
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and #userDetails.id == principal.id")
    fun handleRefreshTokenRequest(refreshTokenParam: String?,
                                  clientRedirection: String?,
                                  scopes: String?,
                                  userDetails: AegaeonUserDetails): TokenResponse {


        TODO()
    }

    @Transactional
    @PreAuthorize("isAuthenticated() and #userDetails.id == principal.id")
    fun handleClientCredTokenRequest(clientRedirection: String?,
                                     scopes: String?,
                                     userDetails: AegaeonUserDetails): TokenResponse {
        TODO()
    }

    /**
     * Handle "authorize" type of request
     *
     * This function will either return an authorization code or
     * a set of token based on the requested response type.
     *
     * As many different case and validation need to be handle, a definite set of response (using sealed class)
     * can be returned.
     *
     */
    @Transactional
    fun handleAuthorizeRequest(responseTypeParam: String?,
                               scopeParam: String?,
                               clientIdParam: String?,
                               clientRedirectParam: String?,
                               stateParam: String?,
                               nonceParam: String?,
                               displayParam: String?,
                               promptParam: String?,
                               idTokenHintParam: String?,
                               aegaeonUserDetails: AegaeonUserDetails): Response {

        val clientPublicId = trim(clientIdParam) ?: return Response.InvalidClientId(clientIdParam)
        val redirection = trim(clientRedirectParam) ?: return Response.InvalidClientRedirection(clientPublicId, clientRedirectParam)

        // Get client
        val client = clientService.getByPublicId(clientPublicId) ?: return Response.InvalidClientId(clientIdParam)
        val clientId = requireNotNull(client.id) // Coming from persistence, OK

        if (!clientService.hasRedirectionUri(clientId, redirection)) {
            return Response.InvalidClientRedirection(clientPublicId, redirection)
        }

        // Parse requested response type and validate
        val responseType = trim(responseTypeParam) ?: return Response.ClientError(ClientErrorType.unsupported_response_type,
                                                                                  redirection, stateParam,
                                                                                  Separator.QUESTION_MARK)
        val responseTypes = responseTypesFromParams(responseType);
        if (responseTypes.isEmpty()) {
            return Response.ClientError(ClientErrorType.unsupported_response_type, redirection, stateParam, Separator.QUESTION_MARK)
        }
        val separator = getSeparatorForResponseType(responseTypes)

        // Nonce is mandatory
        val nonce = trim(nonceParam) ?: return Response.ClientError(ClientErrorType.invalid_request,
                                                                    redirection, stateParam, separator)

        // Check scope
        val flow = responseTypesToFlow(responseTypes)
        val requestScopes = trim(scopeParam) ?: return Response.ClientError(ClientErrorType.invalid_scope,
                                                                            redirection, stateParam, separator)
        val scopeSet = scopeService.validate(requestScopes, flow)

        if (scopeSet.invalidScopes.isNotEmpty()) {
            return Response.ClientError(ClientErrorType.invalid_scope, redirection, stateParam, separator)
        }

        // Check if this client is allowed to use this flow
        if (!clientService.hasFlow(clientId, flow)) {
            return Response.ClientError(ClientErrorType.unauthorized_client, redirection, stateParam, separator)
        }

        val authorizedByUser =
            userAuthorizationService.isAuthorized(aegaeonUserDetails, clientPublicId, redirection, scopeSet.validScopes)
        val prompt = promptFromString(promptParam)
        if (prompt == Prompt.none && !authorizedByUser) {
            return Response.ClientError(ClientErrorType.consent_required, redirection, stateParam, separator)
        } else if (!authorizedByUser || prompt == Prompt.login || prompt == Prompt.login) {
            return Response.UserConsentRequired()
        }

        // Last chance, if the server has any validator defined
        if (!authorizationInterceptor.validate(aegaeonUserDetails, client)) {
            return Response.ValidationError(client.publicId)
        }

        // OK, good to go.
        return when (flow) {
            Flow.AUTHORIZATION_CODE -> {
                val scopeString = scopeSet.validScopes.joinToString(" ") { scopeDto -> scopeDto.code }
                val codeDto = authorizationCodeService.create(aegaeonUserDetails.id,
                                                              clientId,
                                                              redirection,
                                                              scopeString,
                                                              responseType,
                                                              nonce)

                return if (codeDto != null) {
                    Response.AuthCode(redirection, codeDto.code, separator, stateParam)
                } else {
                    Response.InternalServerError("Unable to create authorization code.")
                }
            }

            Flow.IMPLICIT -> {
                TODO()
            }

            Flow.HYBRID -> {
                TODO()
            }

            Flow.CLIENT_CREDENTIALS -> {
                TODO()
            }

            else -> return Response.InternalServerError("Cannot handle flow $flow")
        }
    }

    private fun isOneOfFlows(flow: Flow?, vararg acceptableFlows: Flow): Boolean {
        return acceptableFlows.any { acceptableFlow -> acceptableFlow == flow }
    }
}

sealed class Response() {

    class AuthCode(

        val url: String,

        val code: String,

        val separator: Separator,

        val state: String?

    ) : Response()

    class ImplicitToken(

        @JsonProperty("id_token")
        val idToken: String? = null,

        @JsonProperty("access_token")
        val accessToken: String? = null,

        @JsonProperty("token_type")
        val tokenType: String = "Bearer",

        @JsonProperty("expires_in")
        val expiresIn: Long,

        val scope: String? = null

    ) : Response()

    class HybridToken() : Response()

    class ClientError(val error: ClientErrorType, val url: String, val state: String?, val separator: Separator) : Response()
    class UserConsentRequired() : Response()
    class InvalidClientId(val clientId: String?) : Response()
    class InvalidClientRedirection(val clientId: String?, val clientRedirection: String?) : Response()
    class InternalServerError(val cause: String) : Response()
    class ValidationError(val clientPublicId: String) : Response()
}

enum class ClientErrorType {
    interaction_required, // connected but not allowed
    login_required, // not connected
    consent_required, // connected but not allowed
    invalid_request,
    unauthorized_client,
    access_denied,
    unsupported_response_type,
    invalid_scope,
    server_error,
    temporarily_unavailable
}