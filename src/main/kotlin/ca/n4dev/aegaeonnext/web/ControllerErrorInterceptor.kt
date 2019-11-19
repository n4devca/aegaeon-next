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

package ca.n4dev.aegaeonnext.web

import ca.n4dev.aegaeonnext.config.AegaeonServerInfo
import ca.n4dev.aegaeonnext.service.ClientService
import ca.n4dev.aegaeonnext.utils.LabelUtils
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView
import javax.print.attribute.standard.Severity

/**
 *
 * ControllerErrorInterceptor.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 06 - 2019
 *
 */
@ControllerAdvice
class ControllerErrorInterceptor(labelUtils: LabelUtils, serverInfo: AegaeonServerInfo, clientService: ClientService) {

    private val ERROR_PAGE = "error"

    @ExceptionHandler(Throwable::class)
    fun handleAll(throwable: Throwable): ModelAndView {

        return internalErrorPage(Severity.ERROR, "", throwable);
    }


    private fun internalErrorPage(severity: Severity, errorCode: String, throwable: Throwable?): ModelAndView {
        val mv: ModelAndView = ModelAndView(ERROR_PAGE);

        mv.addObject("severity", severity)
        mv.addObject("errorCode", errorCode)
        mv.addObject("throwable", throwable)

        return mv;
    }
}

enum class Severity {
    INFO, WARNING, DANGER
}

