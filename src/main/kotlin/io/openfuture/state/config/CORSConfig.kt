package io.openfuture.state.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder

@Configuration
class CORSConfig: WebSecurityConfigurerAdapter() {

    @Throws(Exception::class)
    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.authorizeRequests().anyRequest().authenticated()
    }

    @Bean
    fun corsFilter(@Value("\${openfuture.api.url}") apiUrl: String): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOrigins = listOf(apiUrl)
        config.allowedHeaders = listOf("Origin", "Content-Type", "Accept")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }

}
