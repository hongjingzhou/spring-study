package tech.edison.hongjing.study.ssec

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.password.PasswordEncoder

class MyPasswordEncoder : PasswordEncoder {
    override fun encode(p0: CharSequence?): String {
        return p0.toString()
    }

    override fun matches(p0: CharSequence?, p1: String?): Boolean {
        return p0?.toString() == p1
    }
}

@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity?) {
        http?.authorizeRequests()
                ?.antMatchers("/hello")?.permitAll()
                ?.anyRequest()?.authenticated()?.and()
                ?.formLogin()?.and()
                ?.httpBasic()
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.inMemoryAuthentication()?.passwordEncoder(MyPasswordEncoder())
                ?.withUser("a")?.password("a")?.roles("USER")?.and()
                ?.withUser("b")?.password("b")?.roles("USER", "ADMIN")
    }
}
