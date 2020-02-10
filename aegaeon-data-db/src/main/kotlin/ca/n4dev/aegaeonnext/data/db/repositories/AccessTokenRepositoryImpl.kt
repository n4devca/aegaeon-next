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

package ca.n4dev.aegaeonnext.data.db.repositories

import ca.n4dev.aegaeonnext.common.model.AccessToken
import ca.n4dev.aegaeonnext.common.repository.AccessTokenRepository
import ca.n4dev.aegaeonnext.common.utils.Page
import ca.n4dev.aegaeonnext.common.utils.QueryResult
import ca.n4dev.aegaeonnext.common.utils.resultOf
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Timestamp

private const val GET_BY_TOKEN =
    "select id, token, user_id, client_id, scopes, valid_until, created_at from access_token where token = :token"

private const val GET_BY_USER_ID =
    "select id, token, user_id, client_id, scopes, valid_until, created_at from access_token where user_id = :user_id"

private const val COUNT_BY_USER_ID =
    "select count(*) from access_token where user_id = :user_id"

@Repository
class AccessTokenRepositoryImpl(jdbcTemplate: NamedParameterJdbcTemplate) : BaseRepository(jdbcTemplate), AccessTokenRepository {

    private val resultSetToAccessToken = RowMapper { rs: ResultSet, _: Int ->
        AccessToken(
            id = rs.getLong("id"),
            token = rs.getString("token"),
            userId = rs.getLong("user_id"),
            clientId = rs.getLong("client_id"),
            scopes = rs.getString("scopes"),
            validUntil = toInstant(rs.getTimestamp("valid_until")),
            createdAt = toNonNullInstant(rs.getTimestamp("created_at"))
        )
    }

    override fun getByToken(token: String): AccessToken? =
        single(jdbcTemplate.query(GET_BY_TOKEN, mapOf("token" to token), resultSetToAccessToken))

    override fun getByUserId(userId: Long, page: Page): QueryResult<AccessToken> {
        val results = jdbcTemplate.query(GET_BY_USER_ID, mapOf("user_id" to userId), resultSetToAccessToken)
        return resultOf(results, page, count(COUNT_BY_USER_ID, mapOf("user_id" to userId)))
    }

    override fun create(accessToken: AccessToken): Long {

        val params =
            mapOf("token" to accessToken.token,
                  "user_id" to accessToken.userId,
                  "client_id" to accessToken.clientId,
                  "scopes" to accessToken.scopes,
                  "valid_until" to Timestamp.from(accessToken.validUntil))

        val insertTemplate = getInsertTemplate(params.keys)

        val key = insertTemplate.executeAndReturnKey(params)
        return key.toLong()
    }

    override fun delete(id: Long): Int {
        return super.delete(getTableName(), id)
    }

    override fun getTableName(): String = "access_token"
}