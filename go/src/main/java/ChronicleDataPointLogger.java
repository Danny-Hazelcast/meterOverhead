import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.ExcerptTailer;
import net.openhft.chronicle.tools.ChronicleTools;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.*;


public class ChronicleDataPointLogger extends DataPointProducer{

    public static final String baseDir = "/Users/danny/fastOut/loger";

    private static Chronicle chronicle;
    private static ExcerptAppender appender;

    public ChronicleDataPointLogger(int itterations) throws IOException {
        super(itterations);

        ChronicleTools.deleteDirOnExit(baseDir);
        chronicle = ChronicleQueueBuilder.vanilla(baseDir).build();
        chronicle.clear();
        appender = chronicle.createAppender();
    }

    @Override
    void handelDataPoint(DataPoint dataPoint) {
        //System.out.print(dataPoint);
        dataPoint.write(appender);
    }
}
