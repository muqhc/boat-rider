package io.github.muqhc.boatrider.plugin

import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import io.github.muqhc.boatrider.BoatRally
import io.github.muqhc.skygui.manager.SimpleSkyguiManager
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector

class BoatRiderPlugin: JavaPlugin() {
    var displayManager = SimpleSkyguiManager()
    var managingTask: BukkitTask? = null

    var boatRallyList: MutableList<BoatRally> = mutableListOf()
    var boatRallyUpdatingTask: BukkitTask? = null

    override fun onEnable() {
        configureKommand()

        managingTask = displayManager.manageStart(this)

        boatRallyUpdatingTask = object : BukkitRunnable() {
            override fun run() {
                boatRallyList.forEach { it.update() }
            }
        }.runTaskTimer(this,1L,1L)
    }

    fun configureKommand() =
        kommand {
            register("boat_rider") {
                val boatRallyArgument = dynamic { _, input -> input }.apply {
                    suggests {
                        suggest(boatRallyList.map { it.name })
                    }
                }

                then("add_rally", "rally_name" to string()) {
                    requires { isPlayer }
                    executes {
                        val rally_name: String by it
                        boatRallyList += BoatRally(rally_name,displayManager,world).apply {
                            startLocationVector = player.location.toVector()
                            startRangeVector = Vector(4.0,1.5,4.0)
                        }
                    }
                }

                then("remove_rally", "rally_name" to boatRallyArgument) {
                    executes {
                        val rally_name: String by it
                        val boatRally = boatRallyList.find { it.name == rally_name }!!
                        boatRally.clear()
                        boatRallyList.remove(boatRally)
                    }
                }

                then("add_boat", "rally_name" to boatRallyArgument, "boat_name" to string()) {
                    requires { isPlayer }
                    executes {
                        val rally_name: String by it
                        val boat_name: String by it
                        val boatRally = boatRallyList.find { it.name == rally_name }!!
                        boatRally.addBoat(player.location,boat_name)
                    }
                }
            }
        }
}