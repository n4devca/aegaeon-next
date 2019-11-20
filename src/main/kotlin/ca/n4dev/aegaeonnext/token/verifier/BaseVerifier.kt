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

package ca.n4dev.aegaeonnext.token.verifier

import ca.n4dev.aegaeonnext.config.AegaeonServerInfo
import ca.n4dev.aegaeonnext.token.Claims
import ca.n4dev.aegaeonnext.token.OAuthUser
import ca.n4dev.aegaeonnext.token.OAuthUserAndClaim
import ca.n4dev.aegaeonnext.token.Verifier
import com.nimbusds.jwt.SignedJWT


/**
 *
 * BaseVerifier.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 04 - 2019
 *
 */

abstract class BaseVerifier(private val serverInfo: AegaeonServerInfo) : Verifier {

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#validate(java.lang.String)
     */
    override fun validate(pToken: String): Boolean {

        try {
            val signedJWT = SignedJWT.parse(pToken)
            return signedJWT.verify(getJwsVerifier()) && signedJWT.jwtClaimsSet.issuer == serverInfo.issuer
        } catch (e: Exception) {
            // ignore
        }

        return false
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#extract(java.lang.String)
     */
    override fun extract(pToken: String): OAuthUserAndClaim? {
        try {

            val signedJWT = SignedJWT.parse(pToken)
            return extract(signedJWT)

        } catch (e: Exception) {
            // TODO: handle exception
        }

        return null
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#extractAndValidate(java.lang.String)
     */
    override fun extractAndValidate(pToken: String): OAuthUserAndClaim? {
        try {
            val signedJWT = SignedJWT.parse(pToken)

            if (signedJWT.verify(getJwsVerifier()) && signedJWT.jwtClaimsSet.issuer == serverInfo.issuer) {

                return extract(signedJWT)
            }

        } catch (e: Exception) {
            // TODO: handle exception
        }

        return null
    }

    private fun extract(pSignedJWT: SignedJWT): OAuthUserAndClaim? {
        try {

            val jwtClaimsSet = pSignedJWT.jwtClaimsSet

            val sub = jwtClaimsSet.subject
            val name = jwtClaimsSet.getStringClaim(Claims.NAME.value)

            val claims = jwtClaimsSet.claims
                .filter { it.key != Claims.NAME.value && it.key != Claims.SUB.value }
                .map { Pair(it.key, it.value.toString()) }.toMap()

            return OAuthUserAndClaim(OAuthUser(uniqueIdentifier = sub, name = name), claims)

        } catch (e: Exception) {
            // ignore
        }

        return null
    }

}

