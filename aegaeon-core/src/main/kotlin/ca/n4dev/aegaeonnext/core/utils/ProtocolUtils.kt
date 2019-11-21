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

import ca.n4dev.aegaeonnext.common.model.Flow
import ca.n4dev.aegaeonnext.data.db.entities.Grant

/**
 *
 * ProtocolUtils.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 10 - 2019
 *
 */


const val FLOW_AUTH_CODE = "code"
const val FLOW_IMPLICIT_FULL = "id_token token"
const val FLOW_IMPLICIT_ONLYID = "id_token"
const val FLOW_HYBRID_CODE_AND_ID = "code id_token"
const val FLOW_HYBRID_CODE_AND_ID_AND_TOKEN = "code id_token token"
const val FLOW_HYBRID_CODE_AND_TOKEN = "code token"
const val FLOW_OAUTH2_CLIENT_CRED = "client_credentials"
const val FLOW_OAUTH2_REFRESH_TOKEN = "refresh_token"

enum class Protocol {
    OPENID,
    OAUTH
}

data class GrantRequest(val flow: Flow, val composition: List<Grant> = emptyList(), val protocol: Protocol)

fun flowOf(flowName: String): Flow? {

    for (flow in Flow.values()) {
        if (flow.toString().equals(flowName, ignoreCase = true)) {
            return flow
        }
    }

    return null
}


fun flowOfResponseType(responseTypeParam: String?): GrantRequest? {

    /*
         * code => Authorization Code
         * id_token [token] => implicit
         * token => implicit (oauth)
         * code id_token => hybrid
         * code token => hybrid
         * code id_token token => hybrid
         */
    return if (!responseTypeParam.isNullOrBlank()) {
        if (FLOW_AUTH_CODE.equals(responseTypeParam, ignoreCase = true)) {
            GrantRequest(Flow.AUTHORIZATION_CODE, listOf(Grant.code), Protocol.OPENID)
        } else if (FLOW_IMPLICIT_ONLYID.equals(responseTypeParam, ignoreCase = true) || FLOW_IMPLICIT_FULL.equals(responseTypeParam, ignoreCase = true)) {
            GrantRequest(Flow.IMPLICIT, listOf(Grant.id_token, Grant.token), Protocol.OPENID)
        } else if (FLOW_HYBRID_CODE_AND_ID.equals(responseTypeParam, ignoreCase = true)) {
            GrantRequest(Flow.HYBRID, listOf(Grant.code, Grant.id_token), Protocol.OPENID)
        } else if (FLOW_HYBRID_CODE_AND_ID_AND_TOKEN.equals(responseTypeParam, ignoreCase = true)) {
            GrantRequest(Flow.HYBRID, listOf(Grant.code, Grant.id_token, Grant.token), Protocol.OPENID)
        } else if (FLOW_HYBRID_CODE_AND_TOKEN.equals(responseTypeParam, ignoreCase = true)) {
            GrantRequest(Flow.HYBRID, listOf(Grant.code, Grant.token), Protocol.OPENID)
        } else {
            null
        }
    } else {
        null
    }

}