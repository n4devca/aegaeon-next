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

import java.util.*

/**
 * ResponseType.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Feb 11 - 2018
 */
enum class ResponseType {
    id_token,
    code,
    token;


    companion object {

        /**
         * Get a ResponseType from a String
         * @param pResponseType The ResponseType as String
         * @return A ResponseType or null.
         */
        fun from(pResponseType: String): ResponseType? {
            for (rt in ResponseType.values()) {
                if (rt.toString().equals(pResponseType, ignoreCase = true)) {
                    return rt
                }
            }
            return null
        }

        /**
         * Get many ResponseTypes from a String. Value are split by space
         * @param pResponseTypeStr The ResponseType as String
         * @return One or many ResponseType or empty.
         */
        fun of(pResponseTypeStr: String?): List<ResponseType> {
            val responseTypes = ArrayList<ResponseType>()

            if (pResponseTypeStr != null && !pResponseTypeStr.isEmpty()) {
                val args = pResponseTypeStr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                for (r in args) {
                    for (rt in ResponseType.values()) {
                        if (rt.toString().equals(r, ignoreCase = true)) {
                            responseTypes.add(rt)
                            break
                        }
                    }
                }
            }

            return responseTypes
        }
    }
}
