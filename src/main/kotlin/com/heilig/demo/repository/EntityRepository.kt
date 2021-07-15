package com.heilig.demo.repository

import com.heilig.demo.xsd.PageableItem
import com.heilig.demo.xsd.Pagination
import com.heilig.demo.xsd.SortOrder
import io.quarkus.hibernate.orm.panache.PanacheQuery
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase
import io.quarkus.panache.common.Page
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author sebastien.heilig
 * @since 1.0.0
 */
abstract class EntityRepository<T>(
    @ConfigProperty(name = "com.heilig.props.pagination.limit") private val defaultLimit: Int = 10,
    private val defaultOffset: Int = 0
) : PanacheRepositoryBase<T, Long> {

    // Constants
    val and = " and "
    val or = " or "

    //region: Public Methods

    fun findAnyEntitiesByParams(pagination: Pagination, searchParams: Map<String, Any>?, andOr: String, sortParams: Map<String, SortOrder>?): PageableItem {

        validateOrInitOffsetAndLimit(pagination)
        val response = initPageableItemResponse(pagination)
        LOGGER.debug("limit -> {}, offset -> {}", response.pagination.limit, response.pagination.offset)
        if (searchParams == null) {
            // no result
            LOGGER.debug("No search params")
            response.pagination.total = 0
            return response
        }
        //results
        LOGGER.debug("There are search params")
        val query = buildSearchQuery(searchParams, andOr, sortParams)
        response.pagination.total = query.count()
        response.result = query.page<T>(Page.of(response.pagination.offset, response.pagination.limit)).list<T>()
        return response
    }

    fun findAnyEntitiesByParams(pagination: Pagination, searchParams: Map<String, Any>?, operators: List<String>, sortParams: Map<String, SortOrder>): PageableItem {

        validateOrInitOffsetAndLimit(pagination)
        val response = initPageableItemResponse(pagination)
        if (searchParams == null) {
            // no result
            response.pagination.total = 0
            return response
        }
        //results
        val query = buildSearchQuery(searchParams, operators, sortParams)
        response.pagination.total = query.count()
        response.result = query.page<T>(Page.of(response.pagination.offset, response.pagination.limit)).list<T>()
        return response
    }

    private fun initPageableItemResponse(pagination: Pagination): PageableItem {

        val response = PageableItem()
        response.pagination = Pagination()
        response.pagination.limit = pagination.limit
        response.pagination.offset = pagination.offset
        return response
    }

    fun findAnyEntityByParams(searchParams: Map<String, Any>, andOr: String): T {

        val results: List<T> = this.find(buildSearchQuery(searchParams, andOr)).list()
        if (results.size != 1) {
            throw RepositoryException("There is 0 or more results matching your request! '" + results.size + "' element(s)")
        }
        return results[0]
    }

    //endregion

    //region: Protected Methods
    fun validateOrInitOffsetAndLimit(pagination: Pagination) {

        if (pagination.limit == null) {
            pagination.limit = defaultLimit
        }
        if (pagination.offset == null) {
            pagination.offset = defaultOffset
        }
    }

    fun xsdMapToHashMap(xsdMap: com.heilig.demo.xsd.Map?): Map<String, SortOrder>? {

        if (xsdMap == null) return null
        val result = HashMap<String, SortOrder>()
        for (e in xsdMap.entry) {
            result[e.key] = e.value as SortOrder
        }
        return result
    }
    //endregion

    //region: Private Methods
    private fun buildSearchQuery(searchParams: Map<String, Any>, andOr: String, sortParams: Map<String, SortOrder>?): PanacheQuery<T> {

        var query = buildSearchQuery(searchParams, andOr)
        LOGGER.debug("Search Query is : {}", query)
        if (sortParams != null) {
            query = addSortToPanacheQuery(query, sortParams)
            LOGGER.debug("Search Query with sort is : {}", query)
        }
        return this.find(query, searchParams)
    }

    private fun buildSearchQuery(searchParams: Map<String, Any>, operators: List<String>, sortParams: Map<String, SortOrder>): PanacheQuery<T> {

        var query = buildSearchQuery(searchParams, operators)
        LOGGER.debug("Search Query is : {}", query)
        query = addSortToPanacheQuery(query, sortParams)
        LOGGER.debug("Search Query with sort is : {}", query)
        return this.find(query, searchParams)
    }

    private fun buildSearchQuery(searchParams: Map<String, Any>, andOr: String): String {

        if(searchParams.isEmpty()){
            return ""
        }
        val sb = StringBuilder()
        searchParams.keys.forEach { k -> sb.append(k, DELIMITER, k, andOr) }
        return sb.subSequence(0, sb.length - andOr.length).toString()
    }

    private fun buildSearchQuery(searchParams: Map<String, Any>, operators: List<String>): String {

        if (searchParams.size != operators.size + 1) {
            throw RepositoryException("The query cannot be built, the number of operators does not match the number of params!")
        }
        val sb = StringBuilder()
        for ((i, k) in searchParams.keys.withIndex()) {
            sb.append(k, DELIMITER, k, operators[i])
        }
        return sb.toString()
    }

    private fun addSortToPanacheQuery(query: String, params: Map<String, SortOrder>): String = query + SPACE + buildSort(params)

    private fun buildSort(params: Map<String, SortOrder>): String {

        if (params.isEmpty()) {
            return ""
        }
        val sb = StringBuilder()
        sb.append(ORDER_BY)
        for (param in params) {
            sb.append(param.key, SPACE, param.value.value(), LIST_DELIMITER)
        }
        // Remove the LIST_DELIMITER at the end
        val orderByQuery = sb.subSequence(0, sb.length - LIST_DELIMITER.length).toString()
        LOGGER.debug("Order By query : {}", orderByQuery)
        return orderByQuery
    }
    //endregion
}

// Private Constants
private const val DELIMITER = " = :"
private const val LIST_DELIMITER = " , "
private const val SPACE = " "
private const val ORDER_BY = "order by "

private val LOGGER: Logger = LoggerFactory.getLogger(EntityRepository::class.java.name)