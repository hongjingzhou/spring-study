package tech.edison.hongjing.study.jpacache

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class DbOp {
    @Autowired
    lateinit var userRepo: UserRepository

    @Cacheable(value = ["user"], key = "#email")
    fun get(email: String) : User? {
        return userRepo.findByIdOrNull(email)
    }

    @CachePut(value = ["user"], key = "#email")
    fun add(email: String, name: String, avatar: String?) : User {
        return userRepo.save(User(email, name, avatar, null))
    }

    @CachePut(value = ["user"], key = "#email")
    fun update(email: String, name: String, avatar: String?, age: Int?) : User {
        return userRepo.save(User(email, name, avatar, age))
    }

    fun list() : List<User> {
        return userRepo.findAll()
    }

    @CacheEvict(value = ["user"], key = "#email", beforeInvocation = true)
    fun delete(email: String) {
        userRepo.deleteById(email)
    }

    fun listOldGuys(age: Int) : List<User> {
        return userRepo.listOldGuys(age)
    }
}
