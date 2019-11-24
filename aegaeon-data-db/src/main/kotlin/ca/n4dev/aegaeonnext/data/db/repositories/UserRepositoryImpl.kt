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
import ca.n4dev.aegaeonnext.common.utils.QueryResult
import ca.n4dev.aegaeonnext.common.utils.resultOf
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.util.*


private val COUNT_ALL_USERS = "select count(*) from users"

private val GET_ALL_USERS = """
    select id, userName, passwd, unique_identifier, name, picture_url, enabled, last_login_date, version
    from users 
    order by userName 
    limit :offset, :limit
"""

private val GET_USER_INFO_BY_USERID =
    "select id, user_id, scope_id, name, value, version from user_info where user_id = :user_id"

private val GET_USER_BY_USERNAME =
    "select id, userName, passwd, unique_identifier, name, picture_url, enabled from users where userName = :userName"

private val GET_USER_INFO_BY_USERIDS =
    "select id, user_id, scope_id, name, value from user_info where user_id in :user_id order by user_id"

private val GET_AUTHORITY_BY_USERID = """
    select id, code, created_at, version 
    from authority a join user_authority ua on (a.id = ua.authority_id)
    where ua.user_id = :user_id
"""

@Repository
class UserRepositoryImpl : BaseRepository(), UserRepository {


    private val resultSetToUser = RowMapper { rs, _ ->
        User(
            id = rs.getLong("id"),
            userName = rs.getString("userName"),
            password = rs.getString("passwd"),
            uniqueIdentifier = rs.getString("unique_identifier"),
            name = rs.getString("name"),
            picture = rs.getString("picture_url"),
            locale = Locale.ENGLISH.toString(),
            enabled = rs.getBoolean("enabled"),
            locked = false,
            lastLoginDate = toLocalDateTime(rs.getTimestamp("last_login_date")),
            version = rs.getInt("version")
        )
    }

    private val resultSetToUserInfo = RowMapper { rs, _ ->
        UserInfo(
            id = rs.getLong("id"),
            userId = rs.getLong("user_id"),
            scopeId = rs.getLong("scope_id"),
            claimName = rs.getString("name"),
            claimValue = rs.getString("value"),
            version = rs.getInt("version")
        )
    }

    private val resultSetToAuthority = RowMapper { rs, _ ->
        Authority(
            rs.getLong(1),
            rs.getString(2),
            toLocalDateTime(rs.getTimestamp(3)),
            rs.getInt(4)
        )
    }

    override fun getAllUsers(page: Page): QueryResult<User> {

        val results =
            jdbcTemplate.query(GET_ALL_USERS,
                               mapOf("offset" to computeOffSet(page.getPageNumber(), page.getPageSize()),
                                     "limit" to page.getPageSize()),
                               resultSetToUser)

        return resultOf(results, page, count(COUNT_ALL_USERS))
    }

    override fun getUserInfoByUserId(userId: Long): List<UserInfo> =
        jdbcTemplate.query(GET_USER_INFO_BY_USERID, params("user_id", userId), resultSetToUserInfo)

    override fun getUserInfoByUserName(userName: String): User? =
        jdbcTemplate.queryForObject(GET_USER_BY_USERNAME, params("userName", userName), resultSetToUser)

    override fun getUserAuthorities(userId: Long): List<Authority> =
        jdbcTemplate.query(GET_AUTHORITY_BY_USERID, params("user_id", userId), resultSetToAuthority)

}