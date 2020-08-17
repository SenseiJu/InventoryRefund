package me.senseiju.inventoryrefund.utils

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import java.io.ByteArrayInputStream
import java.util.*

fun deserializeInventoryContents(b64String: String?) : Array<ItemStack> {
    val bis = ByteArrayInputStream(Base64.getDecoder().decode(b64String))
    val ois = BukkitObjectInputStream(bis)

    return ois.readObject() as Array<ItemStack>
}