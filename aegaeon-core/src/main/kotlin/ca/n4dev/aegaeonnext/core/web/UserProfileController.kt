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

import ca.n4dev.aegaeonnext.core.security.AegaeonUserDetails
import ca.n4dev.aegaeonnext.core.service.ScopeService
import ca.n4dev.aegaeonnext.core.service.UserService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView

/**
 * The user profile base URL
 */
const val UserProfileControllerURL = "/user-profile"

/**
 *
 * UserProfileController
 *
 * A controller to manage user profile.
 *
 * @author rguillemette
 * @since Feb 11 - 2020
 *
 */
@Controller
@RequestMapping(UserProfileControllerURL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = ["account"], havingValue = "true", matchIfMissing = true)
class UserProfileController(private val userService: UserService,
                            private val scopeService: ScopeService) {

    private val userProfilePage = "user-profile"

    @GetMapping
    fun getProfilePage(authentication: Authentication): ModelAndView {

        val userDetails = authentication.principal as AegaeonUserDetails

        val view = ModelAndView(userProfilePage)
        view.addObject("user", userService.getUserById(userDetails.id))
        return view
    }
}