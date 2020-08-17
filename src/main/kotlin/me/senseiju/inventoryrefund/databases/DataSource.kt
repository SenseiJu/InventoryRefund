package me.senseiju.inventoryrefund.databases

import com.zaxxer.hikari.HikariDataSource
import me.senseiju.inventoryrefund.InventoryRefund

interface DataSource : Queries {
    val main: InventoryRefund
    var dataSource: HikariDataSource

    fun configureDataSource(url: String, username: String?, password: String?, driverClassName: String) {}

    fun close() = dataSource.close()
}