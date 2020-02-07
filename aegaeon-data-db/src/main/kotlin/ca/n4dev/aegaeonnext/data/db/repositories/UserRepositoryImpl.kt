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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

private const val USER_COLUMNS = """
id, username, name, unique_identifier, passwd, enabled, picture_url, last_login_date, created_at, updated_at, version
"""

private const val COUNT_ALL_USERS = "select count(*) from users"

private const val GET_ALL_USERS = """
select $USER_COLUMNS
from users 
order by userName 
limit :offset, :limit
"""

private const val GET_USER_INFO_BY_USERID = """
select user_info.id, user_info.user_id, user_info.claim_id, claim.code as claim_code,
       user_info.custom_name, user_info.value, user_info.created_at, user_info.version 
from user_info 
    left outer join claim on (user_info.claim_id = claim.id)
where user_id = :user_id    
"""


private const val GET_USER_BY_USERNAME = "select $USER_COLUMNS from users where userName = :userName"

private const val GET_USER_BY_ID = "select $USER_COLUMNS from users where id = :id"

private const val GET_USER_INFO_BY_USERIDS =
    "select id, user_id, scope_id, name, value from user_info where user_id in (:user_id) order by user_id"

private const val GET_AUTHORITY_BY_USERID = """
select a.id, a.code 
from authority a join user_authority ua on (a.id = ua.authority_id)
where ua.user_id = :user_id
"""

@Repository
class UserRepositoryImpl(jdbcTemplate: NamedParameterJdbcTemplate) : BaseRepository(jdbcTemplate), UserRepository {

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
            lastLoginDate = toInstant(rs.getTimestamp("last_login_date")),
            createdAt = toNonNullInstant(rs.getTimestamp("created_at")),
            updatedAt = toInstant(rs.getTimestamp("updated_at")),
            version = rs.getInt("version")
        )
    }

    private val resultSetToUserInfo = RowMapper { rs, _ ->
        UserInfo(
            id = rs.getLong("id"),
            userId = rs.getLong("user_id"),
            claimId = rs.getLong("claim_id"),
            claimCode = rs.getString("claim_code"),
            customName = rs.getString("custom_name"),
            claimValue = rs.getString("value"),
            createdAt = toNonNullInstant(rs.getTimestamp("created_at")),
            version = rs.getInt("version")
        )
    }

    private val resultSetToAuthority = RowMapper { rs, _ ->
        Authority(
            rs.getLong("id"),
            rs.getString("code")
        )
    }

    override fun getAllUsers(page: Page): QueryResult<User> {

        val results =
            jdbcTemplate.query(GET_ALL_USERS,
                               page.toParams(),
                               resultSetToUser)

        return resultOf(results, page, countAll())
    }

    override fun getUserInfoByUserId(userId: Long): List<UserInfo> =
        jdbcTemplate.query(GET_USER_INFO_BY_USERID, mapOf("user_id" to userId), resultSetToUserInfo)

    override fun getUserById(id: Long): User? =
        single(jdbcTemplate.query(GET_USER_BY_ID, mapOf("id" to id), resultSetToUser))

    override fun getUserByUserName(userName: String): User? =
        single(jdbcTemplate.query(GET_USER_BY_USERNAME, mapOf("userName" to userName), resultSetToUser))

    override fun getUserAuthorities(userId: Long): List<Authority> =
        jdbcTemplate.query(GET_AUTHORITY_BY_USERID, mapOf("user_id" to userId), resultSetToAuthority)

    override fun create(user: User): Long {

        val params =
            mapOf(
                "username" to user.userName,
                "unique_identifier" to user.uniqueIdentifier,
                "name" to user.name,
                "passwd" to user.password,
                "picture_url" to user.picture,
                "enabled" to user.enabled
            )

        return super.create(params)
    }

    override fun update(id: Long, user: User) {
        TODO("not implemented yet")
    }

    override fun delete(id: Long): Int {
        return super.delete(getTableName(), id)
    }

    override fun getTableName(): String = "users"
}