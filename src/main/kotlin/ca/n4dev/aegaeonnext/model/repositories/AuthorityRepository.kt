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

import ca.n4dev.aegaeonnext.model.entities.Authority
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *
 * AuthorityRepository.java
 *
 * Repository to access Authority.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 04 - 2019
 *
 */

private const val GET_ALL = "select id, code, created_at, updated_at, version from authority"

private val resultSetToAuthority = RowMapper { rs, _ ->
    Authority(rs.getLong(1),
        rs.getString(2),
        LocalDateTime.ofInstant(rs.getDate(3).toInstant(), ZoneId.systemDefault()),
        rs.getInt(5))
}

@Repository
class AuthorityRepository : BaseRepository() {

    fun getAll() = jdbcTemplate.query(GET_ALL, resultSetToAuthority)
}