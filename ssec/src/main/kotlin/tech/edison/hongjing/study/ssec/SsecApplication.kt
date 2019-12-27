package tech.edison.hongjing.study.ssec

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
@EnableWebSecurity
class SsecApplication

fun main(args: Array<String>) {
	runApplication<SsecApplication>(*args)
}
