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

package ca.n4dev.aegaeonnext.model.repositories

import ca.n4dev.aegaeonnext.model.entities.AuthorizationCode
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *
 * AuthorizationCodeRepository.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 04 - 2019
 *
 */


private const val GET_BY_CODE = """
    select id, code, client_id, user_id, valid_until, scopes, redirect_url, nonce, response_type, created_at, updated_at, version
    from authorization_code
    where code = :code
"""

private const val GET_BY_CLIENT_ID = """
    select id, code, client_id, user_id, valid_until, scopes, redirect_url, nonce, response_type, created_at, updated_at, version
    from authorization_code
    where client_id = :clientId
    order by :sort
    limit :offset , :size
"""

private const val GET_BY_USER_ID = """
    select id, code, client_id, user_id, valid_until, scopes, redirect_url, nonce, response_type, created_at, updated_at, version
    from authorization_code
    where user_id = :userId
    order by :sort
    limit :offset , :size
"""

private val resultSetToAuthCode = RowMapper { rs, _ ->
    AuthorizationCode(
        rs.getLong(1),
        rs.getString(2),
        rs.getLong(3),
        rs.getLong(4),
        LocalDateTime.ofInstant(rs.getDate(5).toInstant(), ZoneId.systemDefault()),
        rs.getString(6),
        rs.getString(7),
        rs.getString(8),
        rs.getString(9),
        LocalDateTime.ofInstant(rs.getDate(10).toInstant(), ZoneId.systemDefault()),
        rs.getInt(12)
    )
}

@Repository
class AuthorizationCodeRepository : BaseRepository() {

    fun getByCode(code: String) = jdbcTemplate.queryForObject(GET_BY_CODE, mapOf("code" to code), resultSetToAuthCode)

    fun getByClientId(clientId: Long, page: Pageable) =
        jdbcTemplate.query(GET_BY_CLIENT_ID, mapOf("clientId" to clientId), resultSetToAuthCode)

    fun getByUserId(userId: Long, page: Pageable) =
        jdbcTemplate.query(GET_BY_USER_ID, mapOf("userId" to userId), resultSetToAuthCode)

}