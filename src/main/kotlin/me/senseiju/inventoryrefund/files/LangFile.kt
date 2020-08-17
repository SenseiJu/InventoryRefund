package me.senseiju.inventoryrefund.files

import me.senseiju.inventoryrefund.InventoryRefund
import me.senseiju.inventoryrefund.Lang
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException

class LangFile(private val main : InventoryRefund) {
    private val fileName = "lang.yml"

    private val file = File(main.dataFolder, fileName)

    private lateinit var config : YamlConfiguration

    init {
        initFile()
    }

    private fun initFile() {
        if (!file.exists()) {
            try {
                main.dataFolder.mkdir()
                file.createNewFile()
                main.saveResource(fileName, true)
            } catch (ex: IOException) {
                println("Failed to create lang file!")
            }
        }
        config = YamlConfiguration.loadConfiguration(file)
        Lang.lang = config
    }
}