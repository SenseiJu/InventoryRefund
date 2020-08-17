package me.senseiju.inventoryrefund.databases.sql

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.senseiju.inventoryrefund.InventoryRefund
import me.senseiju.inventoryrefund.utils.scopeDefault

class SQLite (override val main: InventoryRefund, databaseName: String?  = "ir_database",
              driverClassName: String) : SQLDataSource {

    override lateinit var dataSource: HikariDataSource

    init {
        val url = "jdbc:sqlite:${main.dataFolder}\\$databaseName.db"
        configureDataSource(url, null, null, driverClassName)

        scopeDefault.launch { createTables() }
    }

    override suspend fun createTables() {
        withContext(Dispatchers.IO) {
            dataSource.connection.use {
                val statement = "CREATE TABLE IF NOT EXISTS `player_inventory_data` (" +
                        "`inventory_id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "`uuid` TEXT NOT NULL," +
                        "`killer` TEXT," +
                        "`inventory` TEXT," +
                        "`date` TEXT," +
                        "`location` TEXT);"
                val preparedStatement = it.prepareStatement(statement)

                preparedStatement.execute()
            }
        }
    }
}