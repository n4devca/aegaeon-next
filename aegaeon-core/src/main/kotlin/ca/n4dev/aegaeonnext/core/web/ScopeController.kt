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

import ca.n4dev.aegaeonnext.common.utils.QueryResult
import ca.n4dev.aegaeonnext.common.utils.pageOf
import ca.n4dev.aegaeonnext.core.service.ClaimDto
import ca.n4dev.aegaeonnext.core.service.ScopeDto
import ca.n4dev.aegaeonnext.core.service.ScopeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 *
 * ScopeController.java
 *
 * A controller to expose and manage server's scopes and claims.
 *
 * @author rguillemette
 * @since 2.0.0 - Jan 10 - 2020
 *
 */

const val ScopeControllerURL = "/scopes"

@RestController
@RequestMapping(ScopeControllerURL)
class ScopeController(val scopeService: ScopeService) {

    @GetMapping("")
    fun getScopes(@RequestParam("page", required = false, defaultValue = "0") page: Int,
                  @RequestParam("size", required = false, defaultValue = "25") size: Int): ResponseEntity<QueryResult<ScopeDto>> {

        val scopes: QueryResult<ScopeDto> = scopeService.getScopes(pageOf(page, size))
        return ResponseEntity.ok(scopes)
    }

    @GetMapping("/{scopeId}/claims")
    fun getScopeClaims(@PathVariable scopeId: Long): ResponseEntity<List<ClaimDto>> {
        val claims: List<ClaimDto> = scopeService.getDistinctClaimsByScopes(setOf(scopeId))
        return ResponseEntity.ok(claims)
    }
}