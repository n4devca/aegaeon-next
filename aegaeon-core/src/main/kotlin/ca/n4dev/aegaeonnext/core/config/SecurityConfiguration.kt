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

package ca.n4dev.aegaeonnext.core.config

import ca.n4dev.aegaeonnext.common.model.ROLE_CLIENT
import ca.n4dev.aegaeonnext.common.model.ROLE_USER
import ca.n4dev.aegaeonnext.core.security.AccessTokenAuthenticationFilter
import ca.n4dev.aegaeonnext.core.security.AccessTokenAuthenticationProvider
import ca.n4dev.aegaeonnext.core.security.AccessTokenEntryPoint
import ca.n4dev.aegaeonnext.core.security.PromptAwareAuthenticationFilter
import ca.n4dev.aegaeonnext.core.service.AuthenticationService
import ca.n4dev.aegaeonnext.core.service.ClientService
import ca.n4dev.aegaeonnext.core.service.UserAuthorizationService
import ca.n4dev.aegaeonnext.core.web.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import javax.servlet.http.HttpServletResponse

/**
 *
 * SecurityConfiguration.java
 *
 * Spring Security configuration.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 26 - 2019
 *
 */
private const val BCRYPT_PASSWD_ENCODER_PREFIX = "bcrypt"
private const val NOOP_PASSWD_ENCODER_PREFIX = "noop"

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfiguration {

    @Bean
    @Primary
    fun passwordEncoder(): PasswordEncoder {
        val bcryptPasswordEncoder = BCryptPasswordEncoder()

        val encoders = HashMap<String, PasswordEncoder>()
        encoders[BCRYPT_PASSWD_ENCODER_PREFIX] = bcryptPasswordEncoder
        encoders[NOOP_PASSWD_ENCODER_PREFIX] = NoOpPasswordEncoder.getInstance()

        val delegatingPasswordEncoder = DelegatingPasswordEncoder(BCRYPT_PASSWD_ENCODER_PREFIX, encoders)
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(bcryptPasswordEncoder)

        return delegatingPasswordEncoder
    }

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint = AccessTokenEntryPoint()
}

@Configuration
@Order(1)
class ClientAuthWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {

    @Autowired
    private val clientDetailsService: UserDetailsService? = null

    @Autowired
    private val authenticationEntryPoint: AuthenticationEntryPoint? = null

    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
            .requestMatchers()
            .antMatchers(TokensControllerURL, IntrospectControllerURL)
            .and()
            .authorizeRequests().anyRequest().hasAnyAuthority(ROLE_CLIENT)
            .and()
            .httpBasic().authenticationEntryPoint(authenticationEntryPoint)
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .csrf().disable()
            .userDetailsService(clientDetailsService)
    }
}

@Configuration
@Order(2)
class UserInfoWebSecurityConfigurerAdapter(val serverInfo: AegaeonServerInfo,
                                           val authenticationEntryPoint: AuthenticationEntryPoint) : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var authenticationService: AuthenticationService

    /**
     * Remember me config
     */
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.authenticationProvider(accessTokenAuthenticationProvider())
    }


    fun accessTokenAuthenticationFilter(): AccessTokenAuthenticationFilter {
        return AccessTokenAuthenticationFilter(authenticationManagerBean(), authenticationEntryPoint)
    }


    fun accessTokenAuthenticationProvider(): AccessTokenAuthenticationProvider {
        return AccessTokenAuthenticationProvider(authenticationService)
    }

    override fun configure(pHttp: HttpSecurity) {
        pHttp
            .antMatcher(UserInfoControllerURL)
            .authorizeRequests()
            .anyRequest().hasAnyAuthority(ROLE_USER)
            .and()
            .exceptionHandling()
            .and()
            .csrf().disable()
            .addFilterBefore(accessTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }
}

@Configuration
// @Order(3)
class FormLoginWebSecurityConfigurerAdapter(private val userDetailsService: UserDetailsService,
                                            private val controllerErrorInterceptor: ErrorInterceptorController,
                                            private val userAuthorizationService: UserAuthorizationService,
                                            private val clientService: ClientService) : WebSecurityConfigurerAdapter() {


    fun promptAwareAuthenticationFilter(): PromptAwareAuthenticationFilter {
        return PromptAwareAuthenticationFilter(controllerErrorInterceptor, userAuthorizationService, clientService)
    }

    override fun configure(pHttp: HttpSecurity) {
        pHttp
            .authorizeRequests()
            .antMatchers("/resources/**",
                         ServerInfoControllerURL,
                         JwkControllerURL,
                         ErrorControllerURL
                /*
                SimpleHomeController.URL,
                SimpleCreateAccountController.URL,
                SimpleCreateAccountController.URL_ACCEPT
                */).permitAll()
            .anyRequest()
            .hasAnyAuthority(ROLE_USER)
            .and()
            .addFilterBefore(promptAwareAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .formLogin()
            .loginPage("/login").permitAll()
            .defaultSuccessUrl("/") //SimpleUserAccountController.URL
            .and()
            .csrf().disable()
            .userDetailsService(userDetailsService)
            .logout()
            .logoutSuccessUrl("/")
    }

}
