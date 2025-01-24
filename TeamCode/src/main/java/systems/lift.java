package systems;

import static systems.lift.liftStatus.DOWN;
import static systems.lift.liftStatus.INITIALIZE;
import static systems.lift.liftStatus.SAMPLE_LOW;
import static systems.lift.liftStatus.SPECIMEN_HIGH;
import static systems.lift.liftStatus.SPECIMEN_LOW;
import static systems.lift.liftStatus.TRANSFER;

import com.acmerobotics.dashboard.config.Config;

import globals.PID;
import globals.robotMap;

@Config
public class lift {

    public enum liftStatus
    {
        INITIALIZE,
        DOWN,
        TRANSFER,
        SAMPLE_HIGH,
        SAMPLE_LOW,
        SPECIMEN_HIGH,
        SPECIMEN_LOW,
    }

    public double CurrentSpeed = 0;

    //    public static double PDRIVE = 0.0005;
//    public static double IDRIVE = 0;
//    public static double DDRIVE = 0;
    public static double PDRIVE = 0.0002;
    public static double IDRIVE = 0;
    public static double DDRIVE = 0.002;

    public static double PDOWN = 0.0006;
    public static double IDOWN = 0;
    public static double DDOWN = 0;

    public double maxSpeedUp = 1;

    public static liftStatus CS = INITIALIZE, PS = INITIALIZE;

    PID LiftPIDDOWN = null;
    PID LiftPID_DRIVE = null;
    public PID activePID;

    public static double base = -50;
    public static double transfer = -1000;
    public static double specimen_high = 20000;
    public static double specimen_low = 0;
    public static double sample_high = 48000;
    public static double sample_low = 20500;
    public int CurrentPosition = 0;

    public lift()
    {
        LiftPIDDOWN = new PID(PDOWN,IDOWN,DDOWN);
        LiftPIDDOWN.targetValue = base;
        LiftPIDDOWN.maxOutput = maxSpeedUp;

        LiftPID_DRIVE = new PID(PDRIVE,IDRIVE,DDRIVE);
        LiftPID_DRIVE.targetValue = base;
        LiftPID_DRIVE.maxOutput = maxSpeedUp;
    }


    public void disableMotor(robotMap r) {
        r.lift.setMotorDisable();
    }

    public void enableMotor(robotMap r)
    {
        r.lift.setMotorEnable();
    }

    public void update(robotMap r, int position, double voltage)
    {
        switch (CS) {
            case DOWN:
                activePID = LiftPIDDOWN;
                break;
            case SAMPLE_HIGH:
                activePID = LiftPID_DRIVE;
                break;
            case SAMPLE_LOW:
                activePID = LiftPID_DRIVE;
                break;
            case SPECIMEN_HIGH:
                activePID = LiftPID_DRIVE;
                break;
            case SPECIMEN_LOW:
                activePID = LiftPID_DRIVE;
                break;
            default:
                activePID = LiftPIDDOWN;
                break;
        }

        CurrentPosition = position;
        double powerLift = Math.min(activePID.update(position), 1);
        powerLift =  Math.max(-1,Math.min(powerLift,1));
        CurrentSpeed=powerLift;

        if(CS == DOWN && r.collect.getCurrentPosition() > 300) r.lift.setPower(-0.9);
        else if (activePID.targetValue <= 0 && r.collect.getCurrentPosition() <=  300 && ( CS == DOWN || CS == TRANSFER)) {
            if (CS == TRANSFER) r.lift.setPower(-0.4);
            else  r.lift.setPower(0);
        } else
        if((activePID.targetValue > 0 || r.collect.getCurrentPosition() > 300) && CS != DOWN)
        {
            r.lift.setPower(powerLift);
        }

        if(CS != PS || CS == SAMPLE_LOW || CS == liftStatus.SAMPLE_HIGH || CS == SPECIMEN_HIGH || CS == SPECIMEN_LOW || CS == TRANSFER || CS == DOWN || CS == INITIALIZE)
        {
            switch (CS)
            {
                case INITIALIZE:
                {
                    activePID.targetValue = base;
                    break;
                }

                case SPECIMEN_HIGH:
                {
                    activePID.targetValue = specimen_high;
                    break;
                }

                case DOWN:
                {
                    activePID.targetValue = base;
                    break;
                }

                case SPECIMEN_LOW:
                {
                    activePID.targetValue = specimen_low;
                    break;
                }

                case SAMPLE_HIGH:
                {
                    activePID.targetValue = sample_high;
                    break;
                }

                case SAMPLE_LOW:
                {
                    activePID.targetValue = sample_low;
                    break;
                }

                case TRANSFER:
                {
                    activePID.targetValue = transfer;
                    break;
                }
            }
        }

        PS = CS;
    }

}