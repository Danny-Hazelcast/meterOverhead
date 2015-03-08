import com.lmax.disruptor.EventFactory;


public class DataPointFactory implements EventFactory<DataPoint>
{
    public DataPoint newInstance() {
        return new DataPoint();
    }
}


