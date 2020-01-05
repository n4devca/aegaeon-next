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

package ca.n4dev.aegaeonnext.core.web

import ca.n4dev.aegaeonnext.core.loggerFor
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView


/**
 *
 * LoginController.java
 *
 * Controller driving login page.
 *
 * Can be enable/disable using aegaeon.modules.login flag.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 13 - 2019
 *
 */

const val LoginControllerURL = "/login"

@Controller
@RequestMapping(LoginControllerURL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = ["login"], havingValue = "true", matchIfMissing = true)
class LoginController {

    val LOGGER = loggerFor(javaClass)

    @RequestMapping("")
    fun login(@RequestParam("error", required = true, defaultValue = "false") error: Boolean): ModelAndView {

        LOGGER.info("/login error=$error")

        return ModelAndView("signin")
    }
}