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

import java.time.Instant

/**
 *
 * UserDto.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 29 - 2019
 *
 */
data class User(
    val id: Long?,
    val userName: String,
    val password: String,
    val uniqueIdentifier: String,
    val name: String,
    val picture: String? = null,
    val locale: String? = null,
    val enabled: Boolean = true,
    val locked: Boolean = false,
    val lastLoginDate: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val version: Int = 0
) {
    override fun toString(): String {
        return "UserDto(id=$id, userName='$userName', uniqueIdentifier='$uniqueIdentifier')"
    }
}