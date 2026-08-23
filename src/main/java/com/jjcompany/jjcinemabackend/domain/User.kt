package com.jjcompany.jjcinemabackend.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var userId: Long? = null

    @Column(nullable = false, unique = true)
    var email: String? = null

    @Column(nullable = false)
    var password: String? = null

    @Column(nullable = false, length = 50)
    var name: String? = null

    @Column(nullable = false, length = 20)
    var role: String? = "CUSTOMER"

    @Column(nullable = false)
    var active: Boolean? = true

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime? = null

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime? = null

    @Column(name = "updated_by", length = 50)
    var updatedBy: String? = null

    companion object {
        @JvmStatic
        fun create(email: String, password: String, name: String): User {
            val user = User()
            user.email = email
            user.password = password
            user.name = name
            return user
        }
    }
}
