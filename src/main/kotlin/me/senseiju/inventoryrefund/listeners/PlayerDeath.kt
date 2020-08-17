package me.senseiju.inventoryrefund.listeners

import kotlinx.coroutines.launch
import me.senseiju.inventoryrefund.InventoryRefund
import me.senseiju.inventoryrefund.extensions.format
import me.senseiju.inventoryrefund.extensions.serialize
import me.senseiju.inventoryrefund.utils.dateFormat
import me.senseiju.inventoryrefund.utils.scopeDefault
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import java.util.*

class PlayerDeath(private val main : InventoryRefund) : Listener {
    @EventHandler
    fun playerDeath(event : PlayerDeathEvent) {
        scopeDefault.launch {
            val player = event.entity
            val date = dateFormat.format(Date())

            if (player.killer != null) {
                main.db.addDeathRecord(
                    player.uniqueId, (player.killer as Player).uniqueId, player.inventory.serialize(), date,
                    player.location.format()
                )
            } else {
                main.db.addDeathRecord(
                    player.uniqueId, null, player.inventory.serialize(), date, player.location.format()
                )
            }
        }
    }
}