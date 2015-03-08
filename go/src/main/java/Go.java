import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import net.openhft.chronicle.Chronicle;
import net.openhft.chronicle.ExcerptAppender;
import net.openhft.chronicle.tools.ChronicleTools;
import net.openhft.chronicle.VanillaChronicle;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Go {

    public class Event
    {
        public long value;

        public void set(long value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Event{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    public class LongEventFactory implements EventFactory<Event>
    {
        public Event newInstance() {
            return new Event();
        }
    }


    public class LongEventHandler implements EventHandler<Event>
    {
        Chronicle chronicle;
        ExcerptAppender logger;

        public LongEventHandler() throws IOException {
            String baseDir = "/Users/danny/fastOut";
            ChronicleTools.deleteDirOnExit(baseDir);
            //chronicle = new VanillaChronicle(baseDir);
            logger = chronicle.createAppender();
        }

        public void onEvent(Event event, long sequence, boolean endOfBatch) {
            //System.out.println(event);

            logger.startExcerpt();
            logger.writeLong(event.value);
            logger.finish();
        }
    }



    public class LongEventProducer
    {
        private final RingBuffer<Event> ringBuffer;

        public LongEventProducer(RingBuffer<Event> ringBuffer) {
            this.ringBuffer = ringBuffer;
        }

        public void onData(long s) {
            long sequence = ringBuffer.next();  // Grab the next sequence
            try {
                Event event = ringBuffer.get(sequence); // Get the entry in the Disruptor
                // for the sequence
                event.set(s);  // Fill with data
            }
            finally {
                ringBuffer.publish(sequence);
            }
        }
    }


    public class LoadProducer implements Runnable {
        private LongEventProducer producer;

        public LoadProducer(String name, RingBuffer<Event> ringBuffer){
            producer = new LongEventProducer(ringBuffer);
        }

        public void run(){
            long start = System.currentTimeMillis();
            for(int i=0; i<1000000; i++){
                producer.onData(i);
            }
            long end = System.currentTimeMillis();

            long duration = end - start;
            System.out.println("duration="+duration);
        }
    }


    public class directLogging implements Runnable {
        Chronicle chronicle;
        ExcerptAppender logger;

        public directLogging() throws IOException {
            String baseDir = "/Users/danny/fastOut";
            ChronicleTools.deleteDirOnExit(baseDir);
            //chronicle = new VanillaChronicle(baseDir);
            logger = chronicle.createAppender();
        }

        public void run(){
            long start = System.currentTimeMillis();
            for(int i=0; i<1000000; i++){

                logger.startExcerpt();
                logger.writeLong(i);
                logger.finish();

            }
            long end = System.currentTimeMillis();

            long duration = end - start;
            System.out.println("directLogging duration="+duration);
        }
    }



    public void doit() throws Exception {
        // Executor that will be used to construct new threads for consumers
        Executor executor = Executors.newCachedThreadPool();

        // The factory for the event
        LongEventFactory factory = new LongEventFactory();

        // Specify the size of the ring buffer, must be power of 2.
        int bufferSize = (int) Math.pow(2, 15);

        // Construct the Disruptor
        Disruptor<Event> disruptor = new Disruptor(factory, bufferSize, executor);

        // Connect the handler
        disruptor.handleEventsWith(new LongEventHandler());

        // Start the Disruptor, starts all threads running
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        RingBuffer<Event> ringBuffer = disruptor.getRingBuffer();

        int producerThreads=16;
        ExecutorService producers = Executors.newFixedThreadPool(producerThreads);
        for(int i =0; i<producerThreads; i++){
            //producers.submit(new LoadProducer("p"+i, ringBuffer));
            producers.submit(new directLogging());
        }


    }

    public static void main(String[] args) throws Exception {
        Go go = new Go();
        go.doit();
    }
}
