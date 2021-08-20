package com.heilig.demo.service

import com.heilig.demo.model.User
import com.heilig.demo.repository.UserRepository
import com.heilig.demo.utils.DateUtils
import com.heilig.demo.xsd.PageableItem
import com.heilig.demo.xsd.SearchUsers
import com.heilig.demo.xsd.UserDto
import com.heilig.demo.xsd.UserDtoList
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped
import javax.transaction.Transactional

/**
 * @author sebastien.heilig
 * @since 1.0.0
 */
@ApplicationScoped
class UserService(val userRepository: UserRepository) {

    @Transactional
    fun createUser(userDto: UserDto): UserDto {

        val user = User(userDto)
        userRepository.persist(user)
        return user.toUserDto()
    }

    @Transactional
    fun retrieveUsers(): PageableItem {

        val response = userRepository.findAllUsersPaginated()
        if (response.result != null) {
            val users: List<User> = response.result as List<User>
            response.result = users.map { u -> u.toUserDto() }
        }
        return response
    }

    @Transactional
    fun retrieveUsers(searchUsers: SearchUsers?): PageableItem {

        if (searchUsers == null) {
            LOGGER.info("Search with no filters")
            return retrieveUsers()
        }
        LOGGER.debug("Filters : id -> {}, firstname -> {}, lastname -> {}", searchUsers.id, searchUsers.firstname, searchUsers.lastname)
        val response = userRepository.findByFiltersAnd(searchUsers)
        if (response.result != null) {
            val users: List<User> = response.result as List<User>
            var userDtoList = UserDtoList()
            userDtoList.usersDto.addAll(users.map { u -> u.toUserDto() })
            response.result = userDtoList
        }
        return response
    }

    @Transactional
    fun updateUser(id: Long, userDto: UserDto): UserDto? {

        val user: User = userRepository.findById(id) ?: return null
        if (userDto.birthDate != null) {
            user.birthDate = DateUtils.parse(userDto.birthDate)
        }
        if (StringUtils.isNotEmpty(userDto.firstname)) {
            user.firstname = userDto.firstname
        }
        if (StringUtils.isNotEmpty(userDto.lastname)) {
            user.lastname = userDto.lastname
        }
        userDto.age = user.computeAge()
        return userDto
    }

    @Transactional
    fun deleteUser(id: Long): Boolean {

        return userRepository.deleteById(id)
    }
}

// Constants
val LOGGER: Logger = LoggerFactory.getLogger(UserService::class.java.name)
