import Commons.Address;
import MessageMarshaller.Message;
import Registry.Entry;
import Registry.Registry;
import RequestReply.ByteStreamTransformer;
import RequestReply.Replyer;

class NamingMessageServer extends MessageServer
{
    @Override
    public Message get_answer(Message msg)
    {
        System.out.println("Dispacher received " + msg.data + " from " + msg.sender);

        Entry serverAddress = Registry.instance().get(msg.data);
        String result = "";

        if(serverAddress == null)
        {
            result = "null";
            Message answer = new Message("Dispacher", result);
            return answer;
        }

        result = "" + serverAddress.dest() + "|" + serverAddress.port();
        Message answer = new Message("Dispacher", result);

        return answer;
    }
}

public class NamingService 
{
    public static void main(String[] args) 
    {
        new Configuration();

		ByteStreamTransformer transformer = new ServerTransformer(new NamingMessageServer());
		
		Address myAddr = Registry.instance().get("Dispacher");
		
		Replyer r = new Replyer("Dispacher", myAddr);

		while (true) 
        {
		  r.receive_transform_and_send_feedback(transformer);
		}
    }
}
