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

package ca.n4dev.aegaeonnext.common.model


/**
 * ResponseType.java
 *
 * Represent a requested response type sent to an authorization or token controller.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 11 - 2018
 */
enum class ResponseType {
    id_token,
    code,
    token;
}

enum class Separator(val asUrlString: String) {
    FRAGMENT("#"),
    QUESTION_MARK("?")
}

fun responseTypesFromParams(responseTypeStr: String?): Set<ResponseType> {
    return if (!responseTypeStr.isNullOrBlank()) {
        val parameters = responseTypeStr.trim().split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return parameters.map { p -> responseTypesFromString(p) }.filterNotNull().toSet()
    } else {
        emptySet()
    }
}

fun responseTypesFromString(responseTypeStr: String?): ResponseType? {

    return if (!responseTypeStr.isNullOrBlank()) {
        if (ResponseType.id_token.toString().equals(responseTypeStr, ignoreCase = true)) {
            ResponseType.id_token
        } else if (ResponseType.token.toString().equals(responseTypeStr, ignoreCase = true)) {
            ResponseType.token
        } else if (ResponseType.code.toString().equals(responseTypeStr, ignoreCase = true)) {
            ResponseType.code
        } else {
            null
        }
    } else {
        null
    }
}

fun getSeparatorForResponseType(responseTypes: Set<ResponseType>): Separator {
    return if (responseTypes.isNotEmpty()) {
        if (responseTypes.size == 1 && responseTypes.contains(ResponseType.id_token)) {
            Separator.FRAGMENT
        } else if (responseTypes.contains(ResponseType.token)) {
            Separator.FRAGMENT
        } else {
            Separator.QUESTION_MARK
        }
    } else {
        Separator.QUESTION_MARK
    }
}
