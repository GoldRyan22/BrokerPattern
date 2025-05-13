import Commons.Address;
import Registry.Entry;
import RequestReply.ByteStreamTransformer;
import RequestReply.Replyer;

public class MathServer {
    public static void main(String[] args) 
    {
        //new Configuration();

        MathServerOrig mathImpl = new MathServerOrig();
        MathProxy proxy = new MathProxy(mathImpl);

        Address myAddr = new Entry("127.0.0.1", 1114); 
        proxy.broker = new BrokerApi();

        proxy.broker.AddServer("MathServer", myAddr.dest(), myAddr.port());

        ByteStreamTransformer transformer = new ServerTransformer(proxy);
        Replyer r = new Replyer("MathServer", myAddr);

        while (true) {
            r.receive_transform_and_send_feedback(transformer);
        }
    }
}
