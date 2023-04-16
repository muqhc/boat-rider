package io.github.muqhc.boatrider

import io.github.muqhc.boatrider.util.format
import io.github.muqhc.skygui.util.Point
import io.github.muqhc.skyguifx.SkyFXSimpleDisplay
import io.github.muqhc.skyguifx.component.SkyFXComponent
import io.github.muqhc.skyguifx.component.SkyPanel
import io.github.muqhc.skyguifx.dsl.*
import io.github.muqhc.skyguifx.layout.SkyPaddingBoxLayout
import io.github.muqhc.skyguifx.util.Alignment
import io.github.muqhc.skyguifx.util.IntPoint
import net.kyori.adventure.text.Component
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot

class BoatHUD(val boatRiderCar: BoatRiderCar): SkyFXSimpleDisplay(
    boatRiderCar.boat.location, boatRiderCar.boat.location.direction
) {

    companion object {
        const val boostChargeTextMap = "▁▂▃▄▅▆▇█"
        fun getBoostChargeText(driftMeter: Double): String {
            return boostChargeTextMap.getOrNull((driftMeter*8).toInt())?.toString() ?: "█"
        }
    }

    var updater: MutableList<()->Unit> = mutableListOf()

    init {
        add(app())
    }

    fun app() =
        SkyPanel(SkyPaddingBoxLayout()).skyguiBuild(this) {
            additionalFloatingLevel = 0.0

            compo.localPoint1 = Point(-1.0,0.2)
            compo.localPoint2 = Point(1.0,1.0)


            board(Material.GLASS_PANE.createBlockData())

            simpleGridField(2,2) {
                additionalFloatingLevel = 0.0

                aligningBox {
                    option.gridPoint1 = IntPoint(0,0)
                    option.gridPoint2 = IntPoint(1,2)

                    val driftMeterLabel = label(
                        Component.text(getBoostChargeText(0.0)).color { 0xF68002 }
                    ) {
                        compo.textDisplay.backgroundColor = Color.fromARGB(0)
                    }

                    updater += {
                        driftMeterLabel.textDisplay.text(
                            Component.text(getBoostChargeText(boatRiderCar.driftMeterRatio)).color { 0xF68002 }
                        )
                    }
                }
                aligningBox {
                    option.gridPoint1 = IntPoint(1,0)
                    option.gridPoint2 = IntPoint(2,2)

                    val timeMeterLabel = label(
                        Component.text("0 s").color { 0x00FF00 }
                    ) {
                        compo.textDisplay.backgroundColor = Color.fromARGB(0)
                    }

                    updater += {
                        timeMeterLabel.textDisplay.text(
                            Component.text("${boatRiderCar.time.format(2)} s").color { 0x00FF00 }
                        )
                    }
                }
                aligningBox {
                    additionalFloatingLevel = 0.0

                    option.gridPoint1 = IntPoint(0,1)
                    option.gridPoint2 = IntPoint(2,2)

                    val speedMeterLabel = label(
                        Component.text("0 m/5s").color { 0x008080 }
                    ) {
                        option.alignment = Alignment.BottomCenter
                        compo.textDisplay.backgroundColor = Color.fromARGB(0)
                    }

                    updater += {
                        speedMeterLabel.textDisplay.text(
                            Component.text("${(boatRiderCar.velocity.length()*100).format(2)} m/5s").color { 0x008080 }
                        )
                    }
                }
            }
        }

    fun updateLocation() {
        normalVector.copy(boatRiderCar.boat.location.direction.clone().setY(0).normalize().multiply(-1))
        location.set(boatRiderCar.boat.location.x, boatRiderCar.boat.location.y+1.78, boatRiderCar.boat.location.z)
        location.subtract(normalVector.clone().multiply(0.65))
    }

    override fun render() {
        super.render()
        updateLocation()
        updater.forEach { it() }
    }
}