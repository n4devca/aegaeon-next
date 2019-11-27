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

import ca.n4dev.aegaeonnext.common.model.RefreshToken
import ca.n4dev.aegaeonnext.common.repository.RefreshTokenRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

private const val GET_BY_TOKEN =
    "select id, token, user_id, client_id, scopes, valid_until, created_at, version from refresh_token where token = :token"

private const val GET_BY_USER_ID =
    "select id, token, user_id, client_id, scopes, valid_until, created_at, version from refresh_token where user_id = :user_id"

@Repository
class RefreshTokenRepositoryImpl : BaseRepository(), RefreshTokenRepository {

    private val resultSetToRefreshToken = RowMapper { rs: ResultSet, _: Int ->
        RefreshToken(
            rs.getLong("id"),
            rs.getString("token"),
            rs.getLong("user_id"),
            rs.getLong("client_id"),
            rs.getString("scopes"),
            toLocalDateTime(rs.getTimestamp("valid_until")),
            toLocalDateTime(rs.getTimestamp("created_at"))
        )
    }

    override fun getByToken(token: String): RefreshToken? =
        jdbcTemplate.queryForObject(GET_BY_TOKEN, mapOf("token" to token), resultSetToRefreshToken)

    override fun getByUserId(userId: Long): List<RefreshToken> =
        jdbcTemplate.query(GET_BY_USER_ID, mapOf("user_id" to userId), resultSetToRefreshToken);

    override fun getTableName(): String = "refresh_token"
}