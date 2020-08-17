package me.senseiju.inventoryrefund.extensions

import org.bukkit.Location

fun Location.format() : String {
    return "${world?.name}:$x:$y:$z"
}