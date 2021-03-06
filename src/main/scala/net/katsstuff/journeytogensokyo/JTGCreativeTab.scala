/*
 * This class was created by <Katrix>. It's distributed as
 * part of the Journey To Gensokyo Mod. Get the Source Code in github:
 * https://github.com/Katrix-/JTG
 *
 * Journey To Gensokyo is Open Source and distributed under the
 * a modifed Botania license: https://github.com/Katrix-/JTG/blob/devDanmakuCore/LICENSE.md
 */
package net.katsstuff.journeytogensokyo

import net.katsstuff.journeytogensokyo.block.JTGBlocks
import net.katsstuff.journeytogensokyo.lib.LibMod
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack

object JTGCreativeTab extends CreativeTabs(LibMod.Id) {

  setNoTitle()
  setBackgroundImageName("item_search.png")

  override def createIcon: ItemStack = new ItemStack(JTGBlocks.DanmakuCrafting)
}
