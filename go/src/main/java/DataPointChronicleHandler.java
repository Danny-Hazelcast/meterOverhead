import com.lmax.disruptor.EventHandler;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.tools.ChronicleTools;

import java.io.IOException;

public class DataPointChronicleHandler implements EventHandler<DataPoint>
{
    public static String baseDir = "/Users/danny/fastOut/disruptChronicle";
    Chronicle chronicle;
    ExcerptAppender appender;

    public DataPointChronicleHandler() throws IOException {
        ChronicleTools.deleteDirOnExit(baseDir);
        chronicle = ChronicleQueueBuilder.vanilla(baseDir).build();
        chronicle.clear();
        appender = chronicle.createAppender();
    }

    public void onEvent(DataPoint dataPoint, long sequence, boolean endOfBatch) {
        appender.startExcerpt();
        appender.writeLong(dataPoint.nanoTime);
        appender.writeLong(dataPoint.nanoElapsed);
        appender.finish();
    }
}


