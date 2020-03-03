package tech.edison.hongjing.study.ssec

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/")
class WebController {

    @Autowired
    lateinit var pScan: PScan

    @RequestMapping("/public", method = [RequestMethod.GET])
    fun hello() : String {
        return "public " + getLoginUser()
    }

    @RequestMapping("/private", method = [RequestMethod.GET])
    fun secret() : String {
        return "private " + getLoginUser()
    }

    @RequestMapping("/scan", method = [RequestMethod.GET])
    fun scan() : String {
        pScan.start("/Volumes/Seagate2T/Master",
                "/Volumes/Seagate2T/Sega320重复")
        return "started"
    }

    class ScanReq(val master: String, val backup: String)
    @PostMapping("/public/scan2")
    fun scan2(@RequestBody body: ScanReq) : String {
        pScan.start(body.master, body.backup)
        return "started"
    }

    fun getLoginUser() : String {
        return SecurityContextHolder.getContext().authentication.name
    }
}
