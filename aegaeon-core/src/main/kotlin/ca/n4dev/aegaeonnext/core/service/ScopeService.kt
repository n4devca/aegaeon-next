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
package ca.n4dev.aegaeonnext.core.service

import ca.n4dev.aegaeonnext.data.db.repositories.ScopeRepository
import ca.n4dev.aegaeonnext.common.utils.splitStringOn
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val scopeToDto = { scope: Scope ->
    ca.n4dev.aegaeonnext.common.model.Scope(
        scope.id!!,
        scope.code
    )
}

@Service
class ScopeService(private val scopeRepository: ScopeRepository) {

    @Transactional(readOnly = true)
    fun getAll() = scopeRepository.getAll().map { scopeToDto }.toSet()

    fun getByName(name: String) = scopeRepository.getByName(name)?.let(scopeToDto)

    @Transactional(readOnly = true)
    fun isPartOf(pAuthorizedScopes: String, pRequestedScopes: String): Boolean {

        val authorizedScopeSet = validate(pAuthorizedScopes)
        val requestedScopeSet = validate(pRequestedScopes)

        // Simple equals (same)
        if (authorizedScopeSet.validScopes.size == requestedScopeSet.validScopes.size
            && authorizedScopeSet.validScopes == requestedScopeSet.validScopes) {
            return true
        }

        // Check if the requested scope is a subset of the authorized
        var allOk = true
        for (scopeView in requestedScopeSet.validScopes) {
            if (!authorizedScopeSet.validScopes.contains(scopeView)) {
                allOk = false
                break
            }
        }

        return allOk
    }

    @Transactional(readOnly = true)
    fun validate(scopeParam: String, exclusions: Set<String> = emptySet()): ScopeSet {

        if (!scopeParam.isBlank()) {

            val scopeList = splitStringOn(scopeParam)
            val scopeViews = mutableSetOf<ca.n4dev.aegaeonnext.common.model.Scope>()
            val invalidScopeViews = mutableSetOf<String>()
            for (s in scopeList) {

                if (!s.isBlank()) {

                    val scopeValue = s.trim()
                    val scopeByName = scopeRepository.getByName(scopeValue)

                    if (scopeByName != null && !exclusions.contains(scopeValue)) {
                        scopeViews.add(ca.n4dev.aegaeonnext.common.model.Scope(scopeByName.id!!, scopeByName.code))
                    } else {
                        invalidScopeViews.add(s)
                    }
                }
            }

            return ScopeSet(scopeViews, invalidScopeViews)
        }

        return emptyScopeSet()
    }

}

data class ScopeSet(

    val validScopes: Set<ca.n4dev.aegaeonnext.common.model.Scope>,

    val invalidScopes: Set<String>
)

private fun emptyScopeSet() = ScopeSet(emptySet(), emptySet());

