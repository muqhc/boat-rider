package io.github.muqhc.boatrider

import io.github.muqhc.boatrider.util.format
import org.bukkit.entity.Boat
import org.bukkit.util.Vector
import kotlin.math.*

open class BoatRiderCar(val name: String, val boat: Boat, var rally: BoatRally) {

    var driftMeterRatio: Double = 0.0
        protected set

    var time = 0.0

    var isStarted = false

    var hud = BoatHUD(this)

    var velocity: Vector = Vector(0,0,0)
        protected set

    protected var lastLocVector = boat.location.toVector()


    fun update() {
        if (!isStarted) return

        time += rally.deltaTime

        velocity = boat.location.toVector().subtract(lastLocVector)
        lastLocVector = boat.location.toVector().multiply((1/rally.deltaTime)/20)

        if (velocity.length() != 0.0) {
            driftMeterRatio = ((1.0 - (abs(velocity.angle(boat.location.direction.clone().setY(0.0)) - (3.14 / 2)) / (3.14 / 2))) *
                velocity.clone().setY(0).length())*2.5
        }

        if (driftMeterRatio.isNaN()) {
            driftMeterRatio = 0.0
        }
    }


}