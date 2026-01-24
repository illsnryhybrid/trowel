package com.theendercore.trowel

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents.ModifyEntries
import net.minecraft.item.Item
import net.minecraft.item.ItemGroups
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("MemberVisibilityCanBePrivate")
object TrowelMod : ModInitializer {
    const val MODID = "trowel"
    val log: Logger = LoggerFactory.getLogger(MODID)

    private val TROWEL_ID: Identifier = Identifier.of(MODID, MODID)
    private val TROWEL_KEY: RegistryKey<Item> = RegistryKey.of(RegistryKeys.ITEM, TROWEL_ID)
    val TROWEL: Item = Trowel(Item.Settings().registryKey(TROWEL_KEY).maxCount(1))

    override fun onInitialize() {
        log.info("Seizing the means of block placement!")

        Registry.register(Registries.ITEM, TROWEL_KEY, TROWEL)

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS)
            .register(ModifyEntries { it.addAfter(Items.SHEARS, TROWEL) })
    }
}
