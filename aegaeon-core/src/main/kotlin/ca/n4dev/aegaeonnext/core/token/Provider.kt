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

import com.nimbusds.jose.JWSAlgorithm
import java.time.temporal.TemporalUnit


/**
 *
 * Provider.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 05 - 2019
 *
 */
interface Provider {

    fun getJWSAlgorithm(): JWSAlgorithm

    /**
     * @return The type of token created by this provider.
     */
    fun getType(): TokenProviderType

    /**
     * If this provider is enabled and has been correctly instanciate.
     * @return true or false.
     */
    fun isEnabled(): Boolean

    /**
     * Create a token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client used during authentication.
     * @return A token.
     */
    fun createToken(pOAuthUser: OAuthUser,
                    pOAuthClient: OAuthClient,
                    pTimeValue: Long,
                    pTemporalUnit: TemporalUnit,
                    tokenType: TokenType): Token

    /**
     * Create a token.
     * @param pOAuthUser The authenticated user.
     * @param pOAuthClient The client used during authentication.
     * @param pPayloads The payload to add to the token.
     * @return A token.
     */
    fun createToken(pOAuthUser: OAuthUser,
                    pOAuthClient: OAuthClient,
                    pTimeValue: Long,
                    pTemporalUnit: TemporalUnit,
                    pPayloads: Map<String, Any>,
                    tokenType: TokenType): Token

    fun getAlgorithmName(): String {
        return getJWSAlgorithm().toString()
    }
}