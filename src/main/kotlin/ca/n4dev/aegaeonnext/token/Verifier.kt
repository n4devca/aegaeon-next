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

package ca.n4dev.aegaeonnext.token

/**
 *
 * Verifier.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 05 - 2019
 *
 */
interface Verifier {

    /**
     * @return The name of this verifier. Must be unique.
     */
    fun getVerifierName(): String

    /**
     * @return The type of token managed by this verifier.
     */
    fun getType(): TokenProviderType

    /**
     * @return if this verifier is enable and correctly initialize.
     */
    fun isEnable(): Boolean

    /**
     * Validate a token. This function should return false if the token is not
     * a JWT token. If the value is a jwt token, the claims must be extract and
     * date must be check.
     *
     * @param pToken The token to validate.
     * @return true or false.
     */
    fun validate(pToken: String): Boolean

    /**
     * Extract a jwt token.
     * @param pToken The token to extract.
     * @return An OAuthUser created from the jwt token or null.
     */
    fun extract(pToken: String): OAuthUserAndClaim?

    /**
     * Extract a jwt token, then validate it.
     * @param pToken The token to extract.
     * @return An OAuthUser created from the jwt token or null.
     */
    fun extractAndValidate(pToken: String): OAuthUserAndClaim?
}