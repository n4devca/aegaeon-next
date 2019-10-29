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

package ca.n4dev.aegaeonnext.model.repositories

import ca.n4dev.aegaeonnext.model.entities.Authority
import ca.n4dev.aegaeonnext.model.entities.User
import ca.n4dev.aegaeonnext.model.entities.UserInfo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneId

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

private val resultSetToUser = RowMapper {rs, _ ->
    User(
        rs.getLong(1),
        rs.getString(2),
        rs.getString(3),
        rs.getString(4),
        rs.getString(5),
        rs.getBoolean(6)
    )
}

private val resultSetToUserInfo = RowMapper {rs, _ ->
    UserInfo(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getString(3), rs.getString(4))
}

private val resultSetToAuthority = RowMapper { rs, _ ->
    Authority(
        rs.getLong(1),
        rs.getString(2),
        LocalDateTime.ofInstant(rs.getDate(3).toInstant(), ZoneId.systemDefault()),
        rs.getInt(4)
    )
}

@Repository
class UserRepository : BaseRepository() {

    fun getAllUsers(pageable: Pageable) : List<User> =
        jdbcTemplate.query(GET_ALL_USERS, params("offset", pageable.offset, "limit", pageable.pageSize), resultSetToUser)

    fun getUserInfoByUserId(userId: Long) : List<UserInfo> =
        jdbcTemplate.query(GET_USER_INFO_BY_USERID, params("user_id", userId), resultSetToUserInfo)

    fun getUserInfoByUserName(userName: String) : User? =
        jdbcTemplate.queryForObject(GET_USER_BY_USERNAME, params("userName", userName), resultSetToUser)

    fun getUserInfoByUserId(userId: Set<Long>) : List<UserInfo> =
        jdbcTemplate.query(GET_USER_INFO_BY_USERIDS, params("user_id", userId), resultSetToUserInfo)

    fun getUserAuthorities(userId: Long) : List<Authority> =
        jdbcTemplate.query(GET_AUTHORITY_BY_USERID, params("user_id", userId), resultSetToAuthority)

}