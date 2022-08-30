package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name="auto", group="Training")
public class auto extends LinearOpMode {
    autofunctionlibrary robot = new autofunctionlibrary();

    @Override
    public void runOpMode() {
        robot.initialization();

        waitForStart();
        while(opModeIsActive()){
            robot.encoderMovement(10, 1, 0.5);

            robot.encoderMovement(10, 4, 0.5);

            robot.encoderMovement(10, 2, 0.5);

            robot.encoderMovement(10, 3, 0.5);

            robot.turn(35); //left

            robot.turn(-35); //right
        break;
        }


    }
}
