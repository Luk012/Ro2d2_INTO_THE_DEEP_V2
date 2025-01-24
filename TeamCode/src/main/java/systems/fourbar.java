package systems;

import static systems.fourbar.fourbarStatus.COLLECT_SPECIMEN;
import static systems.fourbar.fourbarStatus.DRIVE;
import static systems.fourbar.fourbarStatus.INITIALIZE;
import static systems.fourbar.fourbarStatus.SCORE_SAMPLE;
import static systems.fourbar.fourbarStatus.SCORE_SPECIMEN;

import com.acmerobotics.dashboard.config.Config;

import globals.robotMap;

@Config
public class fourbar {

    public enum fourbarStatus {
        SCORE_SPECIMEN,
        COLLECT_SPECIMEN,
        INITIALIZE,
        SCORE_SAMPLE,
        DRIVE,

    }

    public fourbar() {
        CS = INITIALIZE;
        PS = INITIALIZE;
    }

    public static fourbarStatus CS = INITIALIZE, PS = INITIALIZE;

    public static double score_specimen = 0.72;
    public static double collect_specimen = 0.1;
    public static double score_sample = 0.42;
    public static double drive = 1;


    public void update(robotMap r)
    {
        if (PS != CS || CS == INITIALIZE || CS == DRIVE || CS == COLLECT_SPECIMEN  || CS == SCORE_SPECIMEN || CS == SCORE_SAMPLE)

        {

            switch (CS) {
                case INITIALIZE: {
                    r.fourbarLeft.setPosition(drive);
                    r.fourbarRight.setPosition(drive);
                    break;
                }

                case COLLECT_SPECIMEN: {
                    r.fourbarLeft.setPosition(collect_specimen);
                    r.fourbarRight.setPosition(collect_specimen);
                    break;
                }

                case DRIVE:
                {
                    r.fourbarLeft.setPosition(drive);
                    r.fourbarRight.setPosition(drive);
                    break;
                }

                case SCORE_SPECIMEN: {
                    r.fourbarLeft.setPosition(score_specimen);
                    r.fourbarRight.setPosition(score_specimen);
                    break;
                }

                case SCORE_SAMPLE: {
                    r.fourbarLeft.setPosition(score_sample);
                    r.fourbarRight.setPosition(score_sample);
                    break;
                }

            }
        }
        PS = CS;
    }

}