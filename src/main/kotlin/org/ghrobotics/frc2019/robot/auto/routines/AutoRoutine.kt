package org.ghrobotics.frc2019.robot.auto.routines

import org.ghrobotics.lib.commands.BasicCommandGroupBuilder
import org.ghrobotics.lib.commands.InstantRunnableCommand
import org.ghrobotics.lib.commands.sequential
import org.ghrobotics.frc2019.robot.Network
import org.ghrobotics.frc2019.robot.subsytems.drive.DriveSubsystem

fun autoRoutine(block: BasicCommandGroupBuilder.() -> Unit) = sequential {
    +InstantRunnableCommand {
        println("[AutoRoutine] Starting routine...")
        DriveSubsystem.localization
            .reset(Network.startingPositionChooser.selected.pose)
    }
    block()
}