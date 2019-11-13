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

package ca.n4dev.aegaeonnext.config

import ca.n4dev.aegaeonnext.web.interceptor.RequestMethodArgumentResolver
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import java.util.Locale
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.web.servlet.LocaleResolver
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry




/**
 *
 * WebConfiguration.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 04 - 2019
 *
 */
@Configuration
@EnableConfigurationProperties(AegaeonServerInfo::class)
class WebConfiguration : WebMvcConfigurer {

    @Value("\${aegaeon.info.issuer}")
    private val issuer: String? = null

    @Value("\${aegaeon.info.serverName:Aegaeon Server}")
    private val serverName: String? = null

    @Value("\${aegaeon.info.logoUrl:#{null}}")
    private val logoUrl: String? = null

    @Value("\${aegaeon.info.legalEntity:#{null}}")
    private val legalEntity: String? = null

    @Value("\${aegaeon.info.privacyPolicy:#{null}}")
    private val privacyPolicy: String? = null

    @Value("\${aegaeon.info.customStyleSheet:#{null}}")
    private val customStyleSheet: String? = null

    @Bean
    fun localeResolver(): LocaleResolver {
        val resolver = CookieLocaleResolver()
        resolver.setDefaultLocale(Locale.CANADA)

        return resolver
    }

    @Bean
    fun localeChangeInterceptor(): LocaleChangeInterceptor {
        val lci = LocaleChangeInterceptor()
        lci.paramName = "lang"
        return lci
    }

    @Bean
    fun layoutDialect(): LayoutDialect {
        return LayoutDialect()
    }

    @Bean
    fun springSecurityDialect(): SpringSecurityDialect {
        return SpringSecurityDialect()
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeChangeInterceptor())
        //registry.addInterceptor(ServerInfoInterceptor(serverInfo()))
    }

    override fun addArgumentResolvers(pArgumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        pArgumentResolvers.add(RequestMethodArgumentResolver())
    }
}
