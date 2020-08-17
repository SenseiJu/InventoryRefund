package me.senseiju.inventoryrefund.commands

import me.mattstudios.mf.annotations.*
import me.mattstudios.mf.base.CommandBase
import me.senseiju.inventoryrefund.InventoryRefund
import me.senseiju.inventoryrefund.Lang
import me.senseiju.inventoryrefund.extensions.color
import me.senseiju.inventoryrefund.utils.dateFormat
import me.senseiju.inventoryrefund.utils.timeRegex
import org.bukkit.entity.Player
import java.util.*

@Command("PurgeInventory")
class PurgeInventory(private val main : InventoryRefund) : CommandBase() {
    @Default
    @Permission("inventoryrefund.purge")
    @WrongUsage("&bCorrect usage: &e/PurgeInventory <Time>")
    fun default(sender : Player, timeString : String) {
        val cal = Calendar.getInstance()
        cal.time = Date()

        for (t in timeString.split(",")) {
            if (t.matches(timeRegex)) {
                when {
                    t.endsWith('w', true) -> {
                        cal.add(Calendar.WEEK_OF_MONTH, -t.substring(0, t.length - 1).toInt())
                    }
                    t.endsWith('d', true) -> {
                        cal.add(Calendar.DAY_OF_WEEK, -t.substring(0, t.length - 1).toInt())
                    }
                    t.endsWith('h', true) -> {
                        cal.add(Calendar.HOUR_OF_DAY, -t.substring(0, t.length - 1).toInt())
                    }
                    t.endsWith('m', true) -> {
                        cal.add(Calendar.MINUTE, -t.substring(0, t.length - 1).toInt())
                    }
                    t.endsWith('s', true) -> {
                        cal.add(Calendar.SECOND, -t.substring(0, t.length - 1).toInt())
                    }
                }
            } else {
                sender.sendMessage("${Lang.PREFIX}&cInvalid time string entered!".color())
                return
            }
        }

        main.purgeInventoryGui.createPurgeConfirmationGui(sender, dateFormat.format(cal.time))
    }

    @SubCommand("all")
    @Permission("inventoryrefund.purge.all")
    fun allSubCommand(sender : Player) {
        main.purgeInventoryGui.createPurgeConfirmationGui(sender, "all")
    }
}