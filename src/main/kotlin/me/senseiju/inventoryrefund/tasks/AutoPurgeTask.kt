package me.senseiju.inventoryrefund.tasks

import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import me.senseiju.inventoryrefund.InventoryRefund
import me.senseiju.inventoryrefund.Lang
import me.senseiju.inventoryrefund.extensions.color
import me.senseiju.inventoryrefund.utils.dateFormat
import me.senseiju.inventoryrefund.utils.scopeDefault
import me.senseiju.inventoryrefund.utils.timeRegex
import java.util.*

class AutoPurgeTask(private val main: InventoryRefund, timeString: String) : Runnable {
    private val times: List<String> = timeString.split(",")
    private val console = main.server.consoleSender

    override fun run() {
        scopeDefault.launch {
            val cal = Calendar.getInstance()
            cal.time = Date()

            console.sendMessage("${Lang.PREFIX}&aAuto purge has begun!".color())

            for (t in times) {
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
                    console.sendMessage("${Lang.PREFIX}&cInvalid time string entered in auto purge config!".color())
                    console.sendMessage(("${Lang.PREFIX}&cCancelling the auto purge task, restart server to begin again!").color())

                    this.cancel()

                    return@launch
                }
            }
            main.db.removeDeathRecordsByTime(dateFormat.format(cal.time))
            console.sendMessage("${Lang.PREFIX}&aAuto purge has finished!".color())
        }
    }
}