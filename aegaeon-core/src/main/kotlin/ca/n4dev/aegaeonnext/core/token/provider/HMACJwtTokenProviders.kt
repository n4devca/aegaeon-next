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

package ca.n4dev.aegaeonnext.core.token.provider

import ca.n4dev.aegaeonnext.core.config.AegaeonServerInfo
import ca.n4dev.aegaeonnext.core.token.*
import ca.n4dev.aegaeonnext.core.token.key.KeysProvider
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.TemporalUnit
import java.util.*


@Component
class HMAC256JwtTokenProvider(keysProvider: KeysProvider, serverInfo: AegaeonServerInfo) : BaseHmacProvider(keysProvider, serverInfo) {
    override fun getJWSAlgorithm(): JWSAlgorithm = JWSAlgorithm.HS256
    override fun getType() = TokenProviderType.HMAC_HS256
}

@Component
class HMAC512JwtTokenProvider(keysProvider: KeysProvider, serverInfo: AegaeonServerInfo) : BaseHmacProvider(keysProvider, serverInfo) {
    override fun getJWSAlgorithm(): JWSAlgorithm = JWSAlgorithm.HS512
    override fun getType() = TokenProviderType.HMAC_HS512
}

sealed class BaseHmacProvider(keysProvider: KeysProvider, private val serverInfo: AegaeonServerInfo) : Provider {

    private var signer: JWSSigner? = null
    private var keyId: String? = null
    private var enabled = false

    init {
        val keySet = keysProvider.jwkSet

        for (jwk in keySet.keys) {
            if (jwk.isPrivate) {
                if (jwk is OctetSequenceKey) {
                    keyId = jwk.getKeyID()
                    signer = MACSigner(jwk)
                    break
                }
            }
        }

        if (signer != null) {
            enabled = true
        }
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#createToken(ca.n4dev.aegaeon.api.token.OAuthUser, ca.n4dev.aegaeon.api.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit)
     */
    override fun createToken(pOAuthUser: OAuthUser, pOAuthClient: OAuthClient, pTimeValue: Long, pTemporalUnit: TemporalUnit,
                             tokenType: TokenType): Token {
        return createToken(pOAuthUser, pOAuthClient, pTimeValue, pTemporalUnit, emptyMap(), tokenType)
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#isEnabled()
     */
    override fun isEnabled(): Boolean {
        return this.enabled
    }


    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.provider.TokenProvider#createToken(ca.n4dev.aegaeon.api.token.OAuthUser, ca.n4dev.aegaeon.api.token.OAuthClient, java.lang.Long, java.time.temporal.TemporalUnit, java.util.List)
     */
    override fun createToken(pOAuthUser: OAuthUser, pOAuthClient: OAuthClient, pTimeValue: Long, pTemporalUnit: TemporalUnit,
                             pPayloads: Map<String, Any>,
                             tokenType: TokenType): Token {

        val expiredIn = ZonedDateTime.now(ZoneOffset.UTC).plus(pTimeValue, pTemporalUnit)
        val date = Date.from(expiredIn.toInstant())

        val builder = JWTClaimsSet.Builder()

        builder.expirationTime(date)
        builder.issuer(serverInfo.issuer)
        builder.subject(pOAuthUser.uniqueIdentifier)
        builder.audience(pOAuthClient.clientId)
        builder.issueTime(Date())

        if (pPayloads.isNotEmpty()) {
            pPayloads.forEach { (key, value) -> builder.claim(key, value) }
        }

        val claimsSet = builder.build()
        val signedJWT = SignedJWT(JWSHeader(getJWSAlgorithm()), claimsSet)
        signedJWT.sign(this.signer!!)

        return Token(signedJWT.serialize(), expiredIn, tokenType)
    }


}
