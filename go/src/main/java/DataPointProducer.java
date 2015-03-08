import com.lmax.disruptor.RingBuffer;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class DataPointProducer implements Callable<Object> {

    private DataPoint dataPoint = new DataPoint();

    final int itterations;
    long totalElapsedNS=0;
    long durationNs=0;
    long totalCount=0;

    volatile int x=0;

    public DataPointProducer(int itterations){
        this.itterations=itterations;
    }

    public Object call() throws Exception {
        totalElapsedNS=0;
        totalCount=0;
        long startNs = System.nanoTime();
        for(int i=0; i<itterations; i++){

            long oppTimeNanos = System.nanoTime();

                //Thread.sleep(1);
                x = i % 13;
                totalCount++;

            long elapsedNs = System.nanoTime() - oppTimeNanos;
            totalElapsedNS+=elapsedNs;

            dataPoint.nanoTime=oppTimeNanos;
            dataPoint.nanoElapsed=elapsedNs;

            handelDataPoint(dataPoint);
        }
        durationNs = System.nanoTime() - startNs;

        return null;
    }

    abstract void handelDataPoint(DataPoint dataPoint);

    public void printStats(){
        System.out.println("duration     nanos = "+durationNs);
        System.out.println("totalElapsed nanos = "+totalElapsedNS);
        System.out.println("overhead     nanos = "+(durationNs-totalElapsedNS));
        System.out.println("count              = "+totalCount);
        System.out.println("duration     mills = "+  TimeUnit.MILLISECONDS.convert(durationNs, TimeUnit.NANOSECONDS) );
        System.out.println("totalElapsed mills = "+  TimeUnit.MILLISECONDS.convert(totalElapsedNS, TimeUnit.NANOSECONDS) );
        System.out.println("overhead     mills = "+  TimeUnit.MILLISECONDS.convert(durationNs-totalElapsedNS, TimeUnit.NANOSECONDS) );

        System.out.println();
    }

    public int getItterations() {
        return itterations;
    }

    public long getTotalElapsedNS() {
        return totalElapsedNS;
    }

    public long getDurationNs() {
        return durationNs;
    }

    public long getTotalCount() {
        return totalCount;
    }
}