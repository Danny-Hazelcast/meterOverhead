import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

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
        totalOperationNanos =0;
        totalCount=0;
        long startNs = System.nanoTime();
        for(int i=0; i<itterations; i++){

            Timer.Context context =  timer.time();

                opperation();

            context.stop();
        }
        durationNs = System.nanoTime() - startNs;

        return null;
    }

    @Override
    void handelDataPoint(DataPoint dataPoint) {}

}