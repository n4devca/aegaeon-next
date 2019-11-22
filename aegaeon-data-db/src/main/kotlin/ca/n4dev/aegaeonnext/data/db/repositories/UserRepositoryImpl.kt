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

import ca.n4dev.aegaeonnext.common.model.Authority
import ca.n4dev.aegaeonnext.common.model.User
import ca.n4dev.aegaeonnext.common.model.UserInfo
import ca.n4dev.aegaeonnext.common.repository.UserRepository
import ca.n4dev.aegaeonnext.common.utils.Page
import ca.n4dev.aegaeonnext.common.utils.Result
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

/**
 *
 * UserRepository.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 01 - 2019
 *
 */
private const val GET_ALL_USERS =
    "select id, userName, passwd, unique_identifier, name, enabled from users order by userName limit :offset, :limit"

private const val GET_USER_INFO_BY_USERID =
    "select id, user_id, scope_id, claimName, claimValue from user_info where user_id = :user_id"

private const val GET_USER_BY_USERNAME =
    "select id, userName, passwd, unique_identifier, name, enabled from users where userName = :userName"

private const val GET_USER_INFO_BY_USERIDS =
    "select id, user_id, scope_id, claimName, claimValue from user_info where user_id in :user_id order by user_id"

private const val GET_AUTHORITY_BY_USERID = """
    select id, code, created_at, version 
    from authority a join user_authority ua on (a.id = ua.authority_id)
    where ua.user_id = :user_id
"""

private val resultSetToUser = RowMapper { rs, _ ->
    User(
        rs.getLong(1),
        rs.getString(2),
        rs.getString(3),
        rs.getString(4),
        rs.getString(5),
        rs.getBoolean(6)
    )
}

private val resultSetToUserInfo = RowMapper { rs, _ ->
    UserInfo(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getString(3), rs.getString(4))
}

private val resultSetToAuthority = RowMapper { rs, _ ->
    Authority(
        rs.getLong(1),
        rs.getString(2),
        toLocalDateTime(rs.getDate(3)),
        rs.getInt(4)
    )
}

@Repository
class UserRepositoryImpl : BaseRepository(), UserRepository {

    override fun getAllUsers(page: Page): Result<User> {

        val results =
            jdbcTemplate.query(GET_ALL_USERS,
                               params("offset", page.getPageNumber(), "limit", page.getPageSize()),
                               resultSetToUser)

        TODO("Create Result<List<User>>()")
    }

    override fun getUserInfoByUserId(userId: Long): List<UserInfo> =
        jdbcTemplate.query(GET_USER_INFO_BY_USERID, params("user_id", userId), resultSetToUserInfo)

    override fun getUserInfoByUserName(userName: String): User? =
        jdbcTemplate.queryForObject(GET_USER_BY_USERNAME, params("userName", userName), resultSetToUser)

    override fun getUserAuthorities(userId: Long): List<Authority> =
        jdbcTemplate.query(GET_AUTHORITY_BY_USERID, params("user_id", userId), resultSetToAuthority)

}