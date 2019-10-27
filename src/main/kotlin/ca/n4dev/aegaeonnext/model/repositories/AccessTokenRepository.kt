package ca.n4dev.aegaeonnext.model.repositories

import ca.n4dev.aegaeonnext.model.entities.AccessToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime
import java.time.ZoneId

/**
 *
 * AccessTokenRepository.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 22 - 2019
 *
 */

private const val GET_BY_TOKEN =
    "select id, token, user_id, client_id, scopes, valid_until, created_at, version from access_token where token = ?"

private const val GET_BY_USER_ID =
    "select id, token, user_id, client_id, scopes, valid_until, created_at, version from access_token where user_id = ?"

private val resultSetToAccessToken = RowMapper { rs: ResultSet, _: Int ->
    AccessToken(
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
class AccessTokenRepository : BaseRepository() {

    fun getByToken(token: String): AccessToken? =
        jdbcTemplate.queryForObject(GET_BY_TOKEN, mapOf(Pair("token", token)), resultSetToAccessToken)

    fun getByUserId(userId: Long): List<AccessToken> =
        jdbcTemplate.query(GET_BY_USER_ID, mapOf(Pair("user_id", userId)), resultSetToAccessToken);
}