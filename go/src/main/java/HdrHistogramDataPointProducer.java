import org.HdrHistogram.Histogram;

public class HdrHistogramDataPointProducer extends  DataPointProducer{

    private Histogram histogram = new Histogram(3);

    public HdrHistogramDataPointProducer(int itterations){
        super(itterations);
    }


    public Object call() throws Exception {
        totalOperationNanos =0;
        totalCount=0;
        long startNs = System.nanoTime();
        for(int i=0; i<itterations; i++){

            long start = System.nanoTime();
            opperation(i);
            histogram.recordValue(System.nanoTime() - start);

        }
        durationNs = System.nanoTime() - startNs;

        return null;
    }

    @Override
    void handelDataPoint(DataPoint dataPoint) {}

}