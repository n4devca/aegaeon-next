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

            val t: TokenResponse? = transactionTemplate.execute(fun(_): TokenResponse {
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

        try {


            TODO()

        } catch (exception: Exception) {
            LOGGER.error("TokenServicesFacade#handleRefreshTokenRequest error: ", exception)
            return TokenResponse.ServerError()
        }

    }

    @Transactional
    @PreAuthorize("isAuthenticated() and #userDetails.id == principal.id")
    fun handleClientCredTokenRequest(clientRedirection: String?,
                                     scopes: String?,
                                     userDetails: AegaeonUserDetails): TokenResponse {
        try {
            TODO()
        } catch (exception: Exception) {
            LOGGER.error("TokenServicesFacade#handleRefreshTokenRequest error: ", exception)
            return TokenResponse.ServerError()
        }
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
                               userDetails: AegaeonUserDetails): AuthorizeResponse {


        try {

            val authorizeResponse = transactionTemplate.execute(fun(_): AuthorizeResponse {

                val clientPublicId = trim(clientIdParam) ?: return AuthorizeResponse.InvalidClientId(clientIdParam)
                val redirection = trim(clientRedirectParam) ?: return AuthorizeResponse.InvalidClientRedirection(clientPublicId, clientRedirectParam)

                // Get client
                val client = clientService.getByPublicId(clientPublicId) ?: return AuthorizeResponse.InvalidClientId(clientIdParam)
                val clientId = requireNotNull(client.id) // Coming from persistence, OK

                if (!clientService.hasRedirectionUri(clientId, redirection)) {
                    return AuthorizeResponse.InvalidClientRedirection(clientPublicId, redirection)
                }

                // Parse requested response type and validate
                val responseType = trim(responseTypeParam) ?: return AuthorizeResponse.ClientError(ClientErrorType.unsupported_response_type,
                                                                                                   redirection, stateParam,
                                                                                                   Separator.QUESTION_MARK)
                val responseTypes = responseTypesFromParams(responseType);
                if (responseTypes.isEmpty()) {
                    return AuthorizeResponse.ClientError(ClientErrorType.unsupported_response_type, redirection, stateParam, Separator.QUESTION_MARK)
                }
                val separator = getSeparatorForResponseType(responseTypes)

                // Nonce is mandatory
                val nonce = trim(nonceParam) ?: return AuthorizeResponse.ClientError(ClientErrorType.invalid_request,
                                                                                     redirection, stateParam, separator)

                // Check scope
                val flow = responseTypesToFlow(responseTypes)
                val requestScopes = trim(scopeParam) ?: return AuthorizeResponse.ClientError(ClientErrorType.invalid_scope,
                                                                                             redirection, stateParam, separator)
                val scopeSet = scopeService.validate(requestScopes, flow)

                if (scopeSet.invalidScopes.isNotEmpty()) {
                    return AuthorizeResponse.ClientError(ClientErrorType.invalid_scope, redirection, stateParam, separator)
                }

                // Check if this client is allowed to use this flow
                if (!clientService.hasFlow(clientId, flow)) {
                    return AuthorizeResponse.ClientError(ClientErrorType.unauthorized_client, redirection, stateParam, separator)
                }

                val authorizedByUser =
                    userAuthorizationService.isAuthorized(userDetails, clientPublicId, redirection, scopeSet.validScopes)
                val prompt = promptFromString(promptParam)
                if (prompt == Prompt.none && !authorizedByUser) {
                    return AuthorizeResponse.ClientError(ClientErrorType.consent_required, redirection, stateParam, separator)
                } else if (!authorizedByUser || prompt == Prompt.login || prompt == Prompt.login) {
                    return AuthorizeResponse.UserConsentRequired()
                }

                // Last chance, if the server has any validator defined
                if (!authorizationInterceptor.validate(userDetails, client)) {
                    return AuthorizeResponse.ValidationError(client.publicId)
                }

                // OK, good to go.
                val scopeString = scopeSet.validScopes.joinToString(" ") { scopeDto -> scopeDto.code }

                return when (flow) {
                    Flow.AUTHORIZATION_CODE -> {
                        val codeDto = authorizationCodeService.create(userDetails.id,
                                                                      clientId,
                                                                      redirection,
                                                                      scopeString,
                                                                      responseType,
                                                                      nonce)

                        return if (codeDto != null) {
                            AuthorizeResponse.AuthCode(redirection, codeDto.code, separator, stateParam)
                        } else {
                            AuthorizeResponse.InternalServerError("Unable to create authorization code.")
                        }
                    }

                    Flow.IMPLICIT -> {

                        val userDto = requireNotNull(userService.getUserById(userDetails.id))
                        val requestedScopes = scopeSet.validScopes
                        val payload = userService.createPayload(userDto, requestedScopes)

                        val idTokenDto = idTokenService.createToken(userDto, requestedScopes, nonce, payload, userDetails)
                        val accessTokenDto = accessTokenService.createToken(userDto, requestedScopes, payload, userDetails)

                        return AuthorizeResponse.ImplicitToken(redirection,
                                                               idTokenDto?.token,
                                                               accessTokenDto?.token,
                                                               accessTokenDto?.validUntil?.epochSecond ?: 0L,
                                                               scopeString,
                                                               stateParam)
                    }

                    Flow.HYBRID -> {
                        TODO()
                    }

                    else -> return AuthorizeResponse.InternalServerError("Cannot handle flow $flow")
                }
            })

            return authorizeResponse ?: AuthorizeResponse.InternalServerError("Unhandled exception.")

        } catch (exception: Exception) {
            LOGGER.error("Unhandled exception", exception)
            return AuthorizeResponse.InternalServerError("Unhandled exception.")
        }
    }
}

private const val BEARER = "Bearer"

/**
 * AuthorizeResponse.java
 *
 * An authorize response following a call to /authorize endpoint.
 *
 * @author by rguillemette
 * @since Jan 6, 2020
 */
sealed class AuthorizeResponse() {

    class AuthCode(

        val url: String,

        val code: String,

        val separator: Separator,

        val state: String?

    ) : AuthorizeResponse()

    class ImplicitToken(

        val url: String,

        val idToken: String? = null,

        val accessToken: String? = null,

        val expiresIn: Long,

        val scope: String? = null,

        val state: String? = null,

        val tokenType: String = BEARER

    ) : AuthorizeResponse()

    class HybridToken() : AuthorizeResponse()

    class ClientError(val error: ClientErrorType, val url: String, val state: String?, val separator: Separator) : AuthorizeResponse()
    class UserConsentRequired() : AuthorizeResponse()
    class InvalidClientId(val clientId: String?) : AuthorizeResponse()
    class InvalidClientRedirection(val clientId: String?, val clientRedirection: String?) : AuthorizeResponse()
    class InternalServerError(val cause: String) : AuthorizeResponse()
    class ValidationError(val clientPublicId: String) : AuthorizeResponse()
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



/**
 * TokenResponse.java
 *
 * A token response following a call to /token endpoint.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */
sealed class TokenResponse() {

    data class Token(

        @JsonProperty("id_token")
        val idToken: String? = null,

        @JsonProperty("access_token")
        val accessToken: String? = null,

        @JsonProperty("expires_in")
        val expiresIn: Long,

        val scope: String? = null,

        @JsonProperty("refresh_token")
        val refreshToken: String? = null,

        @JsonProperty("token_type")
        val tokenType: String = BEARER

    ) : TokenResponse();


    class InvalidClient() : TokenResponse() {
        val error = "invalid_client"
    }

    class InvalidRequest() : TokenResponse() {
        val error = "invalid_request"
    }

    class InvalidGrant() : TokenResponse() {
        val error = "invalid_grant"
    }

    class UnauthorizedClient() : TokenResponse() {
        val error = "unauthorized_client"
    }

    class UnsupportedGrantType() : TokenResponse() {
        val error = "unsupported_grant_type"
    }

    class InvalidScope() : TokenResponse() {
        val error = "invalid_scope"
    }

    class ServerError() : TokenResponse() {
        val error = "server_error"
    }
}