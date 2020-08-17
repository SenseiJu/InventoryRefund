package me.senseiju.inventoryrefund

import me.senseiju.inventoryrefund.extensions.color
import org.bukkit.configuration.file.YamlConfiguration

enum class Lang(private val path : String, private  val def : String) {
    PREFIX("prefix", "&e&lInventoryRefund");

    companion object {
        lateinit var lang : YamlConfiguration
    }

    override fun toString() : String {
        if (this == PREFIX) {
            return (lang.getString(this.path, this.def) + " ").color()
        }
        return (lang.getString(this.path, this.def) + "").color()
    }
}