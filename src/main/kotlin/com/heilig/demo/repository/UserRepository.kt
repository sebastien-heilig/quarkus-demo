package com.heilig.demo.repository

import com.heilig.demo.model.User
import com.heilig.demo.xsd.PageableItem
import com.heilig.demo.xsd.Pagination
import com.heilig.demo.xsd.SearchUsers
import org.apache.commons.lang3.StringUtils
import javax.enterprise.context.ApplicationScoped

/**
 * @author sebastien.heilig
 * @since 1.0.0
 */
@ApplicationScoped
class UserRepository : EntityRepository<User>() {

    fun findByFiltersAnd(searchUsers: SearchUsers): PageableItem = findAnyEntitiesByParams(searchUsers.pagination, searchUsersToMap(searchUsers), and, xsdMapToHashMap(searchUsers.sort))
    fun findByFiltersOr(searchUsers: SearchUsers): PageableItem = findAnyEntitiesByParams(searchUsers.pagination, searchUsersToMap(searchUsers), or, xsdMapToHashMap(searchUsers.sort))

    fun findAllUsersPaginated() : PageableItem {

        val pageableItem = PageableItem()
        pageableItem.pagination = Pagination()
        validateOrInitOffsetAndLimit(pageableItem.pagination)
        pageableItem.result = this.findAll().page<User>(pageableItem.pagination.offset, pageableItem.pagination.limit).list<User>()
        return pageableItem
    }

    fun searchUsersToMap(searchUsers: SearchUsers?): Map<String, Any>? {

        if (searchUsers == null)
            return null

        val map = HashMap<String, Any>()
        if (searchUsers.id != null) {
            map["id"] = searchUsers.id
        }
        if (StringUtils.isNotEmpty(searchUsers.firstname)) {
            map["firstname"] = searchUsers.firstname
        }
        if (StringUtils.isNotEmpty(searchUsers.lastname)) {
            map["lastname"] = searchUsers.lastname
        }
        return map
    }
}