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

package ca.n4dev.aegaeonnext.data.db.repositories

import ca.n4dev.aegaeonnext.common.model.Claim
import ca.n4dev.aegaeonnext.common.model.Scope
import ca.n4dev.aegaeonnext.common.repository.ScopeRepository
import ca.n4dev.aegaeonnext.common.utils.Page
import ca.n4dev.aegaeonnext.common.utils.QueryResult
import ca.n4dev.aegaeonnext.common.utils.resultOf
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

private const val SCOPE_COLUMNS = "id, code, is_system"
private const val CLAIM_COLUMNS = "id, code"

private const val SCOPE_GET_PAGE = "select $SCOPE_COLUMNS from scope order by code limit :offset, :limit"
private const val SCOPE_GET_BY_CODE = "select $SCOPE_COLUMNS from scope where code = :code"
private const val SCOPE_GET_BY_CODES = "select $SCOPE_COLUMNS from scope where code in (:codes)"
private const val CLAIM_GET_ALL = "select $CLAIM_COLUMNS from claim"
private const val SCOPE_GET_WITH_CLAIMS = """
select scope.id, scope.code, scope.is_system, claim.id as claim_id, claim.code as claim_code
from scope
    left outer join scope_claim on (scope.id = scope_claim.scope_id)
    left outer join claim on (scope_claim.claim_id = claim.id)
order by scope.id, claim.id
"""
private const val CLAIMS_BY_SCOPES = """
select distinct $CLAIM_COLUMNS, scope_claim.scope_id
from claim
    join scope_claim on (claim.id = scope_claim.claim_id)
where scope_claim.scope_id in (:scopeIds)
"""

private const val CLAIMS_DISTINCT_BY_SCOPES = """
select distinct $CLAIM_COLUMNS
from claim
    join scope_claim on (claim.id = scope_claim.claim_id)
where scope_claim.scope_id in (:scopeIds)
"""


private const val CLAIMS_DISTINCT_BY_SCOPE_ID = """
select distinct $CLAIM_COLUMNS
from claim
where scope_claim.scope_id = scopeId
order by code
limit :offset, :limit 
"""

private const val COUNT_CLAIMS_DISTINCT_BY_SCOPE_ID = """
select COUNT(distinct id)
from claim
where scope_claim.scope_id = scopeId
"""


@Repository
class ScopeRepositoryImpl(jdbcTemplate: NamedParameterJdbcTemplate) : BaseRepository(jdbcTemplate), ScopeRepository {


    private val resultSetToClaim = RowMapper { rs, _ ->
        Claim(
            rs.getLong("id"),
            rs.getString("code")
        )
    }

    private val resultSetToScope = RowMapper { rs, _ ->
        Scope(
            rs.getLong("id"),
            rs.getString("code"),
            rs.getBoolean("is_system")
        )
    }

    override fun getScopes(page: Page): QueryResult<Scope> {
        val results: List<Scope> = jdbcTemplate.query(SCOPE_GET_PAGE, page.toParams(), resultSetToScope)
        return resultOf(results, page, countAll())
    }

    override fun getScopeByCode(code: String): Scope? {
        return if (code.isNotBlank()) {
            single(jdbcTemplate.query(SCOPE_GET_BY_CODE, mapOf("code" to code), resultSetToScope))
        } else {
            null
        }
    }

    override fun getScopesByCodes(codes: Set<String>): List<Scope> {
        if (codes.isNotEmpty()) {
            return jdbcTemplate.query(SCOPE_GET_BY_CODES, mapOf("codes" to codes), resultSetToScope)
        } else {
            return emptyList()
        }
    }

    override fun getAllClaims(): List<Claim> = jdbcTemplate.query(CLAIM_GET_ALL, resultSetToClaim)

    fun getAllScopesAndClaims(): Map<Scope, List<Claim>> {

        val scopeAndClaims: List<Pair<Scope, Claim?>> = jdbcTemplate.query(SCOPE_GET_WITH_CLAIMS, RowMapper { rs, _ ->
            val scope = Scope(rs.getLong("id"),
                              rs.getString("code"),
                              rs.getBoolean("is_system"))

            val claimId = rs.getLong("claim_id")
            val claimCode = rs.getString("claim_code")

            if (claimId != null && claimCode != null) {
                Pair<Scope, Claim?>(scope, Claim(claimId, claimCode))
            } else {
                Pair<Scope, Claim?>(scope, null)
            }
        })

        return scopeAndClaims.groupingBy { scopeAndclaims -> scopeAndclaims.first }
            .aggregate { key, accumulator: MutableList<Claim>?, element, first ->
                val acc = if (first || accumulator == null) {
                    mutableListOf<Claim>()
                } else {
                    accumulator
                }

                element.second?.apply { acc.add(this) }

                acc
            }
    }

    override fun getDistinctClaimsByScopes(scopeIds: Set<Long>): List<Claim> {
        return jdbcTemplate.query(CLAIMS_DISTINCT_BY_SCOPES, mapOf("scopeIds" to scopeIds), resultSetToClaim)
    }

    override fun getClaimsByScopes(scopeIds: Set<Long>): Map<Long, List<Claim>> {
        val queryResult: MutableList<Pair<Long, Claim>> = jdbcTemplate.query(CLAIMS_BY_SCOPES, mapOf("scopeIds" to scopeIds)) { rs, rowNum ->
            val claim: Claim = requireNotNull(resultSetToClaim.mapRow(rs, rowNum))
            val scopeId = rs.getLong("scope_id")
            scopeId to claim
        }

//        val aggregate: Map<Long, MutableList<Claim>> =
//            queryResult.groupingBy { it.first }.aggregate { _, accumulator: MutableList<Claim>?, element, first ->
//            if (first) {
//                mutableListOf(element.second)
//            } else {
//                accumulator!!.add(element.second)
//                requireNotNull(accumulator)
//            }
//        }


        TODO()

    }

    override fun getClaimsByScopeId(scopeId: Long, page: Page): QueryResult<Claim> {

        val params = page.toParams()
        params["scopeId"] = scopeId

        val claims: List<Claim> = jdbcTemplate.query(CLAIMS_DISTINCT_BY_SCOPE_ID, params, resultSetToClaim)
        return resultOf(claims, page, count(COUNT_CLAIMS_DISTINCT_BY_SCOPE_ID, mapOf("scopeId" to scopeId)))
    }

    override fun getTableName(): String = "scope"
}