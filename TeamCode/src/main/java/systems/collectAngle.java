package systems;

import static systems.collectAngle.collectAngleStatus.COLLECT;
import static systems.collectAngle.collectAngleStatus.DRIVE;
import static systems.collectAngle.collectAngleStatus.INITIALIZE;

import com.acmerobotics.dashboard.config.Config;

import globals.robotMap;

@Config
public class collectAngle {

    public enum collectAngleStatus
    {
        INITIALIZE,
        COLLECT,
        DRIVE,
    }

    public collectAngle()
    {
        CS = INITIALIZE;
        PS = INITIALIZE;
    }

    public static collectAngleStatus CS = INITIALIZE, PS = INITIALIZE;

    public static double collect = 0.45;
    public static double drive = 0.95;

    public void update(robotMap r)
    {
        if(CS != PS || CS == INITIALIZE || CS == COLLECT || CS == DRIVE )
        {
            switch (CS)
            {
                case INITIALIZE:
                {
                    r.collectAngle.setPosition(drive);
                    break;
                }

                case COLLECT:
                {
                    r.collectAngle.setPosition(collect);
                    break;
                }

                case DRIVE:
                {
                    r.collectAngle.setPosition(drive);
                    break;
                }
            }

        }

        PS = CS;
    }

}
