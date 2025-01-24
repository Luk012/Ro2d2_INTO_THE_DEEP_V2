package systems;

import static systems.pto.ptoStatus.INITIALIZE;
import static systems.pto.ptoStatus.OFF;

import com.acmerobotics.dashboard.config.Config;

import globals.robotMap;

@Config
public class pto {

    public enum ptoStatus
    {
        INITIALIZE,
        ON,
        OFF,
    }

    public pto()
    {
        CS = INITIALIZE;
        PS = INITIALIZE;
    }

    public static ptoStatus CS = INITIALIZE, PS = INITIALIZE;

    public static double on = 0.64;
    public static double off = 0.95;

    public void update(robotMap r)
    {
        if(CS != PS || CS == INITIALIZE || CS == ptoStatus.ON || CS == OFF)
        {
            switch (CS)
            {
                case INITIALIZE:
                {
                    r.pto.setPosition(off);
                    break;
                }

                case ON:
                {
                    r.pto.setPosition(on);
                    break;
                }

                case OFF:
                {
                    r.pto.setPosition(off);
                    break;
                }

            }
        }

        PS = CS;
    }

}