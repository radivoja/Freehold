package freehold.order.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(HttpMethod.POST, "/orders/**").hasAnyRole("CUSTOMER", "ADMIN")
                it.requestMatchers(HttpMethod.PATCH, "/orders/**").hasAnyRole("ADMIN")
                it.requestMatchers(HttpMethod.GET, "/orders/**").authenticated()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { it.jwt { jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()) } }
            .build()

    private fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter(CognitoGroupsConverter())
        return converter
    }

    private class CognitoGroupsConverter : Converter<Jwt, Collection<GrantedAuthority>> {
        override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
            val groups = jwt.getClaimAsStringList("cognito:groups") ?: return emptyList()
            return groups.map { SimpleGrantedAuthority("ROLE_$it") }
        }
    }
}
