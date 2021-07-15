package com.heilig.demo.controller.rest

import com.heilig.demo.controller.api.UserApi
import com.heilig.demo.service.UserService
import com.heilig.demo.xsd.Pagination
import com.heilig.demo.xsd.SearchUsers
import com.heilig.demo.xsd.SortOrder
import com.heilig.demo.xsd.UserDto
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @author sebastien.heilig
 * @since 1.0.0
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class UserRestController(val userService: UserService) : UserApi, SortableRestController {

    companion object {

        const val ID: String = "id"
        const val FIRSTNAME: String = "firstname"
        const val LASTNAME: String = "lastname"
        const val LIMIT: String = "limit"
        const val OFFSET: String = "offset"
        const val SORT: String = "sort"
        const val SORT_ORDER: String = "order"
    }


    @GET
    fun retrieveUsersResponse(
        @QueryParam(ID) id: Long?,
        @QueryParam(FIRSTNAME) firstname: String?,
        @QueryParam(LASTNAME) lastname: String?,
        @QueryParam(LIMIT) limit: Int?,
        @QueryParam(OFFSET) offset: Int?,
        @QueryParam(SORT) sort : List<String>,
        @QueryParam(SORT_ORDER) sortOrder : List<SortOrder>
    ): Response {

        var searchUsers = SearchUsers()
        searchUsers.id = id
        searchUsers.lastname = lastname
        searchUsers.firstname = firstname
        searchUsers.pagination = Pagination()
        searchUsers.pagination.limit = limit
        searchUsers.pagination.offset = offset
        searchUsers.sort = addSortIfAny(sort, sortOrder)
        return Response.ok(retrieveUsers(userService, searchUsers)).build()
    }

    @POST
    fun createUserResponse(userDto: UserDto): Response = Response.ok(createUser(userService, userDto)).build()

    @Path("/{id}")
    @PATCH
    fun updateUserResponse(@PathParam("id") id: Long, userDto: UserDto): Response {

        val user = updateUser(userService, id, userDto) ?: return Response.status(400).build()
        return Response.ok(user).build()
    }

    @Path("/{id}")
    @DELETE
    fun deleteUserResponse(@PathParam("id") id: Long): Response = if (deleteUser(userService, id)) Response.ok().build() else Response.status(404).build()
}