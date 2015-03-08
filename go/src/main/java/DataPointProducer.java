import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class DataPointProducer implements Callable<Object> {

    private DataPoint dataPoint = new DataPoint();

    final int itterations;
    long totalOperationNanos=0;
    long durationNs=0;
    long totalCount=0;

    private long oppTimeNanos=0;
    private long elapsedNs=0;

    volatile int x=0;

    public DataPointProducer(int itterations){
        this.itterations=itterations;
    }

    public Object call() throws Exception {
        totalOperationNanos =0;
        totalCount=0;
        long startNs = System.nanoTime();
        for(int i=0; i<itterations; i++){

            opperation(i);

            dataPoint.nanoTime=oppTimeNanos;
            dataPoint.nanoElapsed=elapsedNs;

            handelDataPoint(dataPoint);
        }
        durationNs = System.nanoTime() - startNs;

        return null;
    }

    public void opperation(int i){
        oppTimeNanos = System.nanoTime();

            //Thread.sleep(1);
            x = i % 13;

        elapsedNs = System.nanoTime() - oppTimeNanos;
        totalOperationNanos +=elapsedNs;
        totalCount++;
    }

    abstract void handelDataPoint(DataPoint dataPoint);

    public void printStats(){
        System.out.println("duration          nanos = "+durationNs);
        System.out.println("totalOperation    nanos = "+ totalOperationNanos);
        System.out.println("overhead          nanos = "+(durationNs- totalOperationNanos));
        System.out.println("count                   = "+totalCount);
        System.out.println("duration          mills = "+  TimeUnit.MILLISECONDS.convert(durationNs, TimeUnit.NANOSECONDS) );
        System.out.println("totalOperation    mills = "+  TimeUnit.MILLISECONDS.convert(totalOperationNanos, TimeUnit.NANOSECONDS) );
        System.out.println("overhead          mills = "+  TimeUnit.MILLISECONDS.convert(durationNs- totalOperationNanos, TimeUnit.NANOSECONDS) );

        System.out.println();
    }

    public int getItterations() {
        return itterations;
    }

    public long getTotalOperationNanos() {
        return totalOperationNanos;
    }

    public long getDurationNs() {
        return durationNs;
    }

    public long getTotalCount() {
        return totalCount;
    }
}