package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="TeleOpTest", group="Training")
public class basicteleop extends OpMode {



    DcMotor leftWheel;
    DcMotor rightWheel;
    DcMotor backLeftWheel;
    DcMotor backRightWheel;



    private ElapsedTime runtime= new ElapsedTime();

    @Override
    public void init() {
        leftWheel = hardwareMap.dcMotor.get("front_left");
        rightWheel = hardwareMap.dcMotor.get("front_right");
        backRightWheel = hardwareMap.dcMotor.get("back_right");
        backLeftWheel = hardwareMap.dcMotor.get("back_left");

        rightWheel.setDirection(DcMotorSimple.Direction.REVERSE); //rightWheel
        backRightWheel.setDirection(DcMotorSimple.Direction.REVERSE); //backRightWheel

        leftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightWheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);  //setting it so when power is 0, robot stops.

    }

    @Override
    public void loop() {
        moveDriveTrain();

    }



    public void moveDriveTrain() {
        double driveForward = 0; //Moves forwards and backwards
        double driveBackward = 0;
        double strafe = 0; //Move side-to-side
        double rotate = 0;
        double drive = 0;
        double denominator = 1;

        drive = gamepad1.left_stick_y;
        strafe = gamepad1.left_stick_x * 1.1;
        rotate = gamepad1.right_stick_x;

        driveForward = gamepad1.right_trigger;
        driveBackward = gamepad1.left_trigger;

        denominator = Math.max(Math.abs(drive) + Math.abs(strafe) + Math.abs(rotate), 1);
        if(driveForward > 0) {
            rightWheel.setPower(-driveForward);
            backRightWheel.setPower(-driveForward);
            leftWheel.setPower(-driveForward);
            backLeftWheel.setPower(-driveForward);
        } else if(driveBackward > 0) {
            rightWheel.setPower(driveBackward);
            backRightWheel.setPower(driveBackward);
            leftWheel.setPower(driveBackward);
            backLeftWheel.setPower(driveBackward);

        } else {
            rightWheel.setPower((drive + strafe + rotate) / denominator);
            backRightWheel.setPower((drive - rotate + strafe) / denominator);
            leftWheel.setPower((drive - rotate - strafe) / denominator);
            backLeftWheel.setPower((drive + rotate - strafe) / denominator);
        }
    }
    public void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resetEncoders() {
        leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

}