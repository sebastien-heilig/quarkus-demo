package com.heilig.demo.model

import com.heilig.demo.utils.DateUtils
import com.heilig.demo.xsd.UserDto
import io.quarkus.hibernate.orm.panache.PanacheEntityBase
import java.time.LocalDate
import java.time.Period
import javax.persistence.*

/**
 * @author sebastien.heilig
 * @since 1.0.0
 */
@Table
@Entity
data class User(
    @Id
    @SequenceGenerator(
        name = "userseq",
        sequenceName = "user_id_seq",
        allocationSize = 1,
        initialValue = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userseq") var id: Long? = null,
    var firstname: String = "",
    var lastname: String = "",
    var birthDate: LocalDate? = null
) : PanacheEntityBase() {

    constructor(userDto: UserDto) : this() {

        this.id = null
        this.firstname = userDto.firstname
        this.lastname = userDto.lastname
        this.birthDate = DateUtils.parse(userDto.birthDate)
    }

    fun toUserDto(): UserDto {

        val userDto = UserDto()
        userDto.id = this.id
        userDto.firstname = this.firstname
        userDto.lastname = this.lastname
        userDto.birthDate = DateUtils.format(this.birthDate!!)
        userDto.age = computeAge()
        return userDto
    }

    fun computeAge(): Int = Period.between(this.birthDate!!, LocalDate.now()).years

}