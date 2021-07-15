package com.heilig.demo.controller.soap

import com.heilig.demo.controller.api.UserApi
import com.heilig.demo.service.UserService
import com.heilig.demo.wsdl.DemoPortType
import com.heilig.demo.xsd.PageableItem
import com.heilig.demo.xsd.SearchUsers
import com.heilig.demo.xsd.UserDto
import javax.jws.WebService
import javax.xml.ws.Holder

/**
 * @author sebastien.heilig
 * @since 1.0.0
 */
@WebService(
    portName = "DemoPortType", serviceName = "UserService",
    targetNamespace = "http://demo.heilig.com/wsdl",
    endpointInterface = "com.heilig.demo.wsdl.DemoPortType"
)
class UserSoapController(private val userService: UserService) : UserApi, DemoPortType {

    override fun createUser(user: Holder<UserDto>?) {
        if (user != null) {
            user.value = createUser(userService, user.value)
        }
    }



    override fun retrieveUsers(searchUsers: SearchUsers?): PageableItem = retrieveUsers(userService, searchUsers)
}