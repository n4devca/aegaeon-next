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

import kotlin.math.max

/**
 *
 * Page.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Nov 21 - 2019
 *
 */
fun computeOffSet(page: Int, size: Int) = (page - 1).coerceAtLeast(0) * size

interface Page {

    val page: Int

    val size: Int

    val offset: Int
        get() = page * size

    val sorts: Set<Sort>
        get() = emptySet()

    fun toParams() = mutableMapOf<String, Any>("offset" to computeOffSet(page, size), "limit" to size)

//    fun getPageNumber(): Int = 1
//
//    fun getPageSize(): Int = 25
//
//    fun getOffset() = getPageNumber() * getPageSize()
//
//    fun getSorts() : Set<Sort> = emptySet()
}

enum class Direction {
    ASC, DESC
}

interface Sort {
    val name: String
    val direction: Direction
}

interface QueryResult<R> {

    val result: List<R>

    val size: Int
        get() = result.size

    val totalResult: Long

    val page: Page

    fun <D> mapTo(transform: (R) -> D): QueryResult<D> {
        return QueryResultImpl<D>(result.map(transform), totalResult, page)
    }
}

private class QueryResultImpl<R>(override val result: List<R>, override val totalResult: Long, override val page: Page) : QueryResult<R>
private class PageImpl(override val page: Int, override val size: Int) : Page

fun pageOf(pageNumber: Int = 0, pageSize: Int = 25): Page = PageImpl(max(pageNumber, 0), max(pageSize, 0))
fun <R> resultOf(results : List<R>, page: Page, total: Long): QueryResult<R> = QueryResultImpl(results, total, page)