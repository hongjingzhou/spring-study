package tech.edison.hongjing.soauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

//https://spring.io/guides/tutorials/spring-boot-oauth2/
@SpringBootApplication
@RestController
class SoauthApplication : WebSecurityConfigurerAdapter() {
	@GetMapping("/user")
    fun user(@AuthenticationPrincipal principal: OAuth2User) : Map<String, Any> {
        return principal.attributes
	}

	override fun configure(http: HttpSecurity?) {
		http ?: return

		http.authorizeRequests()
				?.antMatchers("/", "/error", "/webjars/**")?.permitAll()
				?.anyRequest()?.authenticated()
		http.exceptionHandling {
			it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
		}
		http.oauth2Login()

        http.logout().logoutSuccessUrl("/").permitAll()

        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
	}
}


fun main(args: Array<String>) {
	runApplication<SoauthApplication>(*args)
}
