package abc.def.mp3.Utils;


public class MusicUtils {

    public static final int MAX_PROGRESS = 10000;


    public int getProgressSeekBar(long currentDuration, long totalDuration) {
        Double progress = (double) 0;
        progress = (((double) currentDuration) / totalDuration) * MAX_PROGRESS;
        return progress.intValue();
    }
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / MAX_PROGRESS) * totalDuration);
        return currentDuration * 1000;
    }

}
