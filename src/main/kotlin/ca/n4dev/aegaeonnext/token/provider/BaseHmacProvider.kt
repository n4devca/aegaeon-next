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

package ca.n4dev.aegaeonnext.token.provider

import ca.n4dev.aegaeonnext.config.AegaeonServerInfo
import ca.n4dev.aegaeonnext.token.*
import ca.n4dev.aegaeonnext.token.key.KeysProvider
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.JWSSigner
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.jwk.OctetSequenceKey
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.TemporalUnit
import java.util.*


/**
 *
 * BaseHmacProvider.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 06 - 2019
 *
 */
abstract class BaseHmacProvider(protected val pKeysProvider: KeysProvider, protected val serverInfo: AegaeonServerInfo) : Provider {

    protected var signer: JWSSigner? = null
    protected var keyId: String? = null
    protected var enabled = false

    init {
        val keySet = pKeysProvider.jwkSet

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
    override fun createToken(pOAuthUser: OAuthUser, pOAuthClient: OAuthClient, pTimeValue: Long?, pTemporalUnit: TemporalUnit,
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
    override fun createToken(pOAuthUser: OAuthUser, pOAuthClient: OAuthClient, pTimeValue: Long?, pTemporalUnit: TemporalUnit,
                             pPayloads: Map<String, Any>,
                             tokenType: TokenType): Token {

        val expiredIn = ZonedDateTime.now(ZoneOffset.UTC).plus(pTimeValue!!, pTemporalUnit)
        val instant = expiredIn.toInstant()
        val date = Date.from(instant)

        val builder = JWTClaimsSet.Builder()

        builder.expirationTime(date)
        builder.issuer(this.serverInfo.issuer)
        builder.subject(pOAuthUser.uniqueIdentifier)
        builder.audience(pOAuthClient.clientId)
        builder.issueTime(Date())

        if (!pPayloads.isEmpty()) {
            pPayloads.forEach { key, value -> builder.claim(key, value) }
        }

        val claimsSet = builder.build()
        val signedJWT = SignedJWT(JWSHeader(getJWSAlgorithm()), claimsSet)
        signedJWT.sign(this.signer!!)

        return Token(signedJWT.serialize(), expiredIn, tokenType)
    }


}