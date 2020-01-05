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

import ca.n4dev.aegaeonnext.common.model.Claim
import ca.n4dev.aegaeonnext.common.model.Flow
import ca.n4dev.aegaeonnext.common.model.Scope
import ca.n4dev.aegaeonnext.common.repository.ScopeRepository
import ca.n4dev.aegaeonnext.common.utils.splitStringOn
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

const val OPENID_SCOPE = "openid"
const val OFFLINE_SCOPE = "offline_access"


@Service
class ScopeService(private val scopeRepository: ScopeRepository) {

    private val scopeToScopeDto = { scope: Scope ->
        ScopeDto(
            scope.id,
            scope.code
        )
    }

    private val claimToClaimDto = { claim: Claim ->
        ClaimDto(
            claim.id,
            claim.code
        )
    }

    @Transactional(readOnly = true)
    fun getAllScopes() = scopeRepository.getAllScopes().map { s -> scopeToScopeDto(s) }.toSet()

    @Transactional(readOnly = true)
    fun getScopeByCode(code: String) = scopeRepository.getScopeByCode(code)?.let(scopeToScopeDto)

    @Transactional(readOnly = true)
    fun getByNames(codes: Set<String>): List<ScopeDto> = scopeRepository.getScopesByCodes(codes).map { s -> scopeToScopeDto(s) }

    @Transactional(readOnly = true)
    fun getAllScopesAndClaims(): List<ScopeAndClaimDto> {
        val allScopesAndClaims: Map<Scope, List<Claim>> = scopeRepository.getAllScopesAndClaims()

        return allScopesAndClaims.map { entry ->
            val claimDtos: List<ClaimDto> = entry.value.map { claim -> claimToClaimDto(claim) }
            ScopeAndClaimDto(entry.key.id, entry.key.code, claimDtos)
        }
    }

    @Transactional(readOnly = true)
    fun getDistinctClaimsByScopes(scopes: Set<ScopeDto>): List<ClaimDto> {
        val ids: Set<Long> = scopes.map { it.id }.toSet()
        return scopeRepository.getDistinctClaimsByScopes(ids).map { claim -> claimToClaimDto(claim) }
    }

    @Transactional(readOnly = true)
    fun isPartOf(pAuthorizedScopes: String, pRequestedScopes: String): Boolean {
        val requestedScopeSet = validate(pRequestedScopes)
        return isPartOf(pAuthorizedScopes, requestedScopeSet.validScopes)
    }

    @Transactional(readOnly = true)
    fun isPartOf(pAuthorizedScopes: String, requestedScopes: Set<ScopeDto>): Boolean {
        val authorizedScopeSet = validate(pAuthorizedScopes)

        // Check if the requested scope is a subset of the authorized
        return requestedScopes.all { scopeDto -> authorizedScopeSet.validScopes.contains(scopeDto) }
    }

    @Transactional(readOnly = true)
    fun validate(scopeParam: String, exclusions: Set<String> = emptySet()): ScopeSet {

        return if (scopeParam.isNotBlank()) {

            val scopeList = splitStringOn(scopeParam)
            val validScopes = mutableSetOf<ScopeDto>()
            val invalidScopes = mutableSetOf<String>()
            for (s in scopeList) {

                if (s.isNotBlank()) {

                    val name = s.trim()
                    val scopeByCode = scopeRepository.getScopeByCode(s.trim())

                    if (scopeByCode != null && !exclusions.contains(name)) {
                        validScopes.add(ScopeDto(scopeByCode.id, scopeByCode.code))
                    } else {
                        invalidScopes.add(s)
                    }
                }
            }

            ScopeSet(validScopes, invalidScopes)
        } else {
            emptyScopeSet()
        }
    }

    @Transactional(readOnly = true)
    fun validate(scopeParam: String, flow: Flow): ScopeSet {
        val exclusions = if (flow == Flow.IMPLICIT) {
            setOf(OFFLINE_SCOPE)
        } else {
            emptySet()
        }

        return validate(scopeParam, exclusions)
    }

    fun getValidScopes(scopeParam: String?, exclusions: Set<String> = emptySet()): Set<ScopeDto> {
        if (!scopeParam.isNullOrBlank()) {
            val scopeSet = validate(scopeParam, exclusions)
            return scopeSet.validScopes
        }
        return emptySet()
    }


}

data class ScopeDto(val id: Long, val code: String)
data class ClaimDto(val id: Long, val code: String)
data class ScopeAndClaimDto(val id: Long, val name: String, val claims: List<ClaimDto>)

data class ScopeSet(
    val validScopes: Set<ScopeDto>,
    val invalidScopes: Set<String>
)

private fun emptyScopeSet() = ScopeSet(emptySet(), emptySet());

