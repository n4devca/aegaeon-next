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

package ca.n4dev.aegaeonnext.common.model

/**
 *
 * Flow.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Sep 26 - 2019
 *
 */
enum class Flow {
    IMPLICIT,
    AUTHORIZATION_CODE,
    CLIENT_CREDENTIALS,
    REFRESH_TOKEN,
    PASSWORD, // Not implemented
    HYBRID // Not implemented
}



fun responseTypesToFlow(responseTypes: Set<ResponseType>): Flow {

    require(responseTypes.isNotEmpty()) {
        "This function does not accept an empty set of ResponseType"
    }

    return when {
        containsOnly(responseTypes, ResponseType.code) -> {
            Flow.AUTHORIZATION_CODE // openid
        }
        containsAll(responseTypes, ResponseType.id_token, ResponseType.token) -> {
            Flow.IMPLICIT // openid
        }
        containsOnly(responseTypes, ResponseType.token) -> {
            Flow.IMPLICIT // oauth
        }
        else -> {
            Flow.HYBRID
        }
    }
}

private fun <A> containsOnly(set: Set<A>, a : A): Boolean {
    return set.size == 1 && set.contains(a)
}

private fun <A> containsAll(set: Set<A>, vararg a : A): Boolean {
    return a.size == set.size && set.containsAll(a.toList())
}