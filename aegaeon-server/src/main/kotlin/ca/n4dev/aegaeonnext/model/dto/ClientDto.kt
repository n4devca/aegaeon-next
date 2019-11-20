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

package ca.n4dev.aegaeonnext.model.dto

import ca.n4dev.aegaeonnext.model.entities.Flow

/**
 *
 * ClientDto.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 22 - 2019
 *
 */

data class ClientDto(
    val id: Long?,
    val publicId: String,
    val secret: String,
    val name: String,
    val logoUrl: String?,
    val scopes: Map<String, Long?> = emptyMap(),
    val flows: Map<Flow, Long?> = emptyMap(),
    val redirections: Map<String, Long?> = emptyMap()
) {
    override fun toString(): String {
        return "ClientDto(id=$id, publicId='$publicId', name='$name')"
    }
}

//    var publicId: String
//    var secret: String
//    var name: String
//    var description: String?
//    var logoUrl: String?
//    var providerName: String?
//    var idTokenSeconds: Long
//    var accessTokenSeconds: Long
//    var refreshTokenSeconds: Long
//    var allowIntrospect: Boolean = false
//    var createdAt: LocalDateTime = LocalDateTime.now()
//    var updatedAt: LocalDateTime = LocalDateTime.now()
//    var version: Int = 0

