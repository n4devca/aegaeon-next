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
import ca.n4dev.aegaeonnext.token.TokenProviderType
import ca.n4dev.aegaeonnext.token.key.KeysProvider
import com.nimbusds.jose.JWSAlgorithm
import org.springframework.stereotype.Component

/**
 *
 * RSA256JwtTokenProvider.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 07 - 2019
 *
 */
@Component
class RSA256JwtTokenProvider(pKeysProvider: KeysProvider, pServerInfo: AegaeonServerInfo) : BaseRSAProvider(pKeysProvider, pServerInfo) {
    override fun getJWSAlgorithm(): JWSAlgorithm = JWSAlgorithm.RS256
    override fun getType() = TokenProviderType.RSA_RS256
}

@Component
class RSA512JwtTokenProvider(pKeysProvider: KeysProvider, pServerInfo: AegaeonServerInfo) : BaseRSAProvider(pKeysProvider, pServerInfo) {
    override fun getJWSAlgorithm(): JWSAlgorithm = JWSAlgorithm.RS512
    override fun getType() = TokenProviderType.RSA_RS512
}
