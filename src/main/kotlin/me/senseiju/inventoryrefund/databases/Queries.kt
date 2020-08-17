package me.senseiju.inventoryrefund.databases

import java.util.*

interface Queries {

    suspend fun createTables() {}

    suspend fun addDeathRecord(uuid: UUID, killer: UUID?, inventory: String, date: String, location: String) {}

    suspend fun getDeathRecords(uuid: UUID): List<Map<String, String>> {
        return emptyList()
    }

    suspend fun removeDeathRecordsByTime(time: String) {}

    suspend fun removeAllDeathRecords() {}
}