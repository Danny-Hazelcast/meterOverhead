import com.codahale.metrics.CsvReporter;
import com.codahale.metrics.MetricRegistry;
import com.lmax.disruptor.dsl.Disruptor;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class Test {
    public int itterations=100000;
    public int producerThreads=16;

    CompletionService<Object> completionService = new ExecutorCompletionService(Executors.newFixedThreadPool(producerThreads));

    private Disruptor<DataPoint> disruptor;

    Set<Callable> chronicleDataPointLoggerSet = new HashSet();
    Set<Callable> disruptorDataPointProducerSet = new HashSet();
    Set<Callable> metricsDataPointProducerSet = new HashSet();
    Set<Callable> hdrHistogramDataPointProducerSet = new HashSet();


    public Test() throws Exception{
        System.out.println("itterations="+itterations);
        System.out.println("producerThreads="+producerThreads);

        init_testDisruptedChronicleLogging();

        for(int i =0; i<producerThreads; i++){
            chronicleDataPointLoggerSet.add(new ChronicleDataPointLogger(itterations));
            disruptorDataPointProducerSet.add( new DisruptorDataPointProducer(itterations, disruptor.getRingBuffer()) );
            metricsDataPointProducerSet.add( new MetricsDataPointProducer(itterations) );
            hdrHistogramDataPointProducerSet.add(new HdrHistogramDataPointProducer(itterations));
        }

    }

    public void init_testDisruptedChronicleLogging() throws Exception{
        Executor executor = Executors.newCachedThreadPool();
        DataPointFactory factory = new DataPointFactory();
        int bufferSize = (int) Math.pow(2, 15);

        disruptor = new Disruptor(factory, bufferSize, executor);
        disruptor.handleEventsWith(new DataPointChronicleHandler());
        disruptor.start();
    }



    public void runCallables(Set<Callable> callables) throws Exception{
        for(Callable c : callables){
            completionService.submit(c);
        }
        for(int i=0; i<callables.size(); i++){
            completionService.take();
        }
        for(Callable c : callables){
            ((DataPointProducer)c).printStats();
        }
    }

    public void testChronicleLogging()throws Exception{
        runCallables(chronicleDataPointLoggerSet);
    }

    public void testDisruptedChronicleLogging() throws Exception{
        runCallables(disruptorDataPointProducerSet);
    }

    public void testMetricsDataPointProducer() throws Exception {

        MetricRegistry metricRegistry = new MetricRegistry();
        for(Callable c : metricsDataPointProducerSet){
            ((MetricsDataPointProducer)c).setMetricRegistry(metricRegistry);
        }

        File baseDir = new File("/Users/danny/fastOut");
        CsvReporter csvReporter = CsvReporter.forRegistry(metricRegistry).build(baseDir);
        csvReporter.start(1, TimeUnit.SECONDS);

        runCallables(metricsDataPointProducerSet);
        csvReporter.stop();
    }

    public void testHdrHistogramDataPointProducer()throws Exception{
        runCallables(hdrHistogramDataPointProducerSet);
    }

    public static void main(String[] args) throws Exception {
        Test t = new Test();

        for(int i=0; i<2; i++){
        //    System.out.println("===testChronicleLogging======================"+"run"+(i+1)+"======================");
        //    t.testChronicleLogging();
        }
        for(int i=0; i<2; i++){
            System.out.println("===testDisruptedChronicleLogging============="+"run"+(i+1)+"======================");
            t.testDisruptedChronicleLogging();
        }

        for(int i=0; i<2; i++){
            System.out.println("===testMetricsDataPointProducer=============="+"run"+(i+1)+"======================");
            t.testMetricsDataPointProducer();
        }

        for(int i=0; i<2; i++){
            System.out.println("===testHdrHistogramDataPointProducer=============="+"run"+(i+1)+"======================");
            t.testHdrHistogramDataPointProducer();
        }

        /*
        Chronicle chronicle = ChronicleQueueBuilder.vanilla(DataPointChronicleHandler.baseDir).build();
        ExcerptTailer tailer = chronicle.createTailer();
        DataPoint d = new DataPoint();
        while(d.read(tailer)){
            System.out.println(d);
        }
        */

    }
}
