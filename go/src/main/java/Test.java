import com.codahale.metrics.MetricRegistry;
import com.lmax.disruptor.dsl.Disruptor;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ChronicleQueueBuilder;
import net.openhft.chronicle.ExcerptTailer;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class Test {
    public int itterations=1000000;
    public int producerThreads=1;

    CompletionService<Object> completionService = new ExecutorCompletionService(Executors.newFixedThreadPool(producerThreads));

    private Disruptor<DataPoint> disruptor;

    Set<Callable> chronicleDataPointLoggerSet = new HashSet();
    Set<Callable> disruptorDataPointProducerSet = new HashSet();
    Set<Callable> metricsDataPointProducerSet = new HashSet();

    public Test() throws Exception{
        init_testDisruptedChronicleLogging();

        for(int i =0; i<producerThreads; i++){
            chronicleDataPointLoggerSet.add(new ChronicleDataPointLogger(itterations));
            disruptorDataPointProducerSet.add( new DisruptorDataPointProducer(itterations, disruptor.getRingBuffer()) );

            metricsDataPointProducerSet.add( new MetricsDataPointProducer(itterations) );
        }

    }

    public void init_testDisruptedChronicleLogging() throws Exception{
        Executor executor = Executors.newCachedThreadPool();
        DataPointFactory factory = new DataPointFactory();
        int bufferSize = (int) Math.pow(2, 15);

        disruptor = new Disruptor(factory, bufferSize, executor);
        disruptor.handleEventsWith(new DataPointEventHandler());
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


    public void runCallables2(Set<Callable> callables) throws Exception{

        for(Callable c : callables){
            ((MetricsDataPointProducer)c).setMetricRegistry(new MetricRegistry());
        }


        for(Callable c : callables){
            completionService.submit(c);
        }
        for(int i=0; i<callables.size(); i++){
            completionService.take();
        }

        for(Callable c : callables){
            ((DataPointProducer)c).printStats();
        }

        /*
        long totalDurationNs =0;
        long totalCount =0;
        for(Callable c : callables){
            totalDurationNs += ((MetricsDataPointProducer)c).getDurationNs();
            totalCount += ((MetricsDataPointProducer)c).getTotalCount();
        }
        */
    }


    public void testChronicleLogging()throws Exception{
        runCallables(chronicleDataPointLoggerSet);
    }

    public void testDisruptedChronicleLogging() throws Exception{
        runCallables(disruptorDataPointProducerSet);
    }

    public void testMetricsDataPointProducer() throws Exception {
        runCallables2(metricsDataPointProducerSet);
    }

    public static void main(String[] args) throws Exception {
        Test t = new Test();

        for(int i=0; i<2; i++){
            System.out.println("===testChronicleLogging======================"+"run"+(i+1)+"======================");
            t.testChronicleLogging();
        }
        for(int i=0; i<2; i++){
            System.out.println("===testDisruptedChronicleLogging============="+"run"+(i+1)+"======================");
            t.testDisruptedChronicleLogging();
        }

        for(int i=0; i<2; i++){
            System.out.println("===testMetricsDataPointProducer=============="+"run"+(i+1)+"======================");
            t.testMetricsDataPointProducer();
        }

        /*
        Chronicle chronicle = ChronicleQueueBuilder.vanilla(DataPointEventHandler.baseDir).build();
        ExcerptTailer tailer = chronicle.createTailer();
        DataPoint d = new DataPoint();
        while(d.read(tailer)){
            System.out.println(d);
        }
        */

    }
}
