package ca.n4dev.aegaeonnext.service.dto

import ca.n4dev.aegaeonnext.model.entities.Client
import ca.n4dev.aegaeonnext.model.entities.Flow
import java.time.LocalDateTime

/**
 *
 * ClientDto.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 22 - 2019
 *
 */

data class ClientDto (
    val id: Long?,
    val publicId: String,
    val secret: String,
    val name: String,
    val logoUrl: String?,
    val scopes: Map<String, Long?> = emptyMap(),
    val flows: Map<Flow, Long?> = emptyMap()
)

//    var publicId: String
//    var secret: String
//    var name: String
//    var description: String?
//    var logoUrl: String?
//    var providerName: String?
//    var idTokenSeconds: Long
//    var accessTokenSeconds: Long
//    var refreshTokenSeconds: Long
//    var allowIntrospect: Boolean = false
//    var createdAt: LocalDateTime = LocalDateTime.now()
//    var updatedAt: LocalDateTime = LocalDateTime.now()
//    var version: Int = 0

