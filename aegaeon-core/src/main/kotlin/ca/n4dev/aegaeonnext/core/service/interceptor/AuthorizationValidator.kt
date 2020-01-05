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

package ca.n4dev.aegaeonnext.core.service.interceptor

import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import ca.n4dev.aegaeonnext.core.service.ClientDto
import org.springframework.stereotype.Component

/**
 *
 * AuthorizationInterceptor
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since Dec 30 - 2019
 *
 */
interface AuthorizationValidator {
    fun validate(userDetails: AegaeonUserDetails, clientDto: ClientDto): Boolean
}

@Component
class AuthorizationValidatorImpl : AuthorizationValidator {
    override fun validate(userDetails: AegaeonUserDetails, clientDto: ClientDto): Boolean = true
}
