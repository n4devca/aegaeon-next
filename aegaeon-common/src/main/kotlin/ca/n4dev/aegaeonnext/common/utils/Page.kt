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

package ca.n4dev.aegaeonnext.common.utils

/**
 *
 * Page.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 21 - 2019
 *
 */
interface Page {

    fun getPageNumber(): Int = 1

    fun getPageSize(): Int = 25

    fun getSorts() : Set<Sort> = emptySet()
}

enum class Direction {
    ASC, DESC
}

interface Sort {
    fun getName() : String

    fun getDirection() : Direction
}

interface QueryResult<R> {

    fun getPage() : Page

    fun getTotalResult() : Long

    fun getResult() : List<R>
}

fun <R> resultOf(results : List<R>, page: Page, total: Long): QueryResult<R> {
    return object : QueryResult<R> {
        override fun getPage(): Page {
            return page
        }

        override fun getTotalResult(): Long {
            return total
        }

        override fun getResult(): List<R> {
            return results
        }
    }
}