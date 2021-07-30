package me.hazedev.hapi.player

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonStreamParser
import me.hazedev.hapi.component.Component
import me.hazedev.hapi.logging.Log
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.util.Collections
import java.util.UUID

class NameHistoryArchive : Component("name-history"), Listener {

    private val nameHistoryArchive = Collections.synchronizedMap(HashMap<UUID, ArrayList<String>>())
    lateinit var file: File

    override fun onEnable(): Boolean {
        file = File(dataFolder, "name-history.json")
        if (file.exists()) {
            try {
                BufferedReader(FileReader(file)).use {
                    val jsonNameHistoryArchive = JsonStreamParser(it).next().asJsonArray
                    for (jsonNameHistory in jsonNameHistoryArchive) {
                        try {
                            val nameHistory: Map.Entry<String, JsonElement> =
                                jsonNameHistory.asJsonObject.entrySet().first()
                            val uniqueId = UUID.fromString(nameHistory.key)
                            val names = ArrayList<String>(nameHistory.value.asJsonArray.map(JsonElement::getAsString))
                            nameHistoryArchive[uniqueId] = names
                        } catch (_: Exception) {
                        }
                    }
                }
            } catch (e: IOException) {
                Log.error(this, e, "Failed to load archived player names")
            }
        }
        return true
    }

    override fun save() {
        val jsonNameHistoryArchive = JsonArray()
        synchronized(nameHistoryArchive) {
            for (nameHistory in nameHistoryArchive) {
                val jsonNames = JsonArray()
                nameHistory.value.forEach(jsonNames::add)
                val jsonNameHistory = JsonObject()
                jsonNameHistory.add(nameHistory.key.toString(), jsonNames)
                jsonNameHistoryArchive.add(jsonNameHistory)
            }
        }
        try {
            FileWriter(file).use { fileWriter ->
                Gson().toJson(jsonNameHistoryArchive, fileWriter)
            }
        } catch (e: IOException) {
            Log.error(this, e, "Failed to save")
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val uniqueId = event.player.uniqueId
        val name = event.player.name
        val names = nameHistoryArchive[uniqueId]?: ArrayList()
        if (names.isEmpty()) {
            names.add(name)
            nameHistoryArchive[uniqueId] = names
        } else {
            if (names.last() != name) {
                names.add(name)
            }
        }
    }

    fun getLastKnownName(uniqueId: UUID): String? {
        return nameHistoryArchive[uniqueId]?.last()
    }

    fun getKnownNames(uniqueId: UUID): List<String> {
        return Collections.unmodifiableList(nameHistoryArchive[uniqueId]?: emptyList())
    }

    fun getAllLastKnownNames(): List<String> {
        val lastKnownNames = ArrayList<String>()
        nameHistoryArchive.values.mapTo(lastKnownNames, ArrayList<String>::last)
        return lastKnownNames
    }

}