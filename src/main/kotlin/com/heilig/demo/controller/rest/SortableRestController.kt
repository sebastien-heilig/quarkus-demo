package com.heilig.demo.controller.rest

import com.heilig.demo.xsd.Map
import com.heilig.demo.xsd.SortOrder

/**
 * @author sebastien.heilig
 * @since 1.0.0
 */
interface SortableRestController {

    fun addSortIfAny(sort: List<String>, sortOrder: List<SortOrder>): Map {

        val result = com.heilig.demo.xsd.Map()
        if (sort.isEmpty()) {
            return result
        }
        var defaultSortOrder = completeSortListWithDefaultValueIfMissing(sortOrder, sort.size)
        for(i in sort.withIndex()){
            val entry = Map.Entry()
            entry.key = i.value
            entry.value = defaultSortOrder.get(i.index)
            result.entry.add(i.index, entry)
        }
        return result
    }

    private fun completeSortListWithDefaultValueIfMissing(sortOrder: List<SortOrder>, max: Int): List<SortOrder> {

        println("sortOrder.size " + sortOrder.size)
        println("max $max")
        var result = ArrayList<SortOrder>()
        result.addAll(sortOrder)
        var i = result.size
        while(i <= max){
            result.add(SortOrder.ASC)
            i++
        }
        return result
    }
}