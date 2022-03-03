package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.I2C;
import com.revrobotics.CANSparkMax.ControlType;
import edu.wpi.first.wpilibj.Timer;

public class Intake {

    CANSparkMax intakeMotor = new CANSparkMax(Constants.intakeMotorIndex, MotorType.kBrushless);
    CANSparkMax indexerMotor = new CANSparkMax(Constants.indexerMotorIndex, MotorType.kBrushless);
    CANSparkMax intakeAngleMotor = new CANSparkMax(Constants.intakeAngleMotorIndex, MotorType.kBrushless);

    I2C.Port i2cPort = I2C.Port.kOnboard;
    ColorSensorV3 indexerColorSensor = new ColorSensorV3(i2cPort);
    ColorMatch colorMatch = new ColorMatch();

    Color redBall = new Color(0.423, 0.183, 0.394);
    Color blueBall = new Color(0.195, 0.374, 0.432);
    Color empty = new Color(0.251, 0.273, 0.476);
    static boolean intaking = false;
    static boolean isIntakeDown = false;
    static boolean movingIntake = false;

    Timer intakeAngleTimer = new Timer();

    public void intakeInit() {
        intaking = false;
        isIntakeDown = false;
        movingIntake = false;

        intakeMotor.enableVoltageCompensation(12.5); // same speed every time
        indexerMotor.enableVoltageCompensation(12.5);

        colorMatch.addColorMatch(redBall);
        colorMatch.addColorMatch(blueBall);
        colorMatch.addColorMatch(empty);
        // add third colorMatch?
        intakeAngleTimer.reset();
        intakeAngleTimer.start();
    }

    public void intakeTeleop() {
        Color detectedColor = indexerColorSensor.getColor();
        ColorMatchResult match = colorMatch.matchClosestColor(detectedColor);
        

        // if (Constants.stick.getRawButton(10)) {
        //     intakeMotor.set(0.6);
        //     intaking = false;
        // } else if (Constants.stick.getRawButtonPressed(1)) {
        //     if (isIntakeDown) {
        //         intaking = !intaking;
        //         if (intaking && isIntakeDown) {
        //             intakeMotor.set(-0.6);
        //         }
        //     }
        // } else {
        //     if (!intaking) {
        //         intakeMotor.set(0.0);
        //     }
        // }

        if(isIntakeDown){
            if(Constants.stick.getRawButton(10)){
                intakeMotor.set(0.6);
                intaking = false;
            } else if(Constants.stick.getRawButtonPressed(1)){
                intaking = !intaking;
                if(intaking){
                    intakeMotor.set(-0.6);
                }
            } else {
                if(!intaking){
                    intakeMotor.set(0.0);
                }
            }
        } else {
            intakeMotor.set(0.0);
        }

        if (!Robot.shooter.shooting && !Robot.shooter.dumpShot) {
            if (Constants.xbox.getPOV() == 0) {
                indexerMotor.set(-0.4);
            } else if (Constants.xbox.getPOV() == 180) {
                indexerMotor.set(0.4);
            } else {
                if (((match.color == redBall) || (match.color == blueBall))) {
                    indexerMotor.set(0.0);
                } else {
                    indexerMotor.set(-0.2);
                }
            }
        }

        if ((Constants.xbox.getRawButtonPressed(2)) && (intakeAngleTimer.get() > 1.25)) { // B Button?
            movingIntake = true;
            intakeAngleTimer.reset();
            intakeAngleTimer.start();
        }

        if (movingIntake && !isIntakeDown) {
            intakeAngleMotor.set(-0.5);
            if (intakeAngleTimer.get() > 1.15) {
                intakeAngleMotor.set(0.0);
                intakeAngleMotor.getPIDController().setReference(intakeAngleMotor.getEncoder().getPosition(),
                        ControlType.kPosition);
                movingIntake = false;
                isIntakeDown = true;
            }
        }

        if (movingIntake && isIntakeDown) {
            intakeAngleMotor.set(0.5);
            if (intakeAngleTimer.get() > 1.25) {
                intakeAngleMotor.set(0.0);
                intakeAngleMotor.getPIDController().setReference(intakeAngleMotor.getEncoder().getPosition(),
                        ControlType.kPosition);
                movingIntake = false;
                isIntakeDown = false;
            }
        }
    }
}

// if (Constants.stick.getRawButtonPressed(1)) {
// intaking = !intaking;
// }

// if(Constants.stick.getRawButton(10)){
// intakeMotor.set(intakeSpeed);
// } else {
// if (intaking) {
// intakeMotor.set(-intakeSpeed);
// } else {
// intakeMotor.set(0.0);
// }
// }

// if (Constants.stick.getRawButton(11)) { // needs some work
// intakeAngleMotor.set(-0.35);
// } else if (Constants.stick.getRawButton(12)) {
// intakeAngleMotor.set(0.35);
// } else {
// intakeAngleMotor.set(0.0);
// intakeAngleMotor.getPIDController().setReference(intakeAngleMotor.getEncoder().getPosition(),
// ControlType.kPosition);
// }