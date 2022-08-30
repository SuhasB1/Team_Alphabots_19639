package org.firstinspires.ftc.teamcode.autonomous;


import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@Disabled
@Autonomous(name="basicauto", group="Training")
public class autofunctionlibrary extends LinearOpMode {

    DcMotor leftWheel;
    DcMotor rightWheel;
    DcMotor backLeftWheel;
    DcMotor backRightWheel;
    BNO055IMU imu;
    boolean isStopRequested = false;
    double drivePower = 0.5;
    private Orientation lastAngles = new Orientation();
    private double currAngle = 0.0;
    //1 rotation = 360
    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {

    }

    public void initialization() {
        leftWheel = hardwareMap.dcMotor.get("front_left");
        rightWheel = hardwareMap.dcMotor.get("front_right");
        backRightWheel = hardwareMap.dcMotor.get("back_right");
        backLeftWheel = hardwareMap.dcMotor.get("back_left");

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
        imu.initialize(parameters);

        rightWheel.setDirection(DcMotor.Direction.REVERSE);
        backRightWheel.setDirection(DcMotor.Direction.REVERSE);

        setZeroPowerBehaiv();
        setAllMotorPowers(0);
    }

    public void encoderMovement(double distance, int direction, double power) {

        resetEncoders();
        setRUE();

        final double ENCODER_TPR = 537.6;
        final double GEAR_RATIO = 1;
        final double WHEEL_DIAMETER = 9.6;
        final double CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;
        final double ROTATIONS = distance / CIRCUMFERENCE;
        double ticks = ENCODER_TPR * ROTATIONS * GEAR_RATIO;

        switch (direction) {
            case 1: // robot will move forward
                setTargetPositionCounts(ticks, ticks, ticks, ticks);
                setAllMotorPowers(power);
                break;
            case 2: // robot will move backward
                setTargetPositionCounts(-ticks, -ticks, -ticks, -ticks);
                setAllMotorPowers(power);

                break;
            case 3: // robot will strafe left
                setTargetPositionCounts(-ticks, ticks, ticks, -ticks);
                setAllMotorPowers(power);
                break;
            case 4: // robot will strafe right
                setTargetPositionCounts(ticks, -ticks, -ticks, ticks);
                setAllMotorPowers(power);
                break;
            case 5: // robot will rotate left
                setTargetPositionCounts(-ticks, ticks, -ticks, ticks);
                setAllMotorPowers(power);
                break;
            case 6: // robot will rotate right
                setTargetPositionCounts(ticks, -ticks, ticks, -ticks);
                setAllMotorPowers(power);
                break;
        }

        setRTP();

        while (leftWheel.isBusy() && rightWheel.isBusy() && backLeftWheel.isBusy() && backRightWheel.isBusy()) {

        }
        resetEncoders();
    }

    public double getAngle() {
        Orientation orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double changeInAngle = orientation.firstAngle - lastAngles.firstAngle; //change in angle from previous angle to current angle

        if (changeInAngle > 180) {
            changeInAngle -= 360;
        } else if (changeInAngle <= -180) {
            changeInAngle += 360;
        }
        //these if statements accommodate for the IMU only going until 180 degrees.

        currAngle += changeInAngle;
        lastAngles = orientation;

        telemetry.addData("gyro", orientation.firstAngle);
        telemetry.update();
        return currAngle;
    }

    public void resetAngle() { //resetting the angles (after we finish turn)
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        currAngle = 0;
    }

    public void turn(double degrees) {
        setRWE();
        resetAngle();
        double error = degrees;

        while (opModeIsActive() && Math.abs(error) > 2) {
            double motorPower = (error < 0 ? -0.3 : 0.3);
            setMotorPowers(-motorPower, motorPower, -motorPower, motorPower);
            error = degrees - getAngle();

            telemetry.addData("error", error);
            telemetry.addData("angle", currAngle);
            telemetry.addData("1 imu heading", lastAngles.firstAngle);
            telemetry.addData("2 global heading", currAngle);
            telemetry.addData("3 correction", error);
            telemetry.update();


        }
        setAllMotorPowers(0);
    }

    public void setAllMotorPowers(double power) {
        leftWheel.setPower(power);
        rightWheel.setPower(power);
        backLeftWheel.setPower(power);
        backRightWheel.setPower(power);
    }

    public void setMotorPowers(double lw, double rw, double bl, double br) {
        leftWheel.setPower(lw);
        rightWheel.setPower(rw);
        backLeftWheel.setPower(bl);
        backRightWheel.setPower(br);
    }

    public void setTargetPositionCounts(double fl_count, double fr_count, double bl_count, double br_count) {
        leftWheel.setTargetPosition((int) fl_count);
        rightWheel.setTargetPosition((int) fr_count);
        backLeftWheel.setTargetPosition((int) bl_count);
        backRightWheel.setTargetPosition((int) br_count);
    }

    public void resetEncoders() {
        leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    public void setRTP() {
        leftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void setRUE() {
        leftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setZeroPowerBehaiv() {
        leftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void setRWE() {
        leftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeftWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}
