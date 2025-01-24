package systems;

import static systems.outtake.outtakeStatus.COLLECT_SPECIMEN_DONE;
import static systems.outtake.outtakeStatus.COLLECT_SPECIMEN_FOURBAR;
import static systems.outtake.outtakeStatus.COLLECT_SPECIMEN_LIFT;
import static systems.outtake.outtakeStatus.COLLECT_SPECIMEN_MGN;
import static systems.outtake.outtakeStatus.DRIVE_DONE;
import static systems.outtake.outtakeStatus.DRIVE_FOURBAR;
import static systems.outtake.outtakeStatus.DRIVE_LIFT;
import static systems.outtake.outtakeStatus.DRIVE_MGN;
import static systems.outtake.outtakeStatus.INITIALIZE;
import static systems.outtake.outtakeStatus.SCORE_SAMPLE_DONE;
import static systems.outtake.outtakeStatus.SCORE_SAMPLE_FOURBAR;
import static systems.outtake.outtakeStatus.SCORE_SAMPLE_MGN;
import static systems.outtake.outtakeStatus.SCORE_SPECIMEN_DONE;
import static systems.outtake.outtakeStatus.SCORE_SPECIMEN_FOURBAR;
import static systems.outtake.outtakeStatus.SCORE_SPECIMEN_MGN;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

import globals.globals;
import globals.robotMap;

@Config
public class outtake {

    public enum outtakeStatus
    {
        INITIALIZE,

        /**
         * @DRIVE
         */
        DRIVE_CLAW,
        DRIVE_MGN,
        DRIVE_FOURBAR,
        DRIVE_LIFT,
        DRIVE_DONE,

        /**
         * @COLLECT_SPECIMEN
         */

        COLLECT_SPECIMEN_OPEN_CLAW,
        COLLECT_SPECIMEN_LIFT,
        COLLECT_SPECIMEN_FOURBAR,
        COLLECT_SPECIMEN_MGN,
        COLLECT_SPECIMEN_DONE,

        /**
         * @SCORE_SPECIMEN
         */

        SCORE_SPECIMEN_LIFT,
        SCORE_SPECIMEN_FOURBAR,
        SCORE_SPECIMEN_MGN,
        SCORE_SPECIMEN_DONE,

        /**
         * @SCORE_SAMPLE
         */

        SCORE_SAMPLE_LIFT,
        SCORE_SAMPLE_FOURBAR,
        SCORE_SAMPLE_MGN,
        SCORE_SAMPLE_DONE,

    }

    public outtake()
    {
        CS = INITIALIZE;
        PS = INITIALIZE;
    }

    public static outtakeStatus CS = INITIALIZE, PS = INITIALIZE;
    ElapsedTime timer_mgn = new ElapsedTime();
    ElapsedTime timer_fourbar = new ElapsedTime();
    ElapsedTime timer_lift = new ElapsedTime();



    public void update(robotMap r, claw Claw, lift Lift, fourbar Fourbar, mgn Mgn)
    {
        if(CS != PS || CS == INITIALIZE)
        {
            switch (CS)
            {

                /**
                 * @DRIVE
                 */

                case DRIVE_CLAW:
                {
                    Claw.CS = claw.clawStatus.OPENED;
                    timer_mgn.reset();
                    CS = DRIVE_MGN;
                    break;
                }

                case DRIVE_MGN:
                {
                    if (Mgn.CS != mgn.mgnStatus.DRIVE)
                    {
                        if (timer_mgn.seconds() > globals.limit_mgn)
                        {
                            Mgn.CS = mgn.mgnStatus.DRIVE;
                            timer_fourbar.reset();
                            CS = DRIVE_FOURBAR;
                        }
                    }
                    else
                    {
                        timer_fourbar.reset();
                        CS = DRIVE_FOURBAR;
                    }
                    break;
                }

                case DRIVE_FOURBAR:
                {
                    if (Fourbar.CS != fourbar.fourbarStatus.DRIVE)
                    {
                        if (timer_fourbar.seconds() > globals.limit_fourbar)
                        {
                            Fourbar.CS = fourbar.fourbarStatus.DRIVE;
                            timer_lift.reset();
                            CS = DRIVE_LIFT;
                        }
                    }
                    else
                    {
                        timer_lift.reset();
                        CS = DRIVE_LIFT;
                    }
                    break;
                }

                case DRIVE_LIFT:
                {
                    if (Lift.CS != lift.liftStatus.DOWN)
                    {
                        if (timer_lift.seconds() > globals.limit_lift)
                        {
                            Lift.CS = lift.liftStatus.DOWN;
                            CS = DRIVE_DONE;
                        }
                    }
                    else
                    {
                        CS = DRIVE_DONE;
                    }
                    break;
                }

                /**
                 * @COLLECT_SPECIMEN
                 */

                case COLLECT_SPECIMEN_OPEN_CLAW:
                {
                    Claw.CS = claw.clawStatus.OPENED;
                    CS = COLLECT_SPECIMEN_LIFT;
                    break;
                }

                case COLLECT_SPECIMEN_LIFT:
                {
                    if (Lift.CS != lift.liftStatus.DOWN)
                    {
                        Lift.CS = lift.liftStatus.DOWN;
                        timer_fourbar.reset();
                        CS = COLLECT_SPECIMEN_FOURBAR;
                    }
                    else
                    {
                        timer_fourbar.reset();
                        CS = COLLECT_SPECIMEN_FOURBAR;
                    }
                    break;
                }

                case COLLECT_SPECIMEN_FOURBAR:
                {
                    if (Fourbar.CS != fourbar.fourbarStatus.COLLECT_SPECIMEN)
                    {
                        if (timer_fourbar.seconds() > globals.limit_fourbar)
                        {
                            Fourbar.CS = fourbar.fourbarStatus.COLLECT_SPECIMEN;
                            timer_mgn.reset();
                            CS = COLLECT_SPECIMEN_MGN;
                        }
                    }
                    else
                    {
                        timer_mgn.reset();
                        CS = COLLECT_SPECIMEN_MGN;
                    }
                    break;
                }

                case COLLECT_SPECIMEN_MGN:
                {
                    if (Mgn.CS != mgn.mgnStatus.COLLECT_SPECIMEN)
                    {
                        if (timer_mgn.seconds() > globals.limit_mgn)
                        {
                            Mgn.CS = mgn.mgnStatus.COLLECT_SPECIMEN;
                            CS = COLLECT_SPECIMEN_DONE;
                        }
                    }
                    else
                    {
                        CS = COLLECT_SPECIMEN_DONE;
                    }
                    break;
                }

                /**
                 * @SCORE_SPECIMEN
                 */

                case SCORE_SPECIMEN_LIFT:
                {
                    if(globals.specimen_level == "high")
                    {
                        Lift.CS = lift.liftStatus.SPECIMEN_HIGH;
                        timer_fourbar.reset();
                        CS = SCORE_SPECIMEN_FOURBAR;
                    } else {
                        Lift.CS = lift.liftStatus.SPECIMEN_LOW;
                        timer_fourbar.reset();
                        CS = SCORE_SPECIMEN_FOURBAR;
                    }
                    break;
                }

                case SCORE_SPECIMEN_FOURBAR:
                {
                    if (Fourbar.CS != fourbar.fourbarStatus.SCORE_SPECIMEN)
                    {
                        if (timer_fourbar.seconds() > globals.limit_fourbar)
                        {
                            Fourbar.CS = fourbar.fourbarStatus.SCORE_SPECIMEN;
                            timer_mgn.reset();
                            CS = SCORE_SPECIMEN_MGN;
                        }
                    }
                    else
                    {
                        timer_mgn.reset();
                        CS = SCORE_SPECIMEN_MGN;
                    }
                    break;
                }

                case SCORE_SPECIMEN_MGN:
                {
                    if (Mgn.CS != mgn.mgnStatus.SCORE_SPECIMEN)
                    {
                        if (timer_mgn.seconds() > globals.limit_mgn)
                        {
                            Mgn.CS = mgn.mgnStatus.SCORE_SPECIMEN;
                            CS = SCORE_SPECIMEN_DONE;
                        }
                    }
                    else
                    {
                        CS = SCORE_SPECIMEN_DONE;
                    }
                    break;
                }

                /**
                 * @SCORE_SAMPLE
                 */

                case SCORE_SAMPLE_LIFT:
                {
                    if(globals.sample_level == "high")
                    {
                        Lift.CS = lift.liftStatus.SAMPLE_HIGH;
                        timer_fourbar.reset();
                        CS = SCORE_SAMPLE_FOURBAR;
                    } else {
                        Lift.CS = lift.liftStatus.SAMPLE_LOW;
                        timer_fourbar.reset();
                        CS = SCORE_SAMPLE_FOURBAR;
                    }
                    break;
                }

                case SCORE_SAMPLE_FOURBAR:
                {
                    if (Fourbar.CS != fourbar.fourbarStatus.SCORE_SAMPLE)
                    {
                        if (timer_fourbar.seconds() > globals.limit_fourbar)
                        {
                            Fourbar.CS = fourbar.fourbarStatus.SCORE_SAMPLE;
                            timer_mgn.reset();
                            CS = SCORE_SAMPLE_MGN;
                        }
                    }
                    else
                    {
                        timer_mgn.reset();
                        CS = SCORE_SAMPLE_MGN;
                    }
                    break;
                }

                case SCORE_SAMPLE_MGN:
                {
                    if (Mgn.CS != mgn.mgnStatus.SCORE_SAMPLE)
                    {
                        if (timer_mgn.seconds() > globals.limit_mgn)
                        {
                            Mgn.CS = mgn.mgnStatus.SCORE_SAMPLE;
                            CS = SCORE_SAMPLE_DONE;
                        }
                    }
                    else
                    {
                        CS = SCORE_SAMPLE_DONE;
                    }
                    break;
                }

            }
        }

        PS = CS;
    }

}