package systems;

import static systems.mgn.mgnStatus.COLLECT_SPECIMEN;
import static systems.mgn.mgnStatus.DRIVE;
import static systems.mgn.mgnStatus.INITIALIZE;
import static systems.mgn.mgnStatus.SCORE_SAMPLE;
import static systems.mgn.mgnStatus.SCORE_SPECIMEN;
import static systems.mgn.mgnStatus.TRANSFER;

import com.acmerobotics.dashboard.config.Config;

import globals.robotMap;

@Config
public class mgn {

    public enum mgnStatus
    {
        INITIALIZE,
        DRIVE,
        TRANSFER,

        COLLECT_SPECIMEN,
        SCORE_SPECIMEN,
        SCORE_SAMPLE,
    }

    public mgn()
    {
        CS = INITIALIZE;
        PS = INITIALIZE;
    }

    public static mgnStatus CS = INITIALIZE, PS = INITIALIZE;

    public static double drive = 0;
    public static double transfer = 0;
    public static double collect_specimen = 0;
    public static double score_specimen = 0;
    public static double score_sample = 0;

    public void update(robotMap r)
    {
        if(CS != PS || CS == INITIALIZE || CS == DRIVE || CS == TRANSFER || CS == COLLECT_SPECIMEN || CS == SCORE_SPECIMEN || CS == SCORE_SAMPLE)
        {
            switch (CS)
            {
                case INITIALIZE:
                {
                    r.mgn.setPosition(drive);
                    break;
                }

                case DRIVE:
                {
                    r.mgn.setPosition(drive);
                    break;
                }

                case TRANSFER:
                {
                    r.mgn.setPosition(transfer);
                    break;
                }

                case COLLECT_SPECIMEN:
                {
                    r.mgn.setPosition(collect_specimen);
                    break;
                }

                case SCORE_SPECIMEN:
                {
                    r.mgn.setPosition(score_specimen);
                    break;
                }

                case SCORE_SAMPLE:
                {
                    r.mgn.setPosition(score_sample);
                    break;
                }
            }
        }

        PS = CS;
    }

}