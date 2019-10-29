package ca.n4dev.aegaeonnext.service

import ca.n4dev.aegaeonnext.model.repositories.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 *
 * SpringUserDetailService.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 28 - 2019
 *
 */
@Service("userDetailsService")
class SpringAuthUserDetailsService(val userRepository: UserRepository) : UserDetailsService {

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
     */
    override fun loadUserByUsername(pUsername: String): UserDetails {

        userRepository.getUserInfoByUserName(pUsername)?.let {
            val userAuthorities = userRepository.getUserAuthorities(it.id!!)
            val authorities = userAuthorities.map { SimpleGrantedAuthority(it.code) }
            return AegaeonUserDetails(it.id, it.userName, it.passwd, it.enabled, true, authorities)
        }

        throw UsernameNotFoundException("$pUsername not found")
    }

}

class AegaeonUserDetails(val id: Long?,
                         private val username: String,
                         private val password: String,
                         private val enable: Boolean,
                         private val nonLocked: Boolean,
                         private val authorities: Collection<GrantedAuthority>) : UserDetails {

    /**
     * @return the allowIntrospection
     */
    /**
     * @param pAllowIntrospection the allowIntrospection to set
     */
    var allowIntrospection = false


    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return this.authorities
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    override fun getPassword(): String {
        return this.password
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getUserId()
     */
    override fun getUsername(): String {
        return this.username
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     */
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     */
    override fun isAccountNonLocked(): Boolean {
        return this.nonLocked
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     */
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    override fun isEnabled(): Boolean {
        return this.enable
    }

    override fun toString(): String {
        return "$id,$username,$enable,$nonLocked,$allowIntrospection"
    }
}