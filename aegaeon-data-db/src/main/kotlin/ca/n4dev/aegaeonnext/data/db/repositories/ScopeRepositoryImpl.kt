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

import ca.n4dev.aegaeonnext.common.model.Scope
import ca.n4dev.aegaeonnext.common.repository.ScopeRepository
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository

private const val GET_ALL = """
    select id, name, is_system
    from scope 
"""

private const val GET_BY_NAME = """
    select id, name, is_system
    from scope 
    where name = :name
"""

@Repository
class ScopeRepositoryImpl : BaseRepository(), ScopeRepository {

    private val resultSetToScope = RowMapper { rs, _ ->
        Scope(
            rs.getLong(1),
            rs.getString(2),
            rs.getBoolean(3)
        )
    }

    override fun getAll(): List<Scope> = jdbcTemplate.query(GET_ALL, resultSetToScope)

    override fun getByName(name: String) = jdbcTemplate.queryForObject(GET_BY_NAME, params("name", name), resultSetToScope)
}