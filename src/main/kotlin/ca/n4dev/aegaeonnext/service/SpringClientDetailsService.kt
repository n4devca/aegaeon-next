package ca.n4dev.aegaeonnext.service

import ca.n4dev.aegaeonnext.model.repositories.ClientRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 *
 * SpringClientDetailsService.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 28 - 2019
 *
 */
@Service("clientDetailsService")
class SpringAuthClientDetailsService(private val clientRepository: ClientRepository) : UserDetailsService {

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    override fun loadUserByUsername(pUsername: String): UserDetails {

        clientRepository.getClientByPublicId(pUsername)?.let {
            val aegaeonUserDetails = AegaeonUserDetails(it.id,
                pUsername,
                "{noop}" + it.secret,
                enable = true,
                nonLocked = true,
                authorities = listOf(SimpleGrantedAuthority("ROLE_CLIENT")))

            aegaeonUserDetails.allowIntrospection = it.allowIntrospect

            return aegaeonUserDetails
        }

        throw UsernameNotFoundException("$pUsername not found")
    }

}
