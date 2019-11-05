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
package ca.n4dev.aegaeonnext.service

import ca.n4dev.aegaeonnext.model.dto.ScopeDto
import ca.n4dev.aegaeonnext.model.entities.Scope
import ca.n4dev.aegaeonnext.model.repositories.ScopeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val scopeToDto = { scope: Scope ->
    ScopeDto(
        scope.id!!,
        scope.code
    )
}

@Service
class ScopeService(private val scopeRepository: ScopeRepository) {

    @Transactional(readOnly = true)
    fun getAll() = scopeRepository.getAll().map { scopeToDto(it) }.toSet()

}