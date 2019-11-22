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

import ca.n4dev.aegaeonnext.common.model.AccessToken
import java.time.LocalDateTime
import java.time.ZonedDateTime

/**
 *
 * BaseTokenService.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 21 - 2019
 *
 */
abstract class BaseTokenService {

    abstract fun getManagedTokenType(): TokenType

    protected fun accessTokenToTokenDto(accessToken: AccessToken) =
        TokenDto(
            accessToken.id!!,
            accessToken.token,
            getManagedTokenType(),
            accessToken.scopes,
            accessToken.validUntil
        )
}

enum class TokenType {
    ACCESS_TOKEN,
    ID_TOKEN,
    REFRESH_TOKEN
}

data class TokenDto(
    val id: Long,
    val token: String,
    val tokenType: TokenType,
    val scopes: String,
    val validUntil: LocalDateTime
)