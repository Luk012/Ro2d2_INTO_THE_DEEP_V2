package systems;

import com.acmerobotics.dashboard.config.Config;

import java.io.BufferedReader;

import globals.PID;
import globals.robotMap;

@Config
public class extendo {
    public enum extendoStatus {
        INITIALIZE,
        RETRACTED,
        EXTENDED,
        SHORT,
        TRANSFER,

    }

    // PID constants for extension
    public static double Kp_extend = 0.011;
    public static double Ki_extend = 0;
    public static double Kd_extend = 0;

    public static double Kp_retract = 0.01;
    public static double Ki_retract = 0;
    public static double Kd_retract = 0;

    PID extendoPIDExtend;
    PID extendoPIDRetract;

    public static double maxSpeed = 1;

    public extendoStatus CS = extendoStatus.INITIALIZE, PS = extendoStatus.INITIALIZE;

    public static double retracted = 0;
    public static double extended = 420;

    public static double drive = 800;
    public static double transfer = -5;
    public static double short_pose = 200;

    public PID activePID;

    public extendo() {
        extendoPIDExtend = new PID(Kp_extend, Ki_extend, Kd_extend);
        extendoPIDRetract = new PID(Kp_retract, Ki_retract, Kd_retract);

        extendoPIDExtend.targetValue = retracted;
        extendoPIDExtend.maxOutput = maxSpeed;

        extendoPIDRetract.targetValue = retracted;
        extendoPIDRetract.maxOutput = maxSpeed;
    }

    public void update(robotMap r, int position, double powerCap, double voltage) {
        switch (CS) {
            case INITIALIZE:
                activePID = extendoPIDRetract;
                break;
            case EXTENDED:
                activePID = extendoPIDExtend;
                break;
            case RETRACTED:
                activePID = extendoPIDRetract;
                break;
            case SHORT:
                activePID = extendoPIDExtend;
                break;
            case TRANSFER:
                activePID = extendoPIDRetract;
                break;
            default:
                activePID = extendoPIDRetract;
                break;
        }

        double powerColectare = activePID.update(position);
        powerColectare = Math.max(-1, Math.min(powerColectare, 1));

        if (activePID.targetValue <= 0 && position <= 1 && (CS == extendoStatus.RETRACTED || CS == extendoStatus.TRANSFER)) {
            if (CS == extendoStatus.RETRACTED) r.extendo.setPower(0);
            else if (CS == extendoStatus.TRANSFER) r.extendo.setPower(-0.4);
        } else if (activePID.targetValue > 0 || position > 1) {
            r.extendo.setPower(powerColectare);
        } else {
            r.extendo.setPower(0);
        }


        if (CS != PS || CS == extendoStatus.EXTENDED || CS == extendoStatus.RETRACTED || CS == extendoStatus.SHORT || CS == extendoStatus.TRANSFER || CS == extendoStatus.INITIALIZE) {
            switch (CS) {
                case INITIALIZE: {
                    activePID.targetValue = retracted;
                    activePID.maxOutput = 1;
                    break;
                }

                case EXTENDED: {
                    activePID.targetValue = extended;
                    activePID.maxOutput = 1;
                    break;
                }

                case RETRACTED: {
                    activePID.targetValue = retracted;
                    activePID.maxOutput = 1;
                    break;
                }

                case SHORT: {
                    activePID.targetValue = short_pose;
                    activePID.maxOutput = 1;
                    break;
                }

                case TRANSFER: {
                    activePID.targetValue = transfer;
                    activePID.maxOutput = 1;
                    break;
                }
            }
        }
        PS = CS;
    }

}
