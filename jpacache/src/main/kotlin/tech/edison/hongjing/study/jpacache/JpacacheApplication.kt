package tech.edison.hongjing.study.jpacache

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching


// Ref: https://blog.csdn.net/qq_22172133/article/details/81192040
@SpringBootApplication
@EnableCaching
class JpacacheApplication

fun main(args: Array<String>) {
	runApplication<JpacacheApplication>(*args)
}
