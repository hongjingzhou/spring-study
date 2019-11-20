package tech.edison.hongjing.study.jpacache

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "users")
class User : Serializable {
    @Id
    @Column(name = "email", nullable = false, unique = true)
    var email: String = ""

    @Column(name = "name", nullable = false)
    var name: String = ""

    @Column(name = "avatar", nullable = true)
    var avatar: String? = null

    @Column(name = "age", nullable = true)
    var age: Int? = null

    constructor(email: String, name: String, avatar: String?, age: Int?) {
        this.email = email
        this.name = name
        this.avatar = avatar
        this.age = age
    }
}
