package ca.n4dev.aegaeonnext.web

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * IntrospectController.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 28 - 2019
 *
 */

const val IntrospectControllerURL = "/introspect"

@RestController
@RequestMapping(IntrospectControllerURL)
@ConditionalOnProperty(prefix = "aegaeon.modules", name = ["introspect"], havingValue = "true", matchIfMissing = false)
class IntrospectController {
}