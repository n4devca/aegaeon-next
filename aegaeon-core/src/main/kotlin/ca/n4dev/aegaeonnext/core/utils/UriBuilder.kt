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
package ca.n4dev.aegaeonnext.core.utils

import org.springframework.util.MultiValueMap

/**
 * UriBuilder.java
 *
 * Useful static functions to deal with url building.
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
object UriBuilder {

    val REDIRECTION_ERROR_KEY = "error"
    val REDIRECTION_DESC_KEY = "error_description"

    val PARAM_PROMPT = "prompt"
    val PARAM_STATE = "state"
    val PARAM_CLIENT_ID = "client_id"
    val PARAM_REDIRECTION_URL = "redirect_uri"
    val PARAM_RESPONSE_TYPE = "response_type"
    val PARAM_NONCE = "nonce"
    val PARAM_SCOPE = "scope"
    val PARAM_DISPLAY = "display"
    val PARAM_IDTOKENHINT = "id_token_hint"
    val PARAM_ID_TOKEN = "id_token"
    val PARAM_ACCESS_TOKEN = "access_token"
    val PARAM_REFRESH_TOKEN = "refresh_token"
    val PARAM_TOKEN_TYPE = "token_type"
    val PARAM_EXPIRES_IN = "expires_in"
    val PARAM_CODE = "code"

//    fun build(pUrl: String, pTokenResponse: TokenResponse, pState: String, pAsFragment: Boolean): String {
//        val params = LinkedMultiValueMap<String, String>()
//
//        append(params, PARAM_ACCESS_TOKEN, pTokenResponse.getAccessToken())
//        append(params, PARAM_REFRESH_TOKEN, pTokenResponse.getRefreshToken())
//        append(params, PARAM_ID_TOKEN, pTokenResponse.getIdToken())
//
//        append(params, PARAM_TOKEN_TYPE, pTokenResponse.getTokenType())
//        append(params, PARAM_EXPIRES_IN, pTokenResponse.getExpiresIn())
//        append(params, PARAM_SCOPE, pTokenResponse.getScope())
//        append(params, PARAM_STATE, pState)
//
//        return build(pUrl, params, pAsFragment)
//    }
//
//    fun build(pUrl: String, pOpenIdException: OpenIdException, pAsFragment: Boolean): String {
//        val params = LinkedMultiValueMap<String, String>()
//        params.setAll(buildModel(pOpenIdException))
//        return build(pUrl, params, pAsFragment)
//    }
//
//    fun build(pUrl: String, pParam: MultiValueMap<String, String>, pAsFragment: Boolean): String {
//
//        var builder = UriComponentsBuilder.fromHttpUrl(pUrl)
//
//        if (pAsFragment) {
//            val queryParams = UriComponentsBuilder.fromHttpUrl(pUrl).queryParams(pParam).build().query
//            builder = builder.fragment(queryParams)
//        } else {
//            builder = builder.queryParams(pParam)
//        }
//        val uriComponents = builder.build()
//        return uriComponents.toUri().toString()
//    }
//
//    fun buildModel(pOpenIdException: OpenIdException): Map<String, String> {
//        val model = LinkedHashMap<String, String>()
//
//        model[REDIRECTION_ERROR_KEY] = OpenIdErrorType.fromServerCode(pOpenIdException.getCode()).toString();
//        model[REDIRECTION_DESC_KEY] = pOpenIdException.getMessage()
//        model[PARAM_STATE] = pOpenIdException.getClientState()
//
//        return model
//    }

    private fun append(pParams: MultiValueMap<String, String>, pKey: String, pValue: String) {
        if (!pKey.isNullOrBlank() && !pValue.isNullOrBlank()) {
            pParams.add(pKey, pValue)
        }
    }

    private fun append(pParams: MultiValueMap<String, String>, pKey: String, pValue: Long?) {
        if (!pKey.isNullOrBlank() && pValue != null) {
            pParams.add(pKey, pValue.toString())
        }
    }


}
