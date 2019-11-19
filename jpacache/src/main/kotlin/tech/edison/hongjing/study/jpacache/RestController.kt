package tech.edison.hongjing.study.jpacache

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.bind.annotation.RestController

@RestController
class RestController {

    @Autowired
    lateinit var dbOp: DbOp

    @GetMapping("/user")
    fun get(@RequestParam email:String) : User? {
        return dbOp.get(email)
    }

    @PostMapping("/user/add")
    fun add(@RequestParam email:String, @RequestParam name: String, @RequestParam avatar: String?) : User {
        return dbOp.add(email, name, avatar)
    }

    @PostMapping("/user/update")
    fun update(@RequestParam email:String,
               @RequestParam name: String,
               @RequestParam avatar: String?,
               @RequestParam age: Int?) : User {
        return dbOp.update(email, name, avatar, age)
    }

    @DeleteMapping("/user")
    fun delete(@RequestParam email:String) : String {
        dbOp.delete(email)
        return "done"
    }

    @GetMapping("/users")
    fun list() : List<User> {
        return dbOp.list()
    }

    @GetMapping("/oldUsers")
    fun listOldGuys(@RequestParam age: Int) : List<User> {
        return dbOp.listOldGuys(age)
    }
}

