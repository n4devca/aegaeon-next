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

import ca.n4dev.aegaeonnext.common.model.AuthorizationCode
import ca.n4dev.aegaeonnext.common.repository.AuthorizationCodeRepository
import ca.n4dev.aegaeonnext.common.utils.Page
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp

private const val AUTH_CODE_COLUMNS = """
id, code, client_id, user_id, valid_until, scopes, redirect_url, response_type, nonce, version, created_at
"""

private const val GET_BY_ID = """
    select $AUTH_CODE_COLUMNS
    from authorization_code
    where id = :id
"""

private const val GET_BY_CODE = """
    select $AUTH_CODE_COLUMNS
    from authorization_code
    where code = :code
"""

private const val GET_BY_CLIENT_ID = """
    select $AUTH_CODE_COLUMNS
    from authorization_code
    where client_id = :clientId
    order by :sort
    limit :offset , :limit
"""

private const val GET_BY_USER_ID = """
    select $AUTH_CODE_COLUMNS
    from authorization_code
    where user_id = :userId
    order by :sort
    limit :offset , :limit
"""

@Repository
class AuthorizationCodeRepositoryImpl(jdbcTemplate: NamedParameterJdbcTemplate) : BaseRepository(jdbcTemplate),
    AuthorizationCodeRepository {

    private val resultSetToAuthCode = RowMapper { rs, _ ->
        AuthorizationCode(
            id = rs.getLong("id"),
            code = rs.getString("code"),
            clientId = rs.getLong("client_id"),
            userId = rs.getLong("user_id"),
            validUntil = toNonNullInstant(rs.getTimestamp("valid_until")),
            scopes = rs.getString("scopes"),
            redirectUrl = rs.getString("redirect_url"),
            responseType = rs.getString("response_type"),
            nonce = rs.getString("nonce"),
            createdAt = toNonNullInstant(rs.getTimestamp("created_at"))
        )
    }

    override fun getById(id: Long): AuthorizationCode? =
        single(jdbcTemplate.query(GET_BY_ID, mapOf("id" to id), resultSetToAuthCode))

    override fun getByCode(code: String) =
        single(jdbcTemplate.query(GET_BY_CODE, mapOf("code" to code), resultSetToAuthCode))

    override fun getByClientId(clientId: Long, page: Page): List<AuthorizationCode> =
        jdbcTemplate.query(GET_BY_CLIENT_ID, mapOf("clientId" to clientId), resultSetToAuthCode)

    override fun getByUserId(userId: Long, page: Page): List<AuthorizationCode> =
        jdbcTemplate.query(GET_BY_USER_ID, mapOf("userId" to userId), resultSetToAuthCode)

    override fun create(authorizationCode: AuthorizationCode): Long {
        val params = mapOf("code" to authorizationCode.code,
                           "client_id" to authorizationCode.clientId,
                           "user_id" to authorizationCode.userId,
                           "valid_until" to Timestamp.from(authorizationCode.validUntil),
                           "scopes" to authorizationCode.scopes,
                           "redirect_url" to authorizationCode.redirectUrl,
                           "response_type" to authorizationCode.responseType,
                           "nonce" to authorizationCode.nonce,
                           "version" to 0
        )

        val insertTemplate = getInsertTemplate(params.keys)

        val key = insertTemplate.executeAndReturnKey(params)
        return key.toLong()
    }

    override fun delete(id: Long): Int {
        return super.delete(getTableName(), id)
    }

    override fun getTableName(): String = "authorization_code"
}