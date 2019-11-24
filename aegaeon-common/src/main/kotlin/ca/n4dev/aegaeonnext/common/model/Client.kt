/*
 *
 *  Copyright 2019 Remi Guillemette - n4dev.ca
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 *
 */

package ca.n4dev.aegaeonnext.common.model

import java.time.LocalDateTime

/**
 *
 * Client.java
 *
 * @author rguillemette
 * @since 2.0.0 - Sep 25 - 2019
 *
 */
data class Client(

        val id: Long?,

        val publicId: String,

        val secret: String,

        val name: String,

        val description: String?,

        val logoUrl: String?,

        val providerName: String?,

        val idTokenSeconds: Long = 60*10, // 10 minutes

        val accessTokenSeconds: Long = 60*60, // 1 hour

        val refreshTokenSeconds: Long = 60*60*24, // 24 hours

        val allowIntrospect: Boolean = false,

        val createdAt: LocalDateTime = LocalDateTime.now(),

        val updatedAt: LocalDateTime? = LocalDateTime.now(),

        val version: Int = 0,

        val createdBy: String
)