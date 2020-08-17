package me.senseiju.inventoryrefund.extensions

fun List<String>.color() : List<String> {
    return this.map(String::color)
}