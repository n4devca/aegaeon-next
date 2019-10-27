package ca.n4dev.aegaeonnext.model.repositories

import ca.n4dev.aegaeonnext.model.entities.Client
import ca.n4dev.aegaeonnext.model.entities.ClientFlow
import ca.n4dev.aegaeonnext.model.entities.ClientScope
import ca.n4dev.aegaeonnext.model.entities.Flow
import ca.n4dev.aegaeonnext.utils.requireNonNull
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Repository
import java.lang.Exception
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *
 * ClientRepository.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 22 - 2019
 *
 */
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
    select cs.id, cs.client_id, cs.scope_id, s.code, cs.created_at, cs.version
    from client_scope cs join scope s on (cs.scope_id = s.id)
    where client_id = :client_id
"""

private const val GET_CLIENT_FLOW_BY_CLIENTID = """
    select id, client_id, flow, created_at, version
    from client_flow
    where client_id = :client_id
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

private val resultSetToClient = RowMapper { rs, _ ->
    Client(
        rs.getLong(1),
        rs.getString(2),
        rs.getString(3),
        rs.getString(4),
        rs.getString(5),
        rs.getString(6),
        rs.getString(7),
        rs.getLong(8),
        rs.getLong(9),
        rs.getLong(10),
        rs.getBoolean(11),
        LocalDateTime.ofInstant(rs.getDate(12).toInstant(), ZoneId.systemDefault()),
        LocalDateTime.ofInstant(rs.getDate(13).toInstant(), ZoneId.systemDefault()),
        rs.getInt(14),
        rs.getString(15)
    )
}

private val resultSetToClientScope = RowMapper { rs, _ ->
    ClientScope(
        rs.getLong(1),
        rs.getLong(2),
        rs.getLong(3),
        rs.getString(4),
        LocalDateTime.ofInstant(rs.getDate(5).toInstant(), ZoneId.systemDefault()),
        rs.getInt(6)
    )
}

private val resultSetToClientFlow = RowMapper { rs, _ ->
    ClientFlow(
        rs.getLong(1),
        rs.getLong(2),
        Flow.valueOf(rs.getString(3)),
        LocalDateTime.ofInstant(rs.getDate(4).toInstant(), ZoneId.systemDefault()),
        rs.getInt(5)
    )
}

@Repository
class ClientRepository : BaseRepository() {

    fun getClientById(id: Long): Client? =
        jdbcTemplate.queryForObject(GET_CLIENT_BY_ID, mapOf(Pair("id", id)), resultSetToClient)

    fun getClientByPublicId(publicId: String): Client? =
        jdbcTemplate.queryForObject(GET_CLIENT_BY_PUBLICID, mapOf(Pair("public_id", publicId)), resultSetToClient)

    fun getClientScopesByClientId(clientId: Long): List<ClientScope> =
        jdbcTemplate.query(GET_CLIENT_SCOPES_BY_CLIENTID, mapOf(Pair("client_id", clientId)), resultSetToClientScope)

    fun getClientFlowByClientId(clientId: Long): List<ClientFlow> =
        jdbcTemplate.query(GET_CLIENT_FLOW_BY_CLIENTID, mapOf(Pair("client_id", clientId)), resultSetToClientFlow)

    fun create(client: Client): Long {

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
                Pair("created_by", "n/a"));

        val key = insertTemplate.executeAndReturnKey(clientParams)
        return key as Long
    }

    fun update(id: Long, updatedClient: Client): Unit {

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

    private fun getInsertTemplate(): Lazy<SimpleJdbcInsert> = lazy {
        SimpleJdbcInsert(jdbcTemplate.jdbcTemplate).withTableName("client")
    }


}