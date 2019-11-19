package tech.edison.hongjing.study.jpacache

import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, String> {
    @Query(value = "select * from users where age > :age", nativeQuery = true)
    fun listOldGuys(@Param("age") age: Int) : List<User>
}
