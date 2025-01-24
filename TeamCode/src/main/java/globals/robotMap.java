package globals;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class robotMap {

    /**
     * Chassis
     */

    public DcMotorEx leftFront = null;
    public DcMotorEx leftBack = null;
    public DcMotorEx rightFront = null;
    public DcMotorEx rightBack = null;

    /**
     * Intake
     */

    public DcMotorEx collect = null;
    public DcMotorEx extendo = null;
    public Servo collectAngle = null;

    /**
     * Outtake
     */

    public DcMotorEx lift = null;

    public Servo mgn = null;
    public Servo fourbarLeft = null;
    public Servo fourbarRight = null;
    public Servo claw = null;

    /**
     * Sensors
     */

    public ColorSensor color_sensor = null;
    public DigitalChannel claw_sensor = null;

    public DistanceSensor distance_sensor = null;

    /**
     * ENDGAME
     */
    public Servo pto = null;

    /**
     * INIT
     */
    public robotMap(HardwareMap Init) {
        /**
         * Chassis
         */

        rightFront = Init.get(DcMotorEx.class, "rightFront");
        leftFront = Init.get(DcMotorEx.class, "leftFront");
        rightBack = Init.get(DcMotorEx.class, "rightBack");
        leftBack = Init.get(DcMotorEx.class, "leftBack");

        /**
         * Intake
         */

        collect = Init.get(DcMotorEx.class, "collect");
        extendo = Init.get(DcMotorEx.class, "extendo");
        collectAngle = Init.get(Servo.class, "collectAngle");

        /**
         * Outtake
         */

        lift = Init.get(DcMotorEx.class, "outtake");
        mgn = Init.get(Servo.class,"mgn");
        fourbarLeft = Init.get(Servo.class, "fourbarLeft");
        fourbarRight = Init.get(Servo.class, "fourbarRight");
        claw = Init.get(Servo.class, "claw");

        /**
         * Sensors
         */

        color_sensor = Init.get(ColorSensor.class,"color_sensor");
        claw_sensor = Init.get(DigitalChannel.class,"claw_sensor");
        distance_sensor = Init.get(DistanceSensor.class, "distance_sensor");

        /**
         * ENDGAME
         */

        pto = Init.get(Servo.class, "pto");

        /**
         * PROPERTIES
         */

        leftFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        leftBack.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        leftFront.setDirection(DcMotorEx.Direction.REVERSE);
        leftBack.setDirection(DcMotorEx.Direction.REVERSE);
        rightFront.setDirection(DcMotorEx.Direction.FORWARD);
        rightBack.setDirection(DcMotorEx.Direction.FORWARD);

        leftBack.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        leftFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        extendo.setDirection(DcMotorSimple.Direction.FORWARD);
        lift.setDirection(DcMotorSimple.Direction.FORWARD);

        extendo.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fourbarRight.setDirection(Servo.Direction.FORWARD);
        fourbarLeft.setDirection(Servo.Direction.FORWARD);
    }


}
