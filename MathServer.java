import Commons.Address;
import Registry.Entry;
import RequestReply.ByteStreamTransformer;
import RequestReply.Replyer;

public class MathServer 
{
    public static void main(String[] args) 
    {
        //new Configuration();

        int port = 2222; // default
        if (args.length > 0) 
        {
            port = Integer.parseInt(args[0]);
        }

        System.out.println("I am alive");

        MathServerOrig math = new MathServerOrig();
        //MathProxy proxy = new MathProxy(math);

        GeneralServerProxy proxy = new GeneralServerProxy(math, "MathServer");

        Address myAddr = new Entry("127.0.0.1", port); 
        //BrokerApi broker = new BrokerApi();

        //broker.AddServer("MathServer", myAddr.dest(), myAddr.port());

        ByteStreamTransformer transformer = new ServerTransformer(proxy);
        Replyer r = new Replyer("MathServer", myAddr);

        while (true) 
        {
            r.receive_transform_and_send_feedback(transformer);
        }
    }
}
