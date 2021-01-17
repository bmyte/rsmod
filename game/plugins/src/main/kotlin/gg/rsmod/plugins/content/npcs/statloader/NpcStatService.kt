package gg.rsmod.plugins.content.npcs.statloader

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gg.rsmod.game.Server
import gg.rsmod.game.model.World
import gg.rsmod.game.service.Service
import gg.rsmod.plugins.api.ext.appendToString
import gg.rsmod.plugins.content.mechanics.doors.DoorService
import gg.rsmod.util.ServerProperties
import java.io.FileNotFoundException
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Paths

class NpcStatService : Service {


    val npcData = HashMap<Int, NPCWrapper>()

    override fun init(server: Server, world: World, serviceProperties: ServerProperties) {

        val statsFile = Paths.get(serviceProperties.get("npc-stats") ?: "./data/cfg/npcwikistats.json")
        if (!Files.exists(statsFile)) {
            throw FileNotFoundException("Path does not exist. $statsFile")
        }

        Files.newBufferedReader(statsFile).use { reader ->

            val fileContent = reader.readText()
            val gson = Gson()

            val type: Type = object : TypeToken<Map<Int?, NPCWrapper?>?>() {}.getType()
            val myMap =
                gson.fromJson<Map<Int, NPCWrapper>>(
                    fileContent,
                    type
                )

            for (key in myMap.keys) {
                val data = myMap[key]
                if (data == null) {
                    System.err.println("Key $key in npcwikistats.json has no value")
                    continue
                }
                npcData[key] = data
            }
        }

        DoorService.logger.info { "Loaded ${npcData.size.appendToString("npc stats")}." }
    }

    override fun postLoad(server: Server, world: World) {
    }

    override fun bindNet(server: Server, world: World) {
    }

    override fun terminate(server: Server, world: World) {
    }

    class NPCWrapper(
        var name: String,
        var hitpoints: Int = 1,
        var slayerLevel: Int = 1,

        var combatLevel: Int = 1,
        var attackSpeed: Int = 1,

        var attackLevel: Int = 1,
        var strengthLevel: Int = 1,
        var defenceLevel: Int = 1,
        var rangeLevel: Int = 1,
        var magicLevel: Int = 1,

        var stabDef: Int = 1,
        var slashDef: Int = 1,
        var crushDef: Int = 1,
        var rangeDef: Int = 1,
        var magicDef: Int = 1,

        var bonusAttack: Int = 1,
        var bonusStrength: Int = 1,
        var bonusRangeStrength: Int = 1,
        var bonusMagicDamage: Int = 1,

        var poisonImmune: Boolean = false,
        var venomImmune: Boolean = false
    ) {
    }
}