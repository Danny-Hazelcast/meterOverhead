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

            opperation();

            dataPoint.nanoTime=oppTimeNanos;
            dataPoint.nanoElapsed=elapsedNs;

            handelDataPoint(dataPoint);
        }
        durationNs = System.nanoTime() - startNs;

        return null;
    }

    public void opperation() throws Exception{
        oppTimeNanos = System.nanoTime();

            //Thread.sleep(1);
            for(int i=0; i<50000; i++){
                x = i % 13;
            }

        elapsedNs = System.nanoTime() - oppTimeNanos;
        totalOperationNanos +=elapsedNs;
        totalCount++;
    }

    abstract void handelDataPoint(DataPoint dataPoint);

    public void printStats(){

        long overhead = durationNs - totalOperationNanos;

        long durationMills = TimeUnit.MILLISECONDS.convert(durationNs, TimeUnit.NANOSECONDS);

        double avgOps =  (double)durationMills / (double)totalCount;
        System.out.println("duration          nanos = "+durationNs);
        System.out.println("totalOperation    nanos = "+ totalOperationNanos);
        System.out.println("overhead          nanos = "+ overhead);
        System.out.println("count                   = "+totalCount);
        System.out.println("Avg Operation     mills = "+avgOps);
        System.out.println("duration          mills = "+durationMills);
        System.out.println("totalOperation    mills = "+  TimeUnit.MILLISECONDS.convert(totalOperationNanos, TimeUnit.NANOSECONDS) );
        System.out.println("overhead          mills = "+  TimeUnit.MILLISECONDS.convert(overhead, TimeUnit.NANOSECONDS) );
        printOverHead();

        System.out.println();
    }

    public void printOverHead(){

        long overhead = durationNs - totalOperationNanos;
        double overHeadPercent  = ( (double)overhead/(double)durationNs ) * 100.0d;
        double operationPercent = ( (double)totalOperationNanos/(double)durationNs ) * 100.0d;
        System.out.println("overhead              % = "+overHeadPercent);
        System.out.println("operation             % = "+operationPercent);
        System.out.println("total                 % = "+(overHeadPercent + operationPercent));
    }
}