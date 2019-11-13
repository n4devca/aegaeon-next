package ca.n4dev.aegaeonnext.config

import ca.n4dev.aegaeonnext.web.ServerInfoController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.DelegatingPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.HashMap

/**
 *
 * SecurityConfiguration.java
 * TODO(rguillemette) Add description.
 *
 * @author rguillemette
 * @since 2.0.0 - Oct 26 - 2019
 *
 */

private const val ROLE_USER = "ROLE_USER"

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfiguration {

    private val BCRYPT_PASSWD_ENCODER_PREFIX = "bcrypt"
    private val NOOP_PASSWD_ENCODER_PREFIX = "noop"
    private val ROLE_CLIENT = "ROLE_CLIENT"
    private val ROLE_USER = "ROLE_USER"

    @Bean
    @Primary
    fun passwordEncoder(): PasswordEncoder {
        val bcryptPasswordEncoder = BCryptPasswordEncoder()

        val encoders = HashMap<String, PasswordEncoder>()
        encoders[BCRYPT_PASSWD_ENCODER_PREFIX] = bcryptPasswordEncoder
        encoders[NOOP_PASSWD_ENCODER_PREFIX] = NoOpPasswordEncoder.getInstance()

        val delegatingPasswordEncoder = DelegatingPasswordEncoder(BCRYPT_PASSWD_ENCODER_PREFIX, encoders)
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(bcryptPasswordEncoder)

        return delegatingPasswordEncoder
    }

}

@Configuration
class FormLoginWebSecurityConfigurerAdapter : WebSecurityConfigurerAdapter() {

    @Autowired
    private val userDetailsService: UserDetailsService? = null


    override fun configure(pHttp: HttpSecurity) {
        pHttp
            .authorizeRequests()
            .antMatchers("/resources/**").permitAll()
            .anyRequest()
            .hasAnyAuthority(ROLE_USER)
            .and()
            .formLogin()
            .loginPage("/login").permitAll()
            .defaultSuccessUrl("/")
            .and()
            .csrf().disable()
            .userDetailsService(userDetailsService)
            .logout()
            .logoutSuccessUrl("/")
    }

}