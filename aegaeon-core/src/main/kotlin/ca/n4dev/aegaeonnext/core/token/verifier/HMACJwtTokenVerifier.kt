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

package ca.n4dev.aegaeonnext.core.token.verifier

import ca.n4dev.aegaeonnext.common.model.TokenProviderType
import ca.n4dev.aegaeonnext.core.config.AegaeonServerInfo
import ca.n4dev.aegaeonnext.core.token.key.KeysProvider
import com.nimbusds.jose.JWSVerifier
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jose.jwk.OctetSequenceKey
import org.springframework.stereotype.Component


/**
 *
 * HMACJwtTokenVerifier.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 08 - 2019
 *
 */
sealed class BaseHMACJwtTokenVerifier(keysProvider: KeysProvider, serverInfo: AegaeonServerInfo)
    : BaseVerifier(serverInfo) {

    private var verifier: JWSVerifier? = null

    init {

        val keySet = keysProvider.jwkSet

        for (jwk in keySet.keys) {

            if (jwk is OctetSequenceKey) {
                this.verifier = MACVerifier(jwk.toByteArray())
                break
            }
        }
    }

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.api.token.verifier.TokenVerifier#isEnable()
     */
    override fun isEnable() = verifier != null

    /* (non-Javadoc)
     * @see ca.n4dev.aegaeon.server.token.verifier.BaseJwtVerifier#getJWSVerifier()
     */
    override fun getJwsVerifier() = verifier
}

@Component
class HMAC256JwtTokenVerifier(keysProvider: KeysProvider, serverInfo: AegaeonServerInfo)
    : BaseHMACJwtTokenVerifier(keysProvider, serverInfo) {
    override fun getType(): TokenProviderType = TokenProviderType.HS256
}

@Component
class HMAC512JwtTokenVerifier(keysProvider: KeysProvider, serverInfo: AegaeonServerInfo)
    : BaseHMACJwtTokenVerifier(keysProvider, serverInfo) {
    override fun getType(): TokenProviderType = TokenProviderType.HS512
}
