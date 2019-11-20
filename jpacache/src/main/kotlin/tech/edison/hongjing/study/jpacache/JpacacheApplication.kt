package tech.edison.hongjing.study.jpacache

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching


@SpringBootApplication
@EnableCaching
class JpacacheApplication

fun main(args: Array<String>) {
	runApplication<JpacacheApplication>(*args)
}
