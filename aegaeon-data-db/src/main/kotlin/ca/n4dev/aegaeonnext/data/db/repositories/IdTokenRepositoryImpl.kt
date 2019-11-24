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

import ca.n4dev.aegaeonnext.common.model.IdToken
import ca.n4dev.aegaeonnext.common.repository.IdTokenRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet

private const val GET_BY_TOKEN =
    "select id, token, user_id, client_id, scopes, valid_until, created_at, version from id_token where token = :token"

private const val GET_BY_USER_ID = """
    select id, token, user_id, client_id, scopes, valid_until, created_at, version 
    from id_token 
    where user_id = :user_id 
    order by created_at
"""

@Repository
class IdTokenRepositoryImpl : BaseRepository(), IdTokenRepository {

    private val resultSetToIdToken = RowMapper { rs: ResultSet, _: Int ->
        IdToken(
            rs.getLong(1),
            rs.getString(2),
            rs.getLong(3),
            rs.getLong(4),
            rs.getString(5),
            toLocalDateTime(rs.getDate(5)),
            toLocalDateTime(rs.getDate(6)),
            rs.getInt(7))
    }

    override fun getByToken(token: String): IdToken? =
        jdbcTemplate.queryForObject(GET_BY_TOKEN, params("token", token), resultSetToIdToken)

    override fun getByUserId(userId: Long): List<IdToken> =
        jdbcTemplate.query(GET_BY_USER_ID, params("user_id", userId), resultSetToIdToken)
}