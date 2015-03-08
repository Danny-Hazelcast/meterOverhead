import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class MetricsDataPointProducer extends  DataPointProducer{

    private MetricRegistry metrics;
    private Timer timer;

    public MetricsDataPointProducer(int itterations){
        super(itterations);

    }

    public void setMetricRegistry(MetricRegistry metrics){
        this.metrics = metrics;
        timer = metrics.timer("timer");
    }

    public Object call() throws Exception {
        totalElapsedNS=0;
        totalCount=0;
        long startNs = System.nanoTime();
        for(int i=0; i<itterations; i++){

            long oppTimeNanos = System.nanoTime();

                Timer.Context context =  timer.time();
                    //Thread.sleep(1);
                    x = i % 13;
                    totalCount++;
                context.stop();

            long elapsedNs = System.nanoTime() - oppTimeNanos;
            totalElapsedNS+=elapsedNs;
        }
        durationNs = System.nanoTime() - startNs;


        //FOR ALL THREADS USING THIS METRIC RESISTERY
        long totalMetricTime = 0;
        for(long v : timer.getSnapshot().getValues()){
            totalMetricTime += v;
        }
        System.out.println("total="+totalMetricTime);
        //System.out.println("d    ="+(durationNs - totalElapsedNS));

        //totalElapsedNS = TimeUnit.MILLISECONDS.toNanos(totalElapsedNS);

        return null;
    }

    @Override
    void handelDataPoint(DataPoint dataPoint) {}

}