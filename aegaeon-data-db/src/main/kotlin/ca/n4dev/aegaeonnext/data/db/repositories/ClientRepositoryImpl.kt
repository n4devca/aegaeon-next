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

import ca.n4dev.aegaeonnext.common.model.*
import ca.n4dev.aegaeonnext.common.repository.ClientRepository
import ca.n4dev.aegaeonnext.common.utils.requireNonNull
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

private const val GET_CLIENT_BY_ID = """
   select id, public_id, secret, name, description, logoUrl, providerName,
          id_token_seconds, access_token_seconds, refresh_token_seconds, allow_introspect,
          created_at, updated_at, version, created_by  
   from client 
   where id = :id 
"""

private const val GET_CLIENT_BY_PUBLICID = """
   select id, public_id, secret, name, description, logoUrl, providerName,
          id_token_seconds, access_token_seconds, refresh_token_seconds, allow_introspect,
          created_at, updated_at, version, created_by  
   from client 
   where public_id = :public_id 
"""

private const val GET_CLIENT_SCOPES_BY_CLIENTID = """
    select cs.id, cs.client_id, cs.scope_id, s.name as scope_name, cs.created_at, cs.version
    from client_scope cs join scope s on (cs.scope_id = s.id)
    where client_id = :client_id
"""

private const val GET_CLIENT_FLOW_BY_CLIENTID = """
    select id, client_id, flow, created_at, version
    from client_flow
    where client_id = :client_id
"""

private const val GET_CLIENT_REDIRECTION_BY_ID = """
    select id, client_id, url, created_at, version
    from client_redirection
    where client_id = :client_id
    order by url
"""

private const val UPDATE_CLIENT = """
    update client
        set public_id = :public_id, 
            secret = :secret, 
            name = :name, 
            description = :description, 
            logoUrl = :logoUrl, 
            provider_name = :provider_name,
            id_token_seconds = :id_token_seconds, 
            access_token_seconds = :access_token_seconds, 
            refresh_token_seconds = :refresh_token_seconds, 
            allow_introspect = :allow_introspect,
            updated_at = :updated_at, 
            version = version + 1
    where id = :id
      and version = :version
"""


@Repository
class ClientRepositoryImpl : BaseRepository(), ClientRepository {

    private val resultSetToClient = RowMapper { rs, _ ->
        Client(
            id = rs.getLong("id"),
            publicId = rs.getString("public_id"),
            secret = rs.getString("secret"),
            name = rs.getString("name"),
            description = rs.getString("description"),
            logoUrl = rs.getString("logoUrl"),
            providerName = rs.getString("providerName"),
            idTokenSeconds = rs.getLong("id_token_seconds"),
            accessTokenSeconds = rs.getLong("access_token_seconds"),
            refreshTokenSeconds = rs.getLong("refresh_token_seconds"),
            allowIntrospect = rs.getBoolean("allow_introspect"),
            createdAt = toLocalDateTime(rs.getTimestamp("created_at"))!!,
            updatedAt = toLocalDateTime(rs.getTimestamp("updated_at")),
            version = rs.getInt("version"),
            createdBy = rs.getString("created_by")
        )
    }

    private val resultSetToClientScope = RowMapper { rs, _ ->
        ClientScope(
            rs.getLong("id"),
            rs.getLong("client_id"),
            rs.getLong("scope_id"),
            rs.getString("scope_name"),
            toLocalDateTime(rs.getTimestamp("created_at")),
            rs.getInt("version")
        )
    }

    private val resultSetToClientFlow = RowMapper { rs, _ ->
        ClientFlow(
            rs.getLong("id"),
            rs.getLong("client_id"),
            Flow.valueOf(rs.getString("flow")),
            toLocalDateTime(rs.getTimestamp("created_at")),
            rs.getInt("version")
        )
    }

    private val resultSetToClientRedirection = RowMapper { rs, _ ->
        ClientRedirection(
            rs.getLong("id"),
            rs.getLong("client_id"),
            rs.getString("url"),
            toLocalDateTime(rs.getTimestamp("created_at")),
            rs.getInt("version")
        )
    }

    override fun getClientById(id: Long): Client? =
        jdbcTemplate.queryForObject(GET_CLIENT_BY_ID, mapOf(Pair("id", id)), resultSetToClient)

    override fun getClientByPublicId(publicId: String): Client? =
        jdbcTemplate.queryForObject(GET_CLIENT_BY_PUBLICID, mapOf(Pair("public_id", publicId)), resultSetToClient)

    override fun getClientScopesByClientId(clientId: Long): List<ClientScope> =
        jdbcTemplate.query(GET_CLIENT_SCOPES_BY_CLIENTID, mapOf(Pair("client_id", clientId)), resultSetToClientScope)

    override fun getClientFlowByClientId(clientId: Long): List<ClientFlow> =
        jdbcTemplate.query(GET_CLIENT_FLOW_BY_CLIENTID, mapOf(Pair("client_id", clientId)), resultSetToClientFlow)

    override fun getClientRedirectionByClientId(clientId: Long): List<ClientRedirection> =
        jdbcTemplate.query(GET_CLIENT_REDIRECTION_BY_ID, mapOf(Pair("client_id", clientId)), resultSetToClientRedirection)

    override fun create(client: Client): Long {

        val insertTemplate = getInsertTemplate().value

        val clientParams =
            mapOf(
                Pair("public_id", client.publicId),
                Pair("secret", client.secret),
                Pair("name", client.name),
                Pair("description", client.description),
                Pair("logo_url", client.logoUrl),
                Pair("provider_name", client.providerName),
                Pair("id_token_seconds", client.idTokenSeconds),
                Pair("access_token_seconds", client.accessTokenSeconds),
                Pair("refresh_token_seconds", client.refreshTokenSeconds),
                Pair("allow_introspect", client.allowIntrospect),
                Pair("created_at", LocalDateTime.now()),
                Pair("updated_at", LocalDateTime.now()),
                Pair("version", 0),
                Pair("created_by", "n/a"))

        val key = insertTemplate.executeAndReturnKey(clientParams)
        return key.toLong()
    }

    override fun update(id: Long, updatedClient: Client) {

        val client = requireNonNull(getClientById(id)) {
            Exception("Client $id cannot be found.")
        }

        val nbUpdated = jdbcTemplate.update(UPDATE_CLIENT, mapOf(
            Pair("id", id),
            Pair("public_id", updatedClient.publicId),
            Pair("secret", updatedClient.secret),
            Pair("name", updatedClient.name),
            Pair("description", updatedClient.description),
            Pair("logo_url", updatedClient.logoUrl),
            Pair("provider_name", updatedClient.providerName),
            Pair("id_token_seconds", updatedClient.idTokenSeconds),
            Pair("access_token_seconds", updatedClient.accessTokenSeconds),
            Pair("refresh_token_seconds", updatedClient.refreshTokenSeconds),
            Pair("allow_introspect", updatedClient.allowIntrospect),
            Pair("updated_at", LocalDateTime.now()),
            Pair("version", client.version)))

        if (nbUpdated == 0) {
            throw OptimisticLockingFailureException("The client [$id][v${client.version}] has been already updated.")
        }
    }

    override fun getTableName(): String = "client"
}