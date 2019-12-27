package tech.edison.hongjing.study.ssec

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class WebController {

    @RequestMapping("/public", method = [RequestMethod.GET])
    fun hello() : String {
        return "public " + getLoginUser()
    }

    @RequestMapping("/private", method = [RequestMethod.GET])
    fun secret() : String {
        return "private " + getLoginUser()
    }

    fun getLoginUser() : String {
        return SecurityContextHolder.getContext().authentication.name
    }
}
