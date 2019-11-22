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
import org.springframework.stereotype.Repository

/**
 *
 * UserAuthorizationRepository.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 05 - 2019
 *
 */

private const val GET_BY_USERID_AND_CLIENTID = """
   select id, user_id, client_id, scopes, created_at, updated_at, version 
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
          user_authorization.updated_at, 
          user_authorization.version 
   from user_authorization
        join users on (user_authorization.user_id = users.id)
   where users.username = :userName
     and client_id = :clientId
"""

private val resultSetToUserAuth = RowMapper { rs, _ ->
    UserAuthorization(
        rs.getLong(1),
        rs.getLong(2),
        rs.getLong(3),
        rs.getString(4),
        toLocalDateTime(rs.getDate(12)),
        toLocalDateTime(rs.getDate(13)),
        rs.getInt(14)
    )
}

@Repository
class UserAuthorizationRepositoryImpl : BaseRepository(), UserAuthorizationRepository {

    override fun getByUserIdAndClientId(userId: Long, clientId: Long) =
        jdbcTemplate.queryForObject(GET_BY_USERID_AND_CLIENTID,
            mapOf("userId" to userId, "clientId" to clientId),
            resultSetToUserAuth)

    override fun getByUserNameAndClientId(userName: String, clientId: Long) =
        jdbcTemplate.queryForObject(GET_BY_USERNAME_AND_CLIENTID,
            mapOf("userName" to userName, "clientId" to clientId),
            resultSetToUserAuth)
}