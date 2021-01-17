package gg.rsmod.plugins.content.magic.alchemy

import gg.rsmod.game.message.impl.SynthSoundMessage
import gg.rsmod.plugins.content.magic.*
import kotlin.math.floor

/**
 * Timer key for delay in between using the alchemy spell
 */
val alchemyDelay = TimerKey()

/**
 * Timer key making sure player only queues one alchemy cast
 */
val alchemyActionStackPrevention = TimerKey()

val lowAlchemyDelay = 3
val highAlchemyDelay = 5

for (lowAlchemy in booleanArrayOf(true, false)) {
    on_spell_on_item(218, if (lowAlchemy) 18 else 39, 149, 0) {

        val item = player.getInteractingItem()
        val itemDef = item.getDef(world.definitions)
        val value = itemDef.cost

        if (!itemDef.tradeable) {
            player.message("You cannot use alchemy on untradeable items.")
            return@on_spell_on_item
        }

        if (value == 0) {
            player.message("This item has no value.")
            return@on_spell_on_item
        }

        if (item.id == Items.COINS_995) {
            player.message("Coins are already made of gold.")
            return@on_spell_on_item
        }

        if (!MagicSpells.canCast(
                player,
                if (lowAlchemy) 21 else 55,
                listOf(Item(Items.NATURE_RUNE), Item(Items.FIRE_RUNE, if (lowAlchemy) 3 else 5)),
                Spellbook.STANDARD.id
            )
        ) return@on_spell_on_item

        player.queue() {

            if (player.timers.has(alchemyDelay)) {

                if (player.timers.has(alchemyActionStackPrevention)) {
                    return@queue
                } else {
                    player.timers[alchemyActionStackPrevention] = player.timers[alchemyDelay]
                    wait(player.timers[alchemyDelay])

                    if (!MagicSpells.canCast(
                            player,
                            if (lowAlchemy) 21 else 55,
                            listOf(Item(Items.NATURE_RUNE), Item(Items.FIRE_RUNE, if (lowAlchemy) 3 else 5)),
                            Spellbook.STANDARD.id
                        ) || !player.inventory.contains(item.id)
                    ) return@queue

                }
            }

            player.timers[alchemyDelay] = 1 + if (lowAlchemy) lowAlchemyDelay else highAlchemyDelay

            MagicSpells.removeRunes(
                player,
                listOf(Item(Items.NATURE_RUNE), Item(Items.FIRE_RUNE, if (lowAlchemy) 3 else 5))
            )
            if (item.amount == 0) {
                player.inventory.remove(item)
            } else {
                player.inventory.remove(item.id, 1)
            }

            player.inventory.add(Items.COINS_995, floor(value * (if (lowAlchemy) 0.4 else 0.6)).toInt())
            player.addXp(Skills.MAGIC, if (lowAlchemy) 31.0 else 65.0)
            player.animate(if (lowAlchemy) 712 else 713)
            player.graphic(if (lowAlchemy) 112 else 113)
            player.write(SynthSoundMessage(if (lowAlchemy) 98 else 97, 1, 1))
            // TODO switch back to spellbook
        }
    }
}