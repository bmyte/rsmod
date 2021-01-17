package gg.rsmod.plugins.content.npcs.statloader

import gg.rsmod.game.model.combat.NpcCombatDef

load_service(NpcStatService())

on_world_init {
    world.getService(NpcStatService::class.java)!!.let { service ->
        for (key in service.npcData.keys) {
            val data = service.npcData[key]
            if (data == null) {
                System.err.println("Key $key in ./data/npcwikistats.json has no value")
                continue
            }
            try {

                set_combat_def(key) {

                    //TODO NpcCombatDef.kt is missing venom and poison immunity

                    species {}

                    configs {
                        attackSpeed = data.attackSpeed
                        respawnDelay = NpcCombatDef.DEFAULT.respawnDelay

                    }

                    aggro {
                        radius = NpcCombatDef.DEFAULT.aggressiveRadius
                        searchDelay = NpcCombatDef.DEFAULT.aggroTargetDelay
                    }

                    stats {
                        hitpoints = data.hitpoints
                        attack = data.attackLevel
                        strength = data.strengthLevel
                        defence = data.defenceLevel
                        magic = data.magicLevel
                    }

                    bonuses {
                        defenceStab = data.stabDef
                        defenceSlash = data.slashDef
                        defenceCrush = data.crushDef
                        defenceMagic = data.magicDef
                        defenceRanged = data.rangeDef

                        attackBonus = data.bonusAttack
                        strengthBonus = data.bonusStrength
                        rangedStrengthBonus = data.bonusRangeStrength
                        magicDamageBonus = data.bonusMagicDamage
                    }

                    anims {
                        block = NpcCombatDef.DEFAULT_BLOCK_ANIMATION
                        death = NpcCombatDef.DEFAULT_DEATH_ANIMATION
                    }

                    slayerData {
                        xp = NpcCombatDef.DEFAULT.slayerXp
                        levelRequirement = data.slayerLevel
                    }
                }
            } catch (e: IllegalStateException) {
                println("Npc Wiki Stat plugin tried to define the stats of npc with id $key but they have already been set")
            }
        }
    }
}