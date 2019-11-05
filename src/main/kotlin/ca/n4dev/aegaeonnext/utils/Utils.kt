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

package ca.n4dev.aegaeonnext.utils

/**
 *
 * Utils.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 23 - 2019
 *
 */

const val DASH: String = "-"

fun <T> requireNonNull(value: T?, pExceptionCreator : () -> Exception) : T {
    if (value != null) {
        return value;
    }
    throw pExceptionCreator.invoke();
}


fun asString(pObject: Any?): String {

    return if (pObject != null) {

        if (pObject is String) {
            pObject
        } else {
            pObject.toString()
        }
    } else DASH
}


fun join(vararg pStrings: String): String {
    return pStrings?.joinToString(" ") ?: ""
}