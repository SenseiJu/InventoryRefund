package me.senseiju.inventoryrefund.databases.sql

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.senseiju.inventoryrefund.databases.DataSource
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.util.*

interface SQLDataSource : DataSource {

    override fun configureDataSource(url: String, username: String?, password: String?, driverClassName: String) {
        val config = HikariConfig()
        config.jdbcUrl = url
        config.username = username
        config.password = password
        config.driverClassName = driverClassName
        config.connectionTestQuery = "SELECT 1"

        dataSource = HikariDataSource(config)
    }

    suspend fun performQuery(statement : PreparedStatement): ResultSet {
        return statement.executeQuery()
    }

    override suspend fun addDeathRecord(uuid: UUID, killer: UUID?, inventory: String, date: String, location: String) {
        withContext(Dispatchers.IO) {
            dataSource.connection.use {
                val statement =
                    "INSERT INTO `player_inventory_data` (`uuid`, `killer`, `inventory`, `date`, `location`) VALUES(?,?,?,?,?);"
                val preparedStatement = it.prepareStatement(statement)

                preparedStatement.setString(1, uuid.toString())
                preparedStatement.setString(2, killer.toString())
                preparedStatement.setString(3, inventory)
                preparedStatement.setString(4, date)
                preparedStatement.setString(5, location)

                preparedStatement.executeUpdate()
            }
        }
    }

    override suspend fun getDeathRecords(uuid: UUID): List<Map<String, String>> {
        dataSource.connection.use {
            val resultsList = ArrayList<Map<String, String>>()
            val statement = "SELECT * FROM `player_inventory_data` WHERE `uuid` = ?;"
            val preparedStatement = it.prepareStatement(statement)

            preparedStatement.setString(1, uuid.toString())

            val resultSet = performQuery(preparedStatement)

            val metaData = resultSet.metaData
            while (resultSet.next()) {
                val results = HashMap<String, String>()
                for (i in 1..metaData.columnCount) {
                    results[metaData.getColumnLabel(i)] = resultSet.getString(i)
                }
                resultsList.add(results)
            }

            return resultsList
        }
    }

    override suspend fun removeDeathRecordsByTime(time: String) {
        withContext(Dispatchers.IO) {
            dataSource.connection.use {
                val statement = "DELETE FROM `player_inventory_data` WHERE `date` < ?;"
                val preparedStatement = it.prepareStatement(statement)

                preparedStatement.setString(1, time)

                preparedStatement.executeUpdate()
            }
        }
    }

    override suspend fun removeAllDeathRecords() {
        withContext(Dispatchers.IO) {
            dataSource.connection.use {
                val statement = "DELETE FROM `player_inventory_data`;"
                val preparedStatement = it.prepareStatement(statement)

                preparedStatement.executeUpdate()
            }
        }
    }
}