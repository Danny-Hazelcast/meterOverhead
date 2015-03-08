import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptTailer;

/**
 * Created by danny on 3/7/15.
 */
public class DataPoint {

    public long nanoTime;
    public long nanoElapsed;

    public void write(ExcerptAppender appender){
        appender.startExcerpt();
        appender.writeLong(nanoTime);
        appender.writeLong(nanoElapsed);
        appender.finish();
    }

    public boolean read(ExcerptTailer tailer){
        if (tailer.nextIndex()){
            nanoTime = tailer.readLong();
            nanoElapsed = tailer.readLong();
            tailer.finish();
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "nanoTime=" + nanoTime +
                ", nanoElapsed=" + nanoElapsed +
                '}';
    }
}

