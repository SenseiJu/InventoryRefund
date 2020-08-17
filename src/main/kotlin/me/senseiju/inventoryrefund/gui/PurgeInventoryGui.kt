package me.senseiju.inventoryrefund.gui

import kotlinx.coroutines.launch
import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.components.xseries.XMaterial
import me.mattstudios.mfgui.gui.guis.Gui
import me.senseiju.inventoryrefund.InventoryRefund
import me.senseiju.inventoryrefund.Lang
import me.senseiju.inventoryrefund.extensions.color
import me.senseiju.inventoryrefund.utils.scopeDefault
import org.bukkit.entity.Player

class PurgeInventoryGui(private val main: InventoryRefund) {
    fun createPurgeConfirmationGui(player: Player, time: String) {
        val title = "&aPurge inventory".color()

        val gui = Gui(3, title)

        gui.setDefaultClickAction { it.isCancelled = true }

        val confirmLore = ArrayList<String>()
        confirmLore.add("")
        confirmLore.add("&bBy clicking this button you confirm the purge")
        confirmLore.add("&bof all inventories in the database specified")
        confirmLore.add("&bby the time frame you have provided.")
        confirmLore.add("")
        confirmLore.add("&c&lWARNING: &cOnce pressed, any and every record")
        confirmLore.add("&cin the database that matches the time frame you")
        confirmLore.add("&chave specified will be removed. This action is")
        confirmLore.add("&cnot able to be undone so if make a backup if required!")

        gui.setItem(11, ItemBuilder.from(XMaterial.GREEN_WOOL.parseItem()!!)
            .setName("&a&lConfirm inventory purge".color())
            .setLore(confirmLore.color())
            .asGuiItem {
                scopeDefault.launch {
                    if (time.equals("all", true)) main.db.removeAllDeathRecords() else main.db.removeDeathRecordsByTime(time)
                    player.sendMessage("${Lang.PREFIX}&aPurged inventory database!".color()) }
                gui.close(player) })

        val cancelLore = ArrayList<String>()
        cancelLore.add("")
        cancelLore.add("&bClick to cancel the inventory purge.")
        cancelLore.add("&bNo changes will be made to the database.")

        gui.setItem(15, ItemBuilder.from(XMaterial.RED_WOOL.parseItem()!!)
            .setName("&c&lCancel inventory purge".color())
            .setLore(cancelLore.color())
            .asGuiItem { gui.close(player) })

        gui.filler.fill(
            ItemBuilder.from(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()!!)
            .setName(" ")
            .asGuiItem())

        gui.open(player)
    }
}