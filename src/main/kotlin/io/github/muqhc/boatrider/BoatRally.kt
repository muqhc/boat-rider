package io.github.muqhc.boatrider

import io.github.muqhc.skygui.manager.SkyguiManager
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Boat
import org.bukkit.entity.EntityType
import org.bukkit.util.Vector
import kotlin.math.abs

class BoatRally(val name: String, val displayManager: SkyguiManager, val world: World) {
    var boatList: MutableList<BoatRiderCar> = mutableListOf()

    var startRangeVector: Vector? = null
    var startLocationVector: Vector? = null

    var boostPower = 2.0
    var maxBoostCount = 2

    var deltaTime = 0.05

    fun update() {
        if (startRangeVector == null || startLocationVector == null) return

        world.spawnParticle(
            Particle.WAX_ON,
            startLocationVector!!.x,startLocationVector!!.y,startLocationVector!!.z,
            1
        )

        val removeBoatQueue: MutableList<BoatRiderCar> = mutableListOf()
        boatList.forEach {
            if (it.boat.isDead) {
                removeBoatQueue += it
                return@forEach
            }
            val isOutside = it.boat.location.clone().subtract(startLocationVector!!).run {
                startRangeVector!!.x <= abs(x) ||
                startRangeVector!!.y <= abs(y) ||
                startRangeVector!!.z <= abs(z)
            }
            if (isOutside) {
                if (!it.isStarted) {
                    it.time = 0.0
                    it.isStarted = true
                }
            } else {
                if (it.isStarted) {
                    it.isStarted = false
                }
            }
            it.update()
        }
        removeBoatQueue.forEach {
            removeBoatRiderCar(it)
        }

    }

    fun addBoat(location: Location, name: String) {
        val boat = (location.world.spawnEntity(location,EntityType.BOAT) as Boat).apply {
            this
        }
        val boatRiderCar = BoatRiderCar(
            name, boat, this
        )
        boatList.add(boatRiderCar)
        displayManager.displays.add(boatRiderCar.hud)
    }

    fun removeBoatRiderCar(boatRiderCar: BoatRiderCar) {
        boatRiderCar.hud.clear()
        displayManager.displays.remove(boatRiderCar.hud)
        boatRiderCar.boat.remove()
        boatList.remove(boatRiderCar)
    }

    fun removeBoatRiderCar(name: String) {
        boatList.find { it.name == name }?.let { removeBoatRiderCar(it) }
    }

    fun clear() {
        boatList.forEach { boatRiderCar ->
            boatRiderCar.hud.clear()
            displayManager.displays.remove(boatRiderCar.hud)
            boatRiderCar.boat.remove()
            boatList.remove(boatRiderCar)
        }
        boatList.clear()
    }
}