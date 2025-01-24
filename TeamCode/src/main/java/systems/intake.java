package systems;

import static systems.intake.intakeStatus.CAINIT;
import static systems.intake.intakeStatus.COLLECT;
import static systems.intake.intakeStatus.COLLECT_DONE;
import static systems.intake.intakeStatus.DRIVE;
import static systems.intake.intakeStatus.DRIVE_DONE;
import static systems.intake.intakeStatus.INITIALIZE;
import static systems.intake.intakeStatus.LONG;
import static systems.intake.intakeStatus.LONG_DONE;
import static systems.intake.intakeStatus.RETRACT_DONE;
import static systems.intake.intakeStatus.RETRACT_EXTENDO;
import static systems.intake.intakeStatus.SHORT_DONE;
import static systems.transfer.transferStatus.TRANSFER_START;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.io.BufferedReader;

import globals.globals;
import globals.robotMap;

@Config
public class intake {

    public enum intakeStatus {

        /**
         * @INTAKE
         */

        INITIALIZE,
        LONG,
        LONG_DONE,
        SHORT,

        SHORT_DONE,

        RETRACT,
        RETRACT_EXTENDO,
        RETRACT_DONE,

        /**
         * @CSCA
         */

        CAINIT,
        COLLECT,
        COLLECT_DONE,
        DRIVE,
        DRIVE_DONE,


    }

    public intake() {
        CS = INITIALIZE;
        PS = INITIALIZE;
    }

    public static intakeStatus CS = INITIALIZE, PS = INITIALIZE;
    public static intakeStatus CSCA = CAINIT, PSCA = CAINIT;

    ElapsedTime retract_timer = new ElapsedTime();

    public static double retract_limit = 0.2;



    public void update(robotMap r, extendo Extendo, collectAngle CollectAngle) {
        if (CS != PS || CS == INITIALIZE) {

            switch (CS) {

                case SHORT:
                {
                    Extendo.CS = extendo.extendoStatus.SHORT;
                    CS = SHORT_DONE;
                    break;
                }

                case LONG:
                {
                    Extendo.CS = extendo.extendoStatus.EXTENDED;
                    CS = LONG_DONE;
                    break;
                }

                case RETRACT:
                {
                    if(CollectAngle.CS != collectAngle.collectAngleStatus.DRIVE)
                    {
                        CollectAngle.CS = collectAngle.collectAngleStatus.DRIVE;
                        retract_limit = 0.2;
                        retract_timer.reset();
                        CS = RETRACT_EXTENDO;
                    } else
                    {
                        retract_limit = 0;
                        retract_timer.reset();
                        CS = RETRACT_EXTENDO;
                    }
                    break;
                }

                case RETRACT_EXTENDO:
                {
                   if(retract_timer.seconds() > retract_limit)
                   {
                       Extendo.CS = extendo.extendoStatus.RETRACTED;
                       CS = RETRACT_DONE;
                   }
                   break;
                }
            }
        }

        if(CSCA != PSCA || CSCA == CAINIT)
        {
            switch (CSCA)
            {
                case COLLECT:
                {
                    CollectAngle.CS = collectAngle.collectAngleStatus.COLLECT;
                    CSCA = COLLECT_DONE;
                    break;
                }

                case DRIVE:
                {
                 CollectAngle.CS = collectAngle.collectAngleStatus.DRIVE;
                 CSCA = DRIVE_DONE;
                 break;
                }
            }
        }

        PSCA = CSCA;
        PS = CS;
    }

}