package systems;

import static systems.transfer.transferStatus.INITIALIZE;
import static systems.transfer.transferStatus.TRANSFER_CLAW;
import static systems.transfer.transferStatus.TRANSFER_DONE;
import static systems.transfer.transferStatus.TRANSFER_EXTENDO;
import static systems.transfer.transferStatus.TRANSFER_MGN;
import static systems.transfer.transferStatus.TRANSFER_MGN_RETRACT;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

import globals.globals;
import globals.robotMap;

@Config
public class transfer {

    public enum transferStatus {

        INITIALIZE,
        TRANSFER_START,
        TRANSFER_EXTENDO,
        TRANSFER_MGN,
        TRANSFER_CLAW,
        TRANSFER_MGN_RETRACT,
        TRANSFER_DONE,

    }

    public transfer() {
        CS = INITIALIZE;
        PS = INITIALIZE;
    }

    public static transferStatus CS = INITIALIZE, PS = INITIALIZE;
    public static double limit_mgn = 1;
    public static double limit_claw = 1;
    public static double limit_mgn_retract = 1;

    ElapsedTime timer_extendo = new ElapsedTime();
    ElapsedTime timer_mgn = new ElapsedTime();
    ElapsedTime timer_claw = new ElapsedTime();
    ElapsedTime timer_mgn_retract = new ElapsedTime();


    public void update(robotMap r, claw Claw, mgn Mgn, extendo Extendo, outtake Outtake, collectAngle CollectAngle) {
        if (CS != PS || CS == INITIALIZE) {
            switch (CS) {

                case TRANSFER_START:
                {
                    Outtake.CS = outtake.outtakeStatus.DRIVE_CLAW;
                    CollectAngle.CS = collectAngle.collectAngleStatus.DRIVE;
                    globals.is_intransfer = true;
                    r.collect.setPower(1);
                    timer_extendo.reset();
                    CS = TRANSFER_EXTENDO;
                    break;
                }

                case TRANSFER_EXTENDO:
                {
                    if(timer_extendo.seconds() > globals.limit_extento)
                    {
                        Extendo.CS = extendo.extendoStatus.TRANSFER;
                        timer_mgn.reset();
                        r.collect.setPower(0);
                        if(!globals.half_transfer)
                        {
                            CS = TRANSFER_MGN;
                        } else {
                            globals.is_intransfer = false;
                            globals.half_transfer = false;
                            CS = TRANSFER_DONE;
                        }
                    }
                    break;
                }

                case TRANSFER_MGN:
                {
                    if(timer_mgn.seconds() > limit_mgn || r.extendo.getCurrentPosition() < 5000)
                    {
                        Mgn.CS = mgn.mgnStatus.TRANSFER;
                        timer_claw.reset();
                        CS = TRANSFER_CLAW;
                    }
                    break;
                }

                case TRANSFER_CLAW:
                {
                    if(timer_claw.seconds() > limit_claw)
                    {
                        Claw.CS = claw.clawStatus.CLOSED;
                        timer_mgn_retract.reset();
                        CS = TRANSFER_MGN_RETRACT;
                    }
                    break;
                }

                case TRANSFER_MGN_RETRACT:
                {
                    if(timer_mgn_retract.seconds() > limit_mgn_retract)
                    {
                        Extendo.CS = extendo.extendoStatus.RETRACTED;
                        Mgn.CS = mgn.mgnStatus.DRIVE;
                        globals.is_intransfer = false;
                        CS = TRANSFER_DONE;
                    }
                    break;
                }

            }
        }

        PS = CS;
    }

}