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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

/**
 *
 * BaseRepository.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 22 - 2019
 *
 */
abstract class BaseRepository {

    @Autowired
    protected lateinit var jdbcTemplate: NamedParameterJdbcTemplate;

    protected fun defaultSort() = "id"

    protected fun params(vararg params : Any) : Map<String, Any> {
        require(params.size % 2 == 0) {
            "The parameters should be pairs."
        }

        val map : MutableMap<String, Any> = LinkedHashMap();
        for (i in params.indices step 2) {
            map.put(params[i] as String, params[i + 1])
        }

        return map
    }

}