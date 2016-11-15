/*
 * This class was created by <Katrix>. It's distributed as
 * part of the Journey To Gensokyo Mod. Get the Source Code in github:
 * https://github.com/Katrix-/JTG
 *
 * Journey To Gensokyo is Open Source and distributed under the
 * Journey To Gensokyo license: https://github.com/Katrix-/JTG/blob/DanmakuCore/LICENSE.md
 */

/*
 * This class was created by <Katrix>. It's distributed as
 * part of the Journey To Gensokyo Mod. Get the Source Code in github:
 * https://github.com/Katrix-/JTG
 *
 * Journey To Gensokyo is Open Source and distributed under the
 * a modifed Botania license: https://github.com/Katrix-/JTG/blob/master/LICENSE.md
 */
package net.katsstuff.journeytogensokyo.phase

import scala.annotation.switch
import scala.util.Random

import net.katsstuff.danmakucore.data.{MovementData, ShotData, Vector3}
import net.katsstuff.danmakucore.entity.danmaku.{DanmakuBuilder, DanmakuVariant}
import net.katsstuff.danmakucore.entity.living.phase.{Phase, PhaseManager, PhaseType}
import net.katsstuff.danmakucore.handler.ConfigHandler
import net.katsstuff.danmakucore.helper.DanmakuHelper
import net.katsstuff.danmakucore.impl.shape.{ShapeArrow, ShapeCircle, ShapeRandomRing, ShapeRing, ShapeStar, ShapeWideShot}
import net.katsstuff.danmakucore.registry.DanmakuRegistry
import net.katsstuff.journeytogensokyo.helper.Implicits._
import net.katsstuff.journeytogensokyo.lib.LibPhaseName
import net.katsstuff.journeytogensokyo.phase.PhaseStageEnemy._
import net.minecraft.nbt.NBTTagCompound

class PhaseStageEnemyType extends PhaseType {

	setRegistryName(LibPhaseName.StageEnemy)

	final   val ShapeAmount = 6
	private val rand        = new Random()

	override def instantiate(phaseManager: PhaseManager): Phase = instantiate(phaseManager, DanmakuRegistry.DANMAKU_VARIANT.getRandomObject(rand.self))
	def instantiate(phaseManager: PhaseManager, variant: DanmakuVariant): Phase = {

		val shot = variant.getShotData.copy(color = DanmakuHelper.randomSaturatedColor())
		val shape = rand.nextInt(ShapeAmount)
		val amount = rand.nextInt(ConfigHandler.getDanmakuMaxNumber)
		val width = rand.nextFloat * 90
		new PhaseStageEnemy(phaseManager, shot, variant.getMovementData, shape, amount, width, this)
	}
}

object PhaseStageEnemy {

	private final val NbtMovement = "speed"
	private final val NbtShape    = "shape"
	private final val NbtAmount   = "amount"
	private final val NbtWidth    = "width"
	private final val NbtShotData = "shotData"
	private final val Accuracy    = 0.7F
}

class PhaseStageEnemy(
		manager: PhaseManager,
		var shot: ShotData,
		var movement: MovementData,
		var shape: Int,
		var amount: Int,
		var width: Float,
		phaseType: PhaseStageEnemyType) extends Phase(manager) {

	override def init() {
		super.init()
		interval = 30
	}

	override def serverUpdate() {
		super.serverUpdate()
		if(counter == interval) {
			val entity = getEntity
			val angle = Vector3.angleLimitRandom(entity.getAngle, Accuracy)
			val distance = 0.1D
			val danmaku = DanmakuBuilder.builder.setUser(entity).setShot(shot).setMovementData(movement).build()
			DanmakuHelper.playShotSound(entity)

			val shapeObj = (shape: @switch) match {
				case 0 => new ShapeArrow(danmaku, amount, distance, width)
				case 1 => new ShapeCircle(danmaku, amount, 0F, distance)
				case 2 => new ShapeRandomRing(danmaku, amount, width, distance)
				case 3 => new ShapeRing(danmaku, amount, width, 0F, distance)
				case 4 => new ShapeStar(danmaku, amount, width, 0F, distance)
				case 5 => new ShapeWideShot(danmaku, amount, width, 0F, distance)
			}
			shapeObj.drawForTick(new Vector3(entity), angle, 0)
		}
	}

	def getInterval: Int = interval

	def setInterval(interval: Int): Unit = this.interval = interval

	override def serializeNBT: NBTTagCompound = {
		val tag = super.serializeNBT
		tag.setTag(NbtMovement, movement.serializeNBT)
		tag.setInteger(NbtShape, shape)
		tag.setInteger(NbtAmount, amount)
		tag.setFloat(NbtWidth, width)
		tag.setTag(NbtShotData, shot.serializeNBT)
		tag
	}

	override def deserializeNBT(tag: NBTTagCompound) {
		super.deserializeNBT(tag)
		shape = tag.getInteger(NbtShape)
		amount = tag.getInteger(NbtAmount)
		width = tag.getFloat(NbtWidth)
		shot = new ShotData(tag.getCompoundTag(NbtShotData))
		movement = MovementData.fromNBT(tag.getCompoundTag(NbtMovement))
	}
	override protected def getType: PhaseType = phaseType
}
