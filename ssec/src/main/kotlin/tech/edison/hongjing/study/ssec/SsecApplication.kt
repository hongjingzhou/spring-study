package tech.edison.hongjing.study.ssec

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@SpringBootApplication
@EnableWebSecurity
@EnableAsync
class SsecApplication

fun main(args: Array<String>) {
	runApplication<SsecApplication>(*args)
}
