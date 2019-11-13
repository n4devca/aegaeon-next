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
package ca.n4dev.aegaeonnext.web.interceptor

import javax.servlet.http.HttpServletRequest

import org.springframework.core.MethodParameter
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * RequestMethodInterceptor.java
 *
 * Manage controller function having RequestMEthod as parameter.
 *
 * @author by rguillemette
 * @since May 24, 2017
 */
class RequestMethodArgumentResolver : HandlerMethodArgumentResolver {

    /* (non-Javadoc)
     * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#supportsParameter(org.springframework.core.MethodParameter)
     */
    override fun supportsParameter(pParameter: MethodParameter): Boolean {
        return RequestMethod::class.java == pParameter.parameterType
    }

    /* (non-Javadoc)
     * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)
     */
    @Throws(Exception::class)
    override fun resolveArgument(pParameter: MethodParameter, pMavContainer: ModelAndViewContainer?, pWebRequest: NativeWebRequest,
                                 pBinderFactory: WebDataBinderFactory?): Any? {

        if (pWebRequest.nativeRequest is HttpServletRequest) {
            val request = pWebRequest.nativeRequest as HttpServletRequest
            val methodString = request.method

            return RequestMethod.valueOf(methodString)
        }

        return null
    }


}
