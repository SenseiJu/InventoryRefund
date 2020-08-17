package me.senseiju.inventoryrefund.commands

import kotlinx.coroutines.launch
import me.mattstudios.mf.annotations.Command
import me.mattstudios.mf.annotations.Default
import me.mattstudios.mf.annotations.Permission
import me.mattstudios.mf.annotations.WrongUsage
import me.mattstudios.mf.base.CommandBase
import me.senseiju.inventoryrefund.InventoryRefund
import me.senseiju.inventoryrefund.Lang
import me.senseiju.inventoryrefund.extensions.color
import me.senseiju.inventoryrefund.utils.scopeDefault
import org.bukkit.entity.Player

@Command("Refund")
class Refund(private val main : InventoryRefund) : CommandBase() {
    @Default
    @Permission("inventoryrefund.refund")
    @WrongUsage("&bCorrect usage: &e/Refund <Player>")
    fun default(sender : Player, target : Player?) {
        if (target == null) {
            sender.sendMessage("${Lang.PREFIX}&cYou must specify an online player!".color())
            return
        }
        scopeDefault.launch {
            val records = main.db.getDeathRecords(target.uniqueId)

            main.refundGui.createSelectPlayerInventoryGui(sender, target, records)
        }
    }
}