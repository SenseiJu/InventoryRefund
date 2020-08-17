package me.senseiju.inventoryrefund.gui

import me.mattstudios.mfgui.gui.components.ItemBuilder
import me.mattstudios.mfgui.gui.components.xseries.XMaterial
import me.mattstudios.mfgui.gui.guis.Gui
import me.mattstudios.mfgui.gui.guis.GuiItem
import me.mattstudios.mfgui.gui.guis.PaginatedGui
import me.senseiju.inventoryrefund.InventoryRefund
import me.senseiju.inventoryrefund.Lang
import me.senseiju.inventoryrefund.extensions.color
import me.senseiju.inventoryrefund.extensions.deserialize
import me.senseiju.inventoryrefund.utils.dateFormat
import me.senseiju.inventoryrefund.utils.deserializeInventoryContents
import me.senseiju.inventoryrefund.utils.millisecondsToHMS
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList


class RefundGui(private val main: InventoryRefund) {
    fun createSelectPlayerInventoryGui(player: Player, target: Player, _resultsList: List<Map<String, String>>) {
        val resultsList = _resultsList.reversed()

        val title = "&a${target.name}".color()

        val gui = PaginatedGui(6, 45, title)
        gui.setDefaultClickAction { it.isCancelled = true }

        for ((index, result) in resultsList.withIndex()) {
            val location = result["location"]?.split(":")

            val killerUUID = if (result["killer"] == "null") null else UUID.fromString(result["killer"])
            val killerName = if (killerUUID == null) "&c<KillerNotFound>".color() else main.server.getOfflinePlayer(killerUUID).name

            val deathDataGuiItem = createDeathDataItem(index + 1, result["date"], killerName, location)
            deathDataGuiItem.setAction { createPlayerInventoryContentsGui(player, target, index + 1,
                    result["date"], killerName, result["inventory"], location, gui) }
            gui.addItem(deathDataGuiItem)
        }

        gui.setItem(6, 3, ItemBuilder.from(XMaterial.PAPER.parseItem()!!)
                .setName("&bNext page".color())
                .asGuiItem { gui.next() })
        gui.setItem(6, 7, ItemBuilder.from(XMaterial.PAPER.parseItem()!!)
                .setName("&bPrevious page".color())
                .asGuiItem { gui.next() })

        gui.filler.fillBottom(ItemBuilder.from(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()!!)
                .setName(" ")
                .asGuiItem())

        main.server.scheduler.runTask(main, Runnable {
            gui.open(player)
        })
    }

    private fun createPlayerInventoryContentsGui(player: Player, target: Player, index : Int, date : String?,
                                                 killerName : String?, inventory : String?, location : List<String>?,
                                                 selectPlayerInventoryGui : PaginatedGui) {

        val title = "&a${target.name}".color()

        val gui = Gui(6, title)

        gui.setDefaultClickAction { it.isCancelled = true }

        gui.setItem(6, createDeathDataItem(index + 1, date, killerName, location))

        gui.setItem(7, ItemBuilder.from(XMaterial.GREEN_WOOL.parseItem()!!)
                .setName("&aApply inventory to player".color())
                .asGuiItem { createApplyInventoryConfirmationGui(player, target, inventory, gui) })

        gui.setItem(8, ItemBuilder.from(XMaterial.RED_WOOL.parseItem()!!)
                .setName("&cBack to player's inventories".color())
                .asGuiItem { selectPlayerInventoryGui.open(player) })

        val items: Array<ItemStack>
        try {
            items = deserializeInventoryContents(inventory)
        } catch (ex: NullPointerException) {
            player.sendMessage("${Lang.PREFIX}&cFailed to create contents GUI. Inventory may contain older/newer items.")
            return
        }

        for (i in 36 until 41) {
            gui.setItem(i - 36, GuiItem(if (items[i] == null) ItemStack(Material.AIR) else items[i]))
        }

        for (i in 0 until 9) {
            gui.setItem(i + 45, GuiItem(if (items[i] == null) ItemStack(Material.AIR) else items[i]))
        }

        for (i in 35 downTo 9) {
            gui.setItem(i + 9, GuiItem(if (items[i] == null) ItemStack(Material.AIR) else items[i]))
        }

        gui.filler.fill(ItemBuilder.from(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()!!)
                .setName(" ")
                .asGuiItem())

        main.server.scheduler.runTask(main, Runnable {
            gui.open(player)
        })
    }

    private fun createApplyInventoryConfirmationGui(player : Player, target : Player, inventory : String?, playerInventoryContentsGui : Gui) {
        val title = "&aApply inventory".color()

        val gui = Gui(3, title)

        gui.setDefaultClickAction { it.isCancelled = true }

        val confirmLore = ArrayList<String>()
        confirmLore.add("")
        confirmLore.add("&bBy clicking this button you will apply")
        confirmLore.add("&bthe previously shown inventory to the player.")
        confirmLore.add("")
        confirmLore.add("&c&lWARNING: &cOnce pressed, any and every item")
        confirmLore.add("&cin the player's current inventory will be lost!")
        confirmLore.add("&cIt is advised that the player in question relives")
        confirmLore.add("&cthemselves of all valuable items before applying")
        confirmLore.add("&cthe inventory refund!")

        gui.setItem(11, ItemBuilder.from(XMaterial.GREEN_WOOL.parseItem()!!)
                .setName("&a&lApply player's inventory refund".color())
                .setLore(confirmLore.color())
                .asGuiItem { applyInventoryRefund(player, target, inventory) })

        val cancelLore = ArrayList<String>()
        cancelLore.add("")
        cancelLore.add("&bClick to cancel the current inventory refund.")
        cancelLore.add("&bYou will be returned to the inventory's GUI")

        gui.setItem(15, ItemBuilder.from(XMaterial.RED_WOOL.parseItem()!!)
                .setName("&c&lCancel player inventory refund".color())
                .setLore(cancelLore.color())
                .asGuiItem { playerInventoryContentsGui.open(player) })

        gui.filler.fill(ItemBuilder.from(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem()!!)
                .setName(" ")
                .asGuiItem())

        main.server.scheduler.runTask(main, Runnable {
            gui.open(player)
        })

    }

    private fun applyInventoryRefund(player : Player, target : Player, inventory : String?) {
        player.closeInventory()
        player.sendMessage("${Lang.PREFIX}&a${target.name}'s inventory has been refunded.".color())

        target.inventory.deserialize(inventory, main.serverVersion18OrLess)
        target.sendMessage("${Lang.PREFIX}&aYour inventory has been refunded by ${player.name}.".color())

    }

    private fun createDeathDataItem(index : Int, date : String?, killerName : String?, location : List<String>?) : GuiItem {
        val lore = ArrayList<String>()
        lore.add("")
        lore.add("&bDate of death: &e${date}")
        lore.add("&bTime since killed: &e${millisecondsToHMS(Date().time - dateFormat.parse(date).time)}")
        lore.add("")
        lore.add("&bKiller's name: &e${killerName}")
        lore.add("")
        lore.add("&bLocation:")
        lore.add("  &bWorld: &e${location?.get(0)}")
        lore.add("  &bX: &e${location?.get(1)}")
        lore.add("  &bY: &e${location?.get(2)}")
        lore.add("  &bZ: &e${location?.get(3)}")

        return ItemBuilder.from(XMaterial.BOOK.parseItem()!!)
                .setName("&aInventory $index".color())
                .setLore(lore.color())
                .asGuiItem()
    }
}