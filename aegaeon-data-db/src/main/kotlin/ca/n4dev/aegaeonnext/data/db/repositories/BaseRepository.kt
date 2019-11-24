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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

abstract class BaseRepository {

    @Autowired
    protected lateinit var jdbcTemplate: NamedParameterJdbcTemplate;

    protected abstract fun getTableName(): String

    protected fun defaultSort() = "id"

    fun delete(id: Long) : Int = jdbcTemplate.update("delete from ${getTableName()} where id = :id", mapOf("id" to id))

    protected fun count(query: String, params : Map<String, Any> = emptyMap()) : Long {
        return jdbcTemplate.queryForObject(query, params, Long::class.java) ?: 0
    }

    protected fun getInsertTemplate(): Lazy<SimpleJdbcInsert> = lazy {
        SimpleJdbcInsert(jdbcTemplate.jdbcTemplate)
            .withTableName(getTableName())
            .usingGeneratedKeyColumns("id")

    }
}

fun toLocalDateTime(timestamp: Timestamp?): LocalDateTime? = timestamp?.toLocalDateTime()

// Zero based
fun computeOffSet(page: Int, size: Int) = (page - 1).coerceAtLeast(0) * size

