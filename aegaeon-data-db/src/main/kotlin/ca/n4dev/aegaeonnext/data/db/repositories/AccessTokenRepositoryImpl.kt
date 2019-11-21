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
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

/**
 *
 * AccessTokenRepository.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 22 - 2019
 *
 */

private const val GET_BY_TOKEN =
    "select id, token, user_id, client_id, scopes, valid_until, created_at, version from access_token where token = ?"

private const val GET_BY_USER_ID =
    "select id, token, user_id, client_id, scopes, valid_until, created_at, version from access_token where user_id = ?"

private val resultSetToAccessToken = RowMapper { rs: ResultSet, _: Int ->
    AccessToken(
        rs.getLong(1),
        rs.getString(2),
        rs.getLong(3),
        rs.getLong(4),
        rs.getString(5),
        toLocalDateTime(rs.getDate(5)),
        toLocalDateTime(rs.getDate(6)),
        rs.getInt(7))
}

@Repository
class AccessTokenRepositoryImpl : BaseRepository(), AccessTokenRepository {

    override fun getByToken(token: String): AccessToken? =
        jdbcTemplate.queryForObject(GET_BY_TOKEN, mapOf(Pair("token", token)), resultSetToAccessToken)

    override fun getByUserId(userId: Long): List<AccessToken> =
        jdbcTemplate.query(GET_BY_USER_ID, mapOf(Pair("user_id", userId)), resultSetToAccessToken)
}