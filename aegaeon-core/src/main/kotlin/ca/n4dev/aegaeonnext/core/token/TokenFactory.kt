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

package ca.n4dev.aegaeonnext.core.token

import ca.n4dev.aegaeonnext.common.model.TokenProviderType
import ca.n4dev.aegaeonnext.common.model.fromTokenProviderTypeString
import ca.n4dev.aegaeonnext.common.utils.requireNonNull
import ca.n4dev.aegaeonnext.core.service.TokenType
import ca.n4dev.aegaeonnext.core.token.key.KeysProvider
import org.springframework.stereotype.Component
import java.time.temporal.TemporalUnit
import java.util.*


/**
 *
 * TokenFactory.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 14 - 2019
 *
 */
@Component
class TokenFactory(private val keysProvider: KeysProvider, providers: List<Provider>, verifiers: List<Verifier>) {

    private val tokenProviderHolder: Map<TokenProviderType, Provider> = providers.map { it.getType() to it }.toMap()
    private val tokenVerifierHolder: Map<TokenProviderType, Verifier> = verifiers.map { it.getType() to it }.toMap()

    /**
     * @return A uuid.
     */
    fun uniqueCode(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProvider A tokenProvider to create the token.
     * @return A Token or null.
     */
    fun createToken(pOAuthUser: OAuthUser,
                    pOAuthClient: OAuthClient,
                    tokenType: TokenType,
                    pTokenProvider: Provider,
                    pTimeValue: Long,
                    pTemporalUnit: TemporalUnit,
                    pPayloads: Map<String, Any>): Token {


        return pTokenProvider.createToken(pOAuthUser,
            pOAuthClient,
            pTimeValue,
            pTemporalUnit,
            pPayloads,
            tokenType)
    }


    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProviderName The name of the token provider to use. Must be a spring bean.
     * @return A Token or null.
     */
    fun createToken(pOAuthUser: OAuthUser, pOAuthClient: OAuthClient,
                    tokenType: TokenType, pTokenProviderType: TokenProviderType,
                    pTimeValue: Long, pTemporalUnit: TemporalUnit, pPayloads: Map<String, Any>): Token {

        val tp = requireNonNull(tokenProviderHolder[pTokenProviderType]) {
            Exception("$pTokenProviderType cannot be found.")
        }

        return createToken(pOAuthUser, pOAuthClient, tokenType, tp, pTimeValue, pTemporalUnit, pPayloads);
    }

    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProviderName The name of the token provider to use. Must be a spring bean.
     * @return A Token or null.
     */
    fun createToken(pOAuthUser: OAuthUser, pOAuthClient: OAuthClient, tokenType: TokenType, pTokenProviderName: String,
                    pTimeValue: Long, pTemporalUnit: TemporalUnit, pPayloads: Map<String, Any>): Token {

        val tokenProviderType = requireNonNull(
            fromTokenProviderTypeString(pTokenProviderName)) {
            Exception("$pTokenProviderName cannot be found.")
        }

        return createToken(pOAuthUser, pOAuthClient, tokenType, tokenProviderType, pTimeValue, pTemporalUnit, pPayloads);
    }

    /**
     * Create a Token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client requesting a token.
     * @param pTokenProviderName The name of the token provider to use. Must be a spring bean.
     * @return A Token or null.
     */
    fun createToken(pOAuthUser: OAuthUser, pOAuthClient: OAuthClient, tokenType: TokenType,
                    pTimeValue: Long, pTemporalUnit: TemporalUnit, pPayloads: Map<String, Any>): Token {
        return createToken(pOAuthUser, pOAuthClient, tokenType, pOAuthClient.signingAlg, pTimeValue, pTemporalUnit, pPayloads);
    }

    fun getSupportedAlgorithm(): List<String> {
        return tokenProviderHolder.entries.map { it.value.getAlgorithmName() }
    }

    @Throws(Exception::class)
    fun publicJwks(): String {
        return keysProvider.toPublicJson()
    }

}