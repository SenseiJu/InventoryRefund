package me.senseiju.inventoryrefund.databases.sql

import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.senseiju.inventoryrefund.InventoryRefund
import me.senseiju.inventoryrefund.utils.scopeDefault

class MySQL(override val main: InventoryRefund, host: String? = "localhost", port: String? = "3306",
            databaseName: String? = "ir_database", username: String? = null, password: String? = null,
            driverClassName: String) : SQLDataSource {

    override lateinit var dataSource: HikariDataSource

    init {
        val url = "jdbc:mysql://$host:$port/$databaseName?verifyServerCertificate=false&useSSL=true"
        configureDataSource(url, username, password, driverClassName)

        scopeDefault.launch { createTables(); setSafeUpdates()}
    }

    private suspend fun setSafeUpdates() {
        withContext(Dispatchers.IO) {
            dataSource.connection.use {
                val statement = "SET sql_safe_updates = 0;"
                val preparedStatement = it.prepareStatement(statement)

                preparedStatement.execute()
            }
        }
    }

    override suspend fun createTables() {
        withContext(Dispatchers.IO) {
            dataSource.connection.use {
                val statement = "CREATE TABLE IF NOT EXISTS `player_inventory_data` (" +
                        "`inventory_id` INTEGER PRIMARY KEY AUTO_INCREMENT," +
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