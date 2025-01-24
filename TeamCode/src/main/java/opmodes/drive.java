package opmodes;

import android.annotation.SuppressLint;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.List;
import java.util.Objects;

import globals.globals;
import globals.robotMap;
import systems.claw;
import systems.collectAngle;
import systems.extendo;
import systems.fourbar;
import systems.intake;
import systems.lift;
import systems.mgn;
import systems.outtake;
import systems.pto;
import systems.transfer;

@TeleOp(name="drive", group="OpMode")
public class drive extends LinearOpMode
{
    public static double  PrecisionDenominatorTranslational = 1, PrecisionDenominatorAngle = 1;
    public boolean can_init = false;
    boolean StrafesOn = true;

    public static String Rec = "nothing";
    public static int red_threshold = 200;
    public static int yellow_threshold = 340;
    public static int blue_threshold = 340;
    public void robotCentricDrive(DcMotor leftFront, DcMotor leftBack, DcMotor rightFront, DcMotor rightBack, double  SpeedLimit, boolean StrafesOn , double LeftTrigger, double RightTrigger)
    {
        double y = -gamepad1.right_stick_y; // Remember, this is reversed!
        double x = gamepad1.right_stick_x;
        if (!StrafesOn)
        {
            x=0;
        }

        double rx = gamepad1.left_stick_x*1 - LeftTrigger + RightTrigger;

        rx*=PrecisionDenominatorAngle;
        x/=PrecisionDenominatorTranslational;
        y/=PrecisionDenominatorTranslational;

        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        frontLeftPower = Clip(frontLeftPower,SpeedLimit);
        backLeftPower = Clip(backLeftPower,SpeedLimit);
        frontRightPower = Clip(frontRightPower,SpeedLimit);
        backRightPower = Clip(backRightPower,SpeedLimit);

        if(frontRightPower > 0.1 || frontLeftPower > 0.1 || backLeftPower > 0.1 || backRightPower > 0.1) can_init = true;

//        if(pto_ON)
//        {
//            if(gamepad1.right_trigger > 0)
//            {
//                leftBack.setPower(-1);
//                rightBack.setPower(-1);
//            }
//            else
//            {
//                leftBack.setPower(backLeftPower);
//                rightBack.setPower(backRightPower);
//            }
//            leftFront.setPower(0);
//            rightFront.setPower(0);
//
//        } else
//        {
            leftFront.setPower(frontLeftPower);
            leftBack.setPower(backLeftPower);
            rightFront.setPower(frontRightPower);
            rightBack.setPower(backRightPower);
        //}


    }
    public String Retrun_Color(ColorSensor colorSensor)
    {
        int red_value = colorSensor.red();
        int  green_value = colorSensor.green();
        int blue_value = colorSensor.blue();

        if(red_value > green_value && red_value > blue_value && red_value > red_threshold)
        {
            return "red";

        }
        else if(green_value > blue_value && green_value > yellow_threshold)
        {
            return "yellow";
        }
        else if(blue_value > blue_threshold)
        {

            return "blue";
        }
        else {return  "nothing";}
    }

    public boolean Has_Sample(DistanceSensor distanceSensor)
    {
        if(distanceSensor.getDistance(DistanceUnit.CM) <= 0.8)
        {
            return true;
        } else return false;
    }

    double Clip(double Speed, double lim)
    {
        return Math.max(Math.min(Speed,lim), -lim);
    }




    @SuppressLint("SuspiciousIndentation")
    @Override
    public void runOpMode()
    {
        List<LynxModule> allHubs = hardwareMap.getAll(LynxModule.class);

        for (LynxModule hub : allHubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        robotMap r = new robotMap(hardwareMap);

        /**
         * SYSTEM CONTROLLERS
         */

        claw Claw = new claw();
        collectAngle CollectAngle = new collectAngle();
        extendo Extendo = new extendo();
        fourbar Fourbar = new fourbar();
        intake Intake = new intake();
        lift Lift = new lift();
        mgn Mgn = new mgn();
        outtake Outtake = new outtake();
        pto Pto = new pto();
        transfer Transfer = new transfer();


        /**
         * INITS
         */

        double voltage;
        double loopTime = 0;

        VoltageSensor batteryVoltageSensor = hardwareMap.voltageSensor.iterator().next();
        voltage = batteryVoltageSensor.getVoltage();

        /**
         * OTHERS INITS
         */

        int position_lift;
        int position_extendo;
        double collect_power = 0;

        String color_value = "nothing";

        Gamepad currentGamepad1 = new Gamepad();
        Gamepad currentGamepad2 = new Gamepad();

        Gamepad previousGamepad1 = new Gamepad();
        Gamepad previousGamepad2 = new Gamepad();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive() && !isStopRequested())
        {
            /**
             * INITS
             */

            for (LynxModule hub : allHubs) {
                hub.clearBulkCache();
            }



            MotorConfigurationType motorConfigurationType = r.leftBack.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            r.leftBack.setMotorType(motorConfigurationType);

            motorConfigurationType = r.rightBack.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            r.rightBack.setMotorType(motorConfigurationType);

            motorConfigurationType = r.rightFront.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            r.rightFront.setMotorType(motorConfigurationType);

            motorConfigurationType = r.leftFront.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            r.leftFront.setMotorType(motorConfigurationType);

            motorConfigurationType = r.extendo.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            r.extendo.setMotorType(motorConfigurationType);

            motorConfigurationType = r.lift.getMotorType().clone();
            motorConfigurationType.setAchieveableMaxRPMFraction(1.0);
            r.lift.setMotorType(motorConfigurationType);

            position_lift = r.collect.getCurrentPosition();
            position_extendo = r.extendo.getCurrentPosition();

            previousGamepad1.copy(currentGamepad1);
            previousGamepad2.copy(currentGamepad2);

            currentGamepad1.copy(gamepad1);
            currentGamepad2.copy(gamepad2);

            robotCentricDrive(r.leftFront, r.leftBack, r.rightFront, r.rightBack, 1 , StrafesOn , 0,0);

            /**
             * BUTON INIT
             */

            if(!previousGamepad1.dpad_up && currentGamepad1.dpad_up)
            {
                StrafesOn = !StrafesOn;
            }


            if(position_lift>3000 || position_extendo>40 || globals.collect_specimen)
            {
                PrecisionDenominatorAngle = 0.4;
            }
            else PrecisionDenominatorAngle = 1;


            if (!previousGamepad1.touchpad && currentGamepad1.touchpad)
            {
                if (globals.alliance == "red") globals.alliance = "blue";
                else globals.alliance = "red";
            }

            /**
             * @INTAKE
             */


                if(!globals.is_intransfer)
                {
                    collect_power = gamepad2.right_trigger - gamepad2.left_trigger;
                } else {
                    collect_power = 0;
                }

                    r.collect.setPower(collect_power);


                if(!globals.is_intransfer) {
                    if (!previousGamepad1.right_bumper && currentGamepad1.right_bumper) {
                        if (Intake.CS != intake.intakeStatus.LONG_DONE) {
                            Intake.CS = intake.intakeStatus.LONG;
                        } else Intake.CS = intake.intakeStatus.RETRACT;
                    }

                    if (!previousGamepad1.left_bumper && currentGamepad2.left_bumper) {
                        if (Intake.CS != intake.intakeStatus.SHORT_DONE) {
                            Intake.CS = intake.intakeStatus.SHORT;
                        } else Intake.CS = intake.intakeStatus.RETRACT;
                    }

                    if (!previousGamepad2.dpad_right && currentGamepad2.dpad_right) {
                        if (Intake.CSCA != intake.intakeStatus.COLLECT_DONE) {
                            Intake.CSCA = intake.intakeStatus.COLLECT;

                        } else {
                            Intake.CSCA = intake.intakeStatus.DRIVE;
                        }
                    }
                }

            /**
             * @REC
             */

            if(CollectAngle.CS == collectAngle.collectAngleStatus.COLLECT && collect_power > 0)
            {
                Rec = Retrun_Color(r.color_sensor);
                globals.has_sample = Has_Sample(r.distance_sensor);

                if((globals.alliance == "red" && Rec == "blue") || (globals.alliance == "blue" && Rec == "red"))
                {
                    globals.can_transfer = false;
                } else {
                    if(Rec == "yellow")
                    {
                        globals.half_transfer = false;
                    } else
                    {
                        globals.half_transfer = true;
                    }
                    globals.can_transfer = true;
                }
            }

            /**
             * @TRANSFER
             */

            if(globals.has_sample && globals.can_transfer && !globals.is_intransfer)
            {
                globals.has_sample = false;
                globals.can_transfer = false;
                Transfer.CS = transfer.transferStatus.TRANSFER_START;
                Intake.CS = intake.intakeStatus.RETRACT_DONE;
            }

            /**
             * @OUTTAKE
             */


            if(!previousGamepad2.triangle && currentGamepad2.triangle)
            {
                if(!globals.collect_specimen)
                {
                    globals.collect_specimen = true;
                    globals.is_indrive = false;
                    Outtake.CS = outtake.outtakeStatus.COLLECT_SPECIMEN_OPEN_CLAW;
                } else {
                    Outtake.CS = outtake.outtakeStatus.DRIVE_CLAW;
                    globals.collect_specimen = false;
                    globals.is_indrive = true;
                }
            }


            if(!previousGamepad2.cross && currentGamepad2.cross || !globals.is_intransfer)
            {
                if(globals.collect_specimen)
                {
                    globals.specimen_level = "high";
                    Outtake.CS = outtake.outtakeStatus.SCORE_SPECIMEN_LIFT;
                    globals.collect_specimen = false;
                    globals.is_indrive = false;
                    globals.score_specimen = true;
                } else if (globals.score_specimen && globals.specimen_level == "high")
                {
                    globals.score_specimen = false;
                    globals.is_indrive = true;
                    Outtake.CS = outtake.outtakeStatus.DRIVE_CLAW;
                } else if(globals.score_specimen && globals.specimen_level == "low")
                {
                    globals.specimen_level = "high";
                    Outtake.CS = outtake.outtakeStatus.SCORE_SPECIMEN_LIFT;
                    globals.collect_specimen = false;
                    globals.is_indrive = false;
                    globals.score_specimen = true;
                } else if (globals.is_indrive)
                {
                    if(Has_Sample(r.distance_sensor))
                    {
                        Transfer.CS = transfer.transferStatus.TRANSFER_MGN;
                        globals.is_intransfer = true;
                    } else if(!globals.is_intransfer)
                    {
                        globals.sample_level = "high";
                        Outtake.CS = outtake.outtakeStatus.SCORE_SAMPLE_LIFT;
                        globals.is_indrive = false;
                        globals.score_sample = true;
                    }
                } else if(globals.score_sample == true && globals.sample_level == "low")
                {
                    globals.sample_level = "high";
                    Outtake.CS = outtake.outtakeStatus.SCORE_SAMPLE_LIFT;
                    globals.is_indrive = false;
                    globals.score_sample = true;
                }
                else {
                    globals.is_indrive = true;
                    globals.score_sample = false;
                    globals.collect_specimen = false;
                    globals.score_specimen = false;
                    Outtake.CS = outtake.outtakeStatus.DRIVE_CLAW;
                }
            }

            if(!previousGamepad2.square && currentGamepad2.square && !globals.is_intransfer)
            {
                if(globals.collect_specimen)
                {
                    globals.specimen_level = "low";

                    Outtake.CS = outtake.outtakeStatus.SCORE_SPECIMEN_LIFT;
                    globals.collect_specimen = false;
                    globals.is_indrive = false;
                    globals.score_specimen = true;
                } else if (globals.score_specimen && globals.specimen_level == "low")
                {

                    Outtake.CS = outtake.outtakeStatus.DRIVE_CLAW;

                    globals.score_specimen = false;
                    globals.is_indrive = true;
                } else if(globals.score_specimen && globals.specimen_level == "high")
                {
                    globals.specimen_level = "low";

                    Outtake.CS = outtake.outtakeStatus.SCORE_SPECIMEN_LIFT;
                    globals.collect_specimen = false;
                    globals.is_indrive = false;
                    globals.score_specimen = true;
                }
                else if (globals.is_indrive)
                {
                    if(Has_Sample(r.distance_sensor))
                    {
                        globals.is_intransfer = true;
                        Transfer.CS = transfer.transferStatus.TRANSFER_MGN;
                    } else if(!globals.is_intransfer)
                    {
                        globals.sample_level = "low";

                        Outtake.CS = outtake.outtakeStatus.SCORE_SAMPLE_LIFT;

                        globals.is_indrive = false;
                        globals.score_sample = true;
                    }
                } else if(globals.score_sample == true && globals.sample_level == "high")
                {
                    globals.sample_level = "low";

                    Outtake.CS = outtake.outtakeStatus.SCORE_SAMPLE_LIFT;
                    globals.is_indrive = false;
                    globals.score_sample = true;
                }
                else {
                    Outtake.CS = outtake.outtakeStatus.DRIVE_CLAW;
                    globals.is_indrive = true;
                    globals.score_sample = false;
                    globals.collect_specimen = false;
                    globals.score_specimen = false;
                }
            }

            if(!previousGamepad2.right_bumper & currentGamepad2.right_bumper && !globals.is_intransfer)
            {
                if(Claw.CS == claw.clawStatus.OPENED)
                {
                    Claw.CS = claw.clawStatus.CLOSED;
                } else {
                    Claw.CS = claw.clawStatus.OPENED;
                }
            }







            /**
             * UPDATES
             */


            if(can_init) {
                Claw.update(r);
                CollectAngle.update(r);
                Extendo.update(r, position_extendo, 1, voltage);
                Fourbar.update(r);
                Intake.update(r,Extendo, CollectAngle);
                Lift.update(r, position_lift, voltage);
                Mgn.update(r);
                Outtake.update(r, Claw, Lift, Fourbar, Mgn);
                Pto.update(r);
                Transfer.update(r, Claw, Mgn, Extendo, Outtake, CollectAngle);

            }




            double loop = System.nanoTime();

            telemetry.addData("hz", 1000000000 / (loop - loopTime));
            loopTime = loop;
            telemetry.update();


        }
    }
}
