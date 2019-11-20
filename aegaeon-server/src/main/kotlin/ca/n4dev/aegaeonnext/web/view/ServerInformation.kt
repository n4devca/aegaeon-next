/*
 * Copyright 2019 Remi Guillemette - n4dev.ca
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"), you may not use this file except in compliance
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

package ca.n4dev.aegaeonnext.web.view

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * ServerInformation.java
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 04 - 2019
 *
 */
data class ServerInformation(

    val issuer: String,

    @JsonProperty("authorization_endpoint")
    val authorizationEndpoint: String,

    @JsonProperty("token_endpoint")
    val tokenEndpoint: String,

    @JsonProperty("token_endpoint_auth_methods_supported")
    val tokenEndpointAuthMethodsSupported: List<String>,

    @JsonProperty("token_endpoint_auth_signing_alg_values_supported")
    val tokenEndpointAuthSigningAlgValuesSupported: List<String>,

    @JsonProperty("userinfo_endpoint")
    val userinfoEndpoint: String,

    @JsonProperty("check_session_iframe")
    val checkSessionIframe: String,

    @JsonProperty("end_session_endpoint")
    val endSessionEndpoint: String,

    @JsonProperty("jwks_uri")
    val jwksUri: String,

    @JsonProperty("registration_endpoint")
    val registrationEndpoint: String,

    @JsonProperty("scopes_supported")
    val scopesSupported: List<String>,

    @JsonProperty("response_types_supported")
    val responseTypesSupported: List<String>,

    @JsonProperty("acr_values_supported")
    val acrValuesSupported: List<String>,

    @JsonProperty("subject_types_supported")
    val subjectTypesSupported: List<String>,

    @JsonProperty("userinfo_signing_alg_values_supported")
    val userinfoSigningAlgValuesSupported: List<String>,

    @JsonProperty("userinfo_encryption_alg_values_supported")
    val userinfoEncryptionAlgValuesSupported: List<String>,

    @JsonProperty("userinfo_encryption_enc_values_supported")
    val userinfoEncryptionEncValuesSupported: List<String>,

    @JsonProperty("id_token_signing_alg_values_supported")
    val idTokenSigningAlgValuesSupported: List<String>,

    @JsonProperty("id_token_encryption_alg_values_supported")
    val idTokenEncryptionAlgValuesSupported: List<String>,

    @JsonProperty("id_token_encryption_enc_values_supported")
    val idTokenEncryptionEncValuesSupported: List<String>,

    @JsonProperty("request_object_signing_alg_values_supported")
    val requestObjectSigningAlgValuesSupported: List<String>,

    @JsonProperty("display_values_supported")
    val displayValuesSupported: List<String>,

    @JsonProperty("claim_types_supported")
    val claimTypesSupported: List<String>,

    @JsonProperty("claims_supported")
    val claimsSupported: List<String>,

    @JsonProperty("claims_parameter_supported")
    val claimsParameterSupported: Boolean,

    @JsonProperty("service_documentation")
    val serviceDocumentation: String,

    @JsonProperty("ui_locales_supported")
    val uiLocalesSupported: List<String>,

    @JsonProperty("op_policy_uri")
    val opPolicyUri: String,

    @JsonProperty("op_tos_uri")
    val opTosUri: String

)