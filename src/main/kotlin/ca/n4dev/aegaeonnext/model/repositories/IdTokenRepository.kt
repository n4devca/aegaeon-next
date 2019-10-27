package ca.n4dev.aegaeonnext.model.repositories

import ca.n4dev.aegaeonnext.model.entities.IdToken
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *
 * IdTokenRepository.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 22 - 2019
 *
 */
private const val GET_BY_TOKEN =
    "select id, token, user_id, client_id, scopes, valid_until, created_at, version from id_token where token = :token"

private const val GET_BY_USER_ID = """
    select id, token, user_id, client_id, scopes, valid_until, created_at, version 
    from id_token 
    where user_id = :user_id 
    order by created_at
"""

private val resultSetToIdToken = RowMapper { rs: ResultSet, _: Int ->
    IdToken(
        rs.getLong(1),
        rs.getString(2),
        rs.getLong(3),
        rs.getLong(4),
        rs.getString(5),
        LocalDateTime.ofInstant(rs.getDate(5).toInstant(), ZoneId.systemDefault()),
        LocalDateTime.ofInstant(rs.getDate(6).toInstant(), ZoneId.systemDefault()),
        rs.getInt(7))
}

@Repository
class IdTokenRepository : BaseRepository() {

    fun getByToken(token: String): IdToken? =
        jdbcTemplate.queryForObject(GET_BY_TOKEN, params("token", token), resultSetToIdToken)

    fun getByUserId(userId: Long): List<IdToken> =
        jdbcTemplate.query(GET_BY_USER_ID, params("user_id", userId), resultSetToIdToken);
}