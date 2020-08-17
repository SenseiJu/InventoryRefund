package me.senseiju.inventoryrefund

import me.mattstudios.mf.base.CommandManager
import me.senseiju.inventoryrefund.commands.PurgeInventory
import me.senseiju.inventoryrefund.commands.Refund
import me.senseiju.inventoryrefund.databases.DataSource
import me.senseiju.inventoryrefund.databases.sql.MySQL
import me.senseiju.inventoryrefund.databases.sql.SQLite
import me.senseiju.inventoryrefund.extensions.color
import me.senseiju.inventoryrefund.files.LangFile
import me.senseiju.inventoryrefund.gui.PurgeInventoryGui
import me.senseiju.inventoryrefund.gui.RefundGui
import me.senseiju.inventoryrefund.listeners.PlayerDeath
import me.senseiju.inventoryrefund.tasks.AutoPurgeTask
import org.bukkit.plugin.java.JavaPlugin

class InventoryRefund : JavaPlugin() {
    var serverVersion18OrLess = false

    lateinit var db : DataSource
    lateinit var purgeTask: AutoPurgeTask

    private lateinit var lang : LangFile

    lateinit var refundGui : RefundGui
    lateinit var purgeInventoryGui: PurgeInventoryGui

    private lateinit var commandManager : CommandManager

    override fun onEnable() {
        lang = LangFile(this)
        refundGui = RefundGui(this)
        purgeInventoryGui = PurgeInventoryGui(this)

        commandManager = CommandManager(this)
        commandManager.register(Refund(this), PurgeInventory(this))

        server.pluginManager.registerEvents(PlayerDeath(this), this)

        serverVersion18OrLess = isServerVersion18OrLess()

        saveDefaultConfig()

        db = when {
            config.getString("databaseType").equals("mysql", true) -> {
                val host = config.getString("mysql.host")
                val port = config.getString("mysql.port")
                val databaseName = config.getString("databaseName")
                val username = config.getString("mysql.username")
                val password = config.getString("mysql.password")
                MySQL(this, host, port, databaseName, username, password, "com.mysql.cj.jdbc.Driver")
            }
            else -> {
                val databaseName = config.getString("databaseName")
                SQLite(this, databaseName, "org.sqlite.JDBC")
            }
        }

        if (config.getBoolean("autoPurge.enabled")) {
            purgeTask = AutoPurgeTask(this, config.getString("autoPurge.purgeByTime", "1h") + "")
            server.scheduler.runTaskTimerAsynchronously(this, purgeTask, 0L, config.getLong("autoPurge.interval", 72000))
        }

        if (isEnabled) {
            enabledMessage()
        } else {
            server.consoleSender.sendMessage("&bInventoryRefund has been disabled!")
        }
    }

    override fun onDisable() {
        db.close()
        server.scheduler.cancelTasks(this)
    }

    private fun enabledMessage() {
        val console = server.consoleSender
        console.sendMessage("&c------------------------------------------------".color())
        console.sendMessage("&eInventoryRefund &bhas been enabled successfully!".color())
        console.sendMessage("&bAdd &aSenseiJu#6692 &bon discord for support or".color())
        console.sendMessage("&bcreate a conversation on MC-Market. Check out my".color())
        console.sendMessage("&bother resources on my MC-Market page!".color())
        console.sendMessage("&ahttps://www.mc-market.org/resources/authors/206823/".color())
        console.sendMessage("&c------------------------------------------------".color())
    }

    private fun isServerVersion18OrLess() : Boolean {
        return server.version.contains("1.7") || server.version.contains("1.8")
    }
}