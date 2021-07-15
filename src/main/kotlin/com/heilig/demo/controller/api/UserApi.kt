package com.heilig.demo.controller.api

import com.heilig.demo.service.UserService
import com.heilig.demo.xsd.PageableItem
import com.heilig.demo.xsd.SearchUsers
import com.heilig.demo.xsd.UserDto

/**
 * @author sebastien.heilig
 * @since 1.0.0
 */
interface UserApi {

    fun createUser(userService: UserService, userDto: UserDto): UserDto = userService.createUser(userDto)

    fun retrieveUsers(userService: UserService, searchUsers: SearchUsers?): PageableItem = userService.retrieveUsers(searchUsers)

    fun updateUser(userService: UserService, id: Long, userDto: UserDto): UserDto? = userService.updateUser(id, userDto)

    fun deleteUser(userService: UserService, id: Long): Boolean = userService.deleteUser(id)

}
