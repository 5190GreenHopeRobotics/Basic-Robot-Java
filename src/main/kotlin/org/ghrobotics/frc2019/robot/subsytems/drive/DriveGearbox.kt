package org.ghrobotics.frc2019.robot.subsytems.drive

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.NeutralMode
import org.ghrobotics.frc2019.robot.Constants
import org.ghrobotics.lib.mathematics.units.amp
import org.ghrobotics.lib.mathematics.units.derivedunits.volt
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.wrappers.ctre.FalconSRX

class DriveGearbox(
    masterId: Int,
    slaveOneId: Int,
    slaveTwoId: Int,
    inverted: Boolean
) {
    val master = FalconSRX(masterId, Constants.kDriveNativeUnitModel)
    val slaveOne = FalconSRX(slaveOneId, Constants.kDriveNativeUnitModel)
    val slaveTwo = FalconSRX(slaveTwoId, Constants.kDriveNativeUnitModel)

    val allMotors = listOf(master, slaveOne, slaveTwo)

    init {
        slaveOne.follow(master)
        slaveTwo.follow(master)

        // Configure Inversion
        master.inverted = inverted
        slaveOne.inverted = inverted
        slaveTwo.inverted = !inverted

        // Configure Encoder
        master.feedbackSensor = FeedbackDevice.QuadEncoder
        master.encoderPhase = false
        master.sensorPosition = 0.meter

        allMotors.forEach { motor ->
            motor.peakForwardOutput = 1.0
            motor.peakReverseOutput = -1.0

            motor.nominalForwardOutput = 0.0
            motor.nominalReverseOutput = 0.0

            motor.brakeMode = NeutralMode.Brake

            motor.voltageCompensationSaturation = 12.volt
            motor.voltageCompensationEnabled = true

            motor.peakCurrentLimit = 0.amp
            motor.peakCurrentLimitDuration = 0.millisecond
            motor.continuousCurrentLimit = 40.amp // TODO Find Actual Value
            motor.currentLimitingEnabled = true
        }
    }
}