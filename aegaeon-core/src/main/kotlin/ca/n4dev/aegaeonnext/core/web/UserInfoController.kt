package ca.n4dev.aegaeonnext.core.web

import ca.n4dev.aegaeonnext.core.security.AccessTokenAuthentication
import ca.n4dev.aegaeonnext.core.service.UserService
import ca.n4dev.aegaeonnext.core.web.view.UserInfoResponse
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * UserInfoController.java
 *
 * A controller managing the oauth userinfo endpoint.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 29 - 2019
 *
 */
const val UserInfoControllerURL = "/userinfo"

@RestController
@RequestMapping(UserInfoControllerURL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = ["oauth"], havingValue = "true", matchIfMissing = true)
class UserInfoController(private val userService: UserService) {

    @GetMapping("")
    fun userInfo(authentication: Authentication): ResponseEntity<UserInfoResponse> {

        if (authentication is AccessTokenAuthentication) {
            val userId = requireNotNull(authentication.userId)
            val userDto = requireNotNull(userService.getUserById(userId))
            val payload = userService.createPayload(userDto, authentication.scopes ?: emptySet())
            return ResponseEntity.ok(UserInfoResponse(userDto.uniqueIdentifier, payload))
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
    }
}