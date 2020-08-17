package me.senseiju.inventoryrefund.extensions

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

fun PlayerInventory.serialize() : String {
    val bos = ByteArrayOutputStream()
    val oos = BukkitObjectOutputStream(bos)

    if (this.contents.size == 36) {
        val contents = this.contents.toMutableList()
        contents.addAll(this.armorContents)
        contents.add(ItemStack(Material.AIR))
        oos.writeObject(contents.toTypedArray())
    } else {
        oos.writeObject(this.contents)
    }

    return Base64.getEncoder().encodeToString(bos.toByteArray())
}

fun PlayerInventory.deserialize(b64String : String?, isVersion18OrLess : Boolean = false) {
    val bis = ByteArrayInputStream(Base64.getDecoder().decode(b64String))
    val ois = BukkitObjectInputStream(bis)

    if (isVersion18OrLess) {
        val deserializeContents = ois.readObject() as Array<ItemStack>
        val contents = ArrayList<ItemStack>()
        val armorContents = ArrayList<ItemStack>()
        for (i in 0 until 36) {
            contents.add(deserializeContents[i])
        }
        for (i in 36 until 40) {
            armorContents.add(deserializeContents[i])
        }
        this.contents = contents.toTypedArray()
        this.setArmorContents(armorContents.toTypedArray())
    } else {
        this.contents = ois.readObject() as Array<ItemStack>
    }
}