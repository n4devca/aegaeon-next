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

import ca.n4dev.aegaeonnext.common.model.Separator
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.util.UriComponentsBuilder

/**
 * UriBuilder.java
 *
 * Useful static functions to deal with url building.
 *
 * @author by rguillemette
 * @since May 17, 2017
 */
const val URI_REDIRECTION_ERROR_KEY = "error"
const val URI_REDIRECTION_DESC_KEY = "error_description"
const val URI_PARAM_PROMPT = "prompt"
const val URI_PARAM_STATE = "state"
const val URI_PARAM_CLIENT_ID = "client_id"
const val URI_PARAM_REDIRECTION_URL = "redirect_uri"
const val URI_PARAM_RESPONSE_TYPE = "response_type"
const val URI_PARAM_NONCE = "nonce"
const val URI_PARAM_SCOPE = "scope"
const val URI_PARAM_DISPLAY = "display"
const val URI_PARAM_IDTOKENHINT = "id_token_hint"
const val URI_PARAM_ID_TOKEN = "id_token"
const val URI_PARAM_ACCESS_TOKEN = "access_token"
const val URI_PARAM_REFRESH_TOKEN = "refresh_token"
const val URI_PARAM_TOKEN_TYPE = "token_type"
const val URI_PARAM_EXPIRES_IN = "expires_in"
const val URI_PARAM_CODE = "code"
const val URI_PARAM_ERROR = "error"

private fun append(pParams: MultiValueMap<String, String>, pKey: String, pValue: String?) {
    if (!pKey.isNullOrBlank() && !pValue.isNullOrBlank()) {
        pParams.add(pKey, pValue)
    }
}

private fun append(pParams: MultiValueMap<String, String>, pKey: String, pValue: Long?) {
    if (!pKey.isNullOrBlank() && pValue != null) {
        pParams.add(pKey, pValue.toString())
    }
}

fun buildAuthorizationCodeRedirect(url: String, code: String, state: String?, separator: Separator): String {

    val params = LinkedMultiValueMap<String, String>()
    append(params, URI_PARAM_STATE, state)
    append(params, URI_PARAM_CODE, code)

    val builder = if (separator == Separator.FRAGMENT) {
        val queryParams = UriComponentsBuilder.fromHttpUrl(url).queryParams(params).build().query
        UriComponentsBuilder.fromHttpUrl(url).fragment(queryParams)
    } else {
        UriComponentsBuilder.fromHttpUrl(url).queryParams(params)
    }

    val uriComponents = builder.build()
    return uriComponents.toUri().toString()
}

object UriBuilder {


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




}
