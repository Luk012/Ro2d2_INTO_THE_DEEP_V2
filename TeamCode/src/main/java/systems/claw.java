package systems;

import static systems.claw.clawStatus.CLOSED;
import static systems.claw.clawStatus.INITIALIZE;
import static systems.claw.clawStatus.OPENED;

import com.acmerobotics.dashboard.config.Config;

import globals.robotMap;

@Config
public class claw {
    public enum clawStatus
    {
        INITIALIZE,
        CLOSED,
        OPENED,
    }

    public claw()
    {
        CS = INITIALIZE;
        PS = INITIALIZE;
    }

    public static clawStatus CS = INITIALIZE, PS = INITIALIZE;

    public static double closed = 1;
    public static double opened = 0 ;

    public void update(robotMap r)
    {
        if(PS != CS || CS == INITIALIZE || CS == CLOSED || CS == OPENED )
        {
            switch (CS)
            {
                case INITIALIZE:
                {
                    r.claw.setPosition(opened);
                    break;
                }

                case CLOSED:
                {
                    r.claw.setPosition(closed);
                    break;
                }

                case OPENED:
                {
                    r.claw.setPosition(opened);
                    break;
                }
            }
        }

        PS = CS;
    }
}
