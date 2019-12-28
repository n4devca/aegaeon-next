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

package ca.n4dev.aegaeonnext.core.web

import ca.n4dev.aegaeonnext.common.model.Separator
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.View
import org.springframework.web.servlet.view.RedirectView


/**
 *
 * InternalError
 *
 * Error displaying Aegaeon error page because we can either not respond to client
 * or this kind of error cannot be communicated to client.
 *
 * @author rguillemette
 * @since 2.0.0 - Dec 26 - 2019
 *
 */
sealed class InternalError(data: Map<String, Any?>) : ModelAndView("error", data) {

    class InvalidClientId(clientId: String?) :
        InternalError(mapOf("errorCode" to "InvalidClientId",
                            "clientId" to clientId))

    class InvalidClientRedirection(clientId: String?, clientRedirection: String?) :
        InternalError(mapOf("errorCode" to "InvalidClientRedirection",
                            "clientId" to clientId,
                            "clientRedirection" to clientRedirection))

    class ServerError(cause: String):
        InternalError(mapOf("errorCode" to "ServerError", "cause" to cause))

}

/**
 * ClientError
 *
 * Error displayed to client (by redirecting).
 *
 * https://tools.ietf.org/html/rfc6749#section-4.1.2.1
 */
sealed class ClientError(view: View) : ModelAndView(view) {

    class InvalidRequest(url: String, state: String?, separator: Separator):
        ClientError(RedirectView(url + "${separator.asUrlString}error=invalid_request" + addStateParam(state)))

    class UnsupportedResponseType(url: String, state: String?, separator: Separator):
        ClientError(RedirectView(url + "${separator.asUrlString}error=unsupported_response_type" + addStateParam(state)))

    class UnauthorizedClient(url: String, state: String?, separator: Separator):
        ClientError(RedirectView(url + "${separator.asUrlString}error=unauthorized_client" + addStateParam(state)))

    class AccessDenied(url: String, state: String?, separator: Separator):
        ClientError(RedirectView(url + "${separator.asUrlString}error=access_denied" + addStateParam(state)))

    class InvalidScope(url: String, state: String?, separator: Separator):
        ClientError(RedirectView(url + "${separator.asUrlString}error=invalid_scope" + addStateParam(state)))

    class ServerError(url: String, state: String?, separator: Separator):
        ClientError(RedirectView(url + "${separator.asUrlString}error=server_error" + addStateParam(state)))

    class TemporarilyUnavailable(url: String, state: String?, separator: Separator):
        ClientError(RedirectView(url + "${separator.asUrlString}error=temporarily_unavailable" + addStateParam(state)))

    class InteractionRequired(url: String, state: String?, separator: Separator):
        ClientError(RedirectView(url + "${separator.asUrlString}error=interaction_required" + addStateParam(state)))

}

private fun addStateParam(state: String?) : String {
    return if (state != null && state.isNotBlank()) {
        "&state=${state}"
    } else {
        ""
    }
}