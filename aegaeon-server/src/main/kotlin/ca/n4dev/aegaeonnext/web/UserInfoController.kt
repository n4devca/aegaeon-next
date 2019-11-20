package ca.n4dev.aegaeonnext.web

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * UserInfoController.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 29 - 2019
 *
 */
const val UserInfoControllerURL = "/userinfo"

@RestController
@RequestMapping(UserInfoControllerURL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = ["oauth"], havingValue = "true", matchIfMissing = true)
class UserInfoController {

}