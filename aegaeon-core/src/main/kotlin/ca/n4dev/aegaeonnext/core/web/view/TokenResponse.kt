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
package ca.n4dev.aegaeonnext.core.web.view

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * TokenResponse.java
 *
 * A token response following a call to /token endpoint.
 *
 * @author by rguillemette
 * @since May 9, 2017
 */

private const val BEARER = "Bearer"

sealed class TokenResponse {

    data class Success(

        @JsonProperty("id_token")
        val idToken: String? = null,

        @JsonProperty("access_token")
        val accessToken: String? = null,

        @JsonProperty("token_type")
        val tokenType: String? = null,

        @JsonProperty("expires_in")
        val expiresIn: Long? = null,

        val scope: String? = null,

        @JsonProperty("refresh_token")
        val refreshToken: String? = null
    ) : TokenResponse();

    data class Error(

        val error: String,

        @JsonProperty("error_description")
        val description: String?,

        @JsonProperty("error_uri")
        val uri: String?,

        val state: String?

    ) : TokenResponse()
}
