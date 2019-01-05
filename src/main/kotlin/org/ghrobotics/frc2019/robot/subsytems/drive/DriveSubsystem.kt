/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.frc2019.robot.subsytems.drive

import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod
import com.ctre.phoenix.sensors.PigeonIMU
import com.team254.lib.physics.DCMotorTransmission
import com.team254.lib.physics.DifferentialDrive
import edu.wpi.first.wpilibj.Solenoid
import org.ghrobotics.frc2019.robot.Constants
import org.ghrobotics.frc2019.robot.Robot
import org.ghrobotics.lib.localization.TankEncoderLocalization
import org.ghrobotics.lib.mathematics.twodim.control.RamseteTracker
import org.ghrobotics.lib.sensors.asSource
import org.ghrobotics.lib.subsystems.drive.TankDriveSubsystem
import kotlin.math.pow
import kotlin.properties.Delegates.observable

object DriveSubsystem : TankDriveSubsystem() {

    // Gearboxes
    private val leftGearbox = DriveGearbox(
        Constants.kLeftMasterId,
        Constants.kLeftSlaveId1,
        Constants.kLeftSlaveId2,
        false
    )
    private val rightGearbox = DriveGearbox(
        Constants.kRightMasterId,
        Constants.kRightSlaveId1,
        Constants.kRightSlaveId2,
        true
    )

    // Master motors
    override val leftMotor get() = leftGearbox.master
    override val rightMotor get() = rightGearbox.master

    private val allMasters get() = listOf(leftMotor, rightMotor)

    // Shifter for two-speed gearbox
    private val shifter = Solenoid(Constants.kPCMId, Constants.kDriveSolenoidId)

    private val dcTransmission = DCMotorTransmission(
        1 / Constants.kVDrive,
        Constants.kWheelRadius.value.pow(2) * Constants.kRobotMass / (2.0 * Constants.kADrive),
        Constants.kStaticFrictionVoltage
    )

    override val differentialDrive = DifferentialDrive(
        Constants.kRobotMass,
        Constants.kRobotMomentOfInertia,
        Constants.kRobotAngularDrag,
        Constants.kWheelRadius.value,
        Constants.kTrackWidth.value / 2.0,
        dcTransmission,
        dcTransmission
    )

    override val trajectoryTracker = RamseteTracker(Constants.kDriveBeta, Constants.kDriveZeta)


    // Type of localization to determine position on the field
    override val localization = TankEncoderLocalization(
        PigeonIMU(Constants.kPigeonIMUId).asSource(), { leftMotor.sensorPosition }, { rightMotor.sensorPosition },
        Robot.coroutineContext
    )

    // Shift up and down
    var lowGear by observable(false) { _, _, wantLow ->
        if (wantLow) {
            shifter.set(true)
        } else {
            shifter.set(false)
        }
    }

    init {
        lowGear = false
        defaultCommand = ManualDriveCommand()
        allMasters.forEach { it.configVelocityMeasurementPeriod(VelocityMeasPeriod.Period_20Ms) }
    }
}
