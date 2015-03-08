import com.lmax.disruptor.RingBuffer;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class DisruptorDataPointProducer extends DataPointProducer {

    private RingBuffer<DataPoint> ringBuffer=null;

    public DisruptorDataPointProducer(int itterations, RingBuffer<DataPoint> ringBuffer){
        super(itterations);
        this.ringBuffer=ringBuffer;
    }

    void handelDataPoint(DataPoint point) {

        long sequence = ringBuffer.next();
        try {
            DataPoint dataPoint = ringBuffer.get(sequence);
            dataPoint.nanoTime=point.nanoTime;
            dataPoint.nanoElapsed=point.nanoElapsed;
        }
        finally {
            ringBuffer.publish(sequence);
        }
    }
}
