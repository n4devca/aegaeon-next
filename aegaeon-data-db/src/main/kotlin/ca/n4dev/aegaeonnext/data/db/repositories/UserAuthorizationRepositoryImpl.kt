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

import ca.n4dev.aegaeonnext.common.model.UserAuthorization
import ca.n4dev.aegaeonnext.common.repository.UserAuthorizationRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.Instant

private const val GET_BY_USERID_AND_CLIENTID = """
   select id, user_id, client_id, scopes, created_at, version 
   from user_authorization 
   where user_id = :userId
     and client_id = :clientId
"""

private const val GET_BY_USERNAME_AND_CLIENTID = """
   select user_authorization.id, 
          user_authorization.user_id, 
          user_authorization.client_id, 
          user_authorization.scopes, 
          user_authorization.created_at, 
          user_authorization.version 
   from user_authorization
        join users on (user_authorization.user_id = users.id)
   where users.username = :userName
     and client_id = :clientId
"""

@Repository
class UserAuthorizationRepositoryImpl(jdbcTemplate: NamedParameterJdbcTemplate) : BaseRepository(jdbcTemplate),
    UserAuthorizationRepository {

    private val resultSetToUserAuth = RowMapper { rs, _ ->
        UserAuthorization(
            id = rs.getLong("id"),
            userId = rs.getLong("user_id"),
            clientId = rs.getLong("client_id"),
            scopes = rs.getString("scopes"),
            createdAt = toNonNullInstant(rs.getTimestamp("created_at")),
            version = rs.getInt("version")
        )
    }

    override fun getByUserIdAndClientId(userId: Long, clientId: Long) =
        single(jdbcTemplate.query(GET_BY_USERID_AND_CLIENTID,
            mapOf("userId" to userId, "clientId" to clientId),
            resultSetToUserAuth))

    override fun getByUserNameAndClientId(userName: String, clientId: Long) =
        single(jdbcTemplate.query(GET_BY_USERNAME_AND_CLIENTID,
            mapOf("userName" to userName, "clientId" to clientId),
            resultSetToUserAuth))

    override fun create(userAuthorization: UserAuthorization): Long {

        val params = mapOf(
            "user_id" to userAuthorization.userId,
            "client_id" to userAuthorization.clientId,
            "scopes" to userAuthorization.scopes,
            "created_at" to Timestamp.from(Instant.now()),
            "version" to 0)

        return super.create(params)
    }

    override fun delete(id: Long): Int {
        return super.delete(getTableName(), id)
    }

    override fun getTableName(): String = "user_authorization"
}