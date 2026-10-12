package com.theendercore.trowel

import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.AirItem
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext

class Trowel(properties: Properties) : Item(properties) {
    override fun useOn(c: UseOnContext): InteractionResult {
        if (c.level.isClientSide) return InteractionResult.SUCCESS

        val player = c.player ?: return InteractionResult.PASS
        val inv = player.inventory ?: return InteractionResult.PASS

        val placeable = (0..8).mapNotNull {
            val stack = inv.getItem(it)
            if (isPlaceable(stack) && stack != null) (it to stack)
            else null
        }

        if (placeable.isEmpty()) return InteractionResult.PASS
        return place(placeable, inv, player, c, player.inventory.selectedSlot)
    }


    private fun place(
        placeable: List<Pair<Int, ItemStack>>, inv: Inventory,
        player: Player, c: UseOnContext, originalSlot: Int,
    ): InteractionResult {
        if (placeable.isEmpty()) {
            inv.selectedSlot = originalSlot
            return InteractionResult.FAIL
        }
        placeable.random().let { (slot, stack) ->
            inv.selectedSlot = slot
            val placeCtx = newPlacementContext(player, stack, c)
            if (canPlace(placeCtx, stack.item as BlockItem)) {
                placeSound(placeCtx)
                inv.selectedSlot = originalSlot
                return InteractionResult.SUCCESS
            }
            return this.place(placeable.filter { it.first != slot }, inv, player, c, originalSlot)
        }
    }

    private fun placeSound(c: UseOnContext) {
        c.level.getBlockState(c.clickedPos).let {
            c.level.playSound(
                null, c.clickedPos, it.soundType.placeSound,
                SoundSource.BLOCKS,
                (it.soundType.volume + 1.0f) / 2.0f,
                it.soundType.pitch * 0.8f
            )
        }
    }

    private fun newPlacementContext(player: Player, stack: ItemStack, context: UseOnContext) =
        BlockPlaceContext(
            player, context.hand, stack,
            BlockHitResult(context.clickLocation, context.clickedFace, context.clickedPos, context.isInside)
        )

    private fun isPlaceable(stack: ItemStack): Boolean = stack.item !is AirItem && stack.item is BlockItem
    private fun canPlace(ctx: BlockPlaceContext, stack: BlockItem): Boolean {
        val state = stack.block.getStateForPlacement(stack.updatePlacementContext(ctx) ?: return false)
            ?: return false
        val shapeContext = ctx.player?.let(CollisionContext::of) ?: CollisionContext.empty()
        return ctx.canPlace()
                && state.canSurvive(ctx.level, ctx.clickedPos)
                && ctx.level.isUnobstructed(state, ctx.clickedPos, shapeContext)
                && stack.useOn(ctx).consumesAction()
    }
}
