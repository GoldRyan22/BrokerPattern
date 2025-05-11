import Commons.Address;
import MessageMarshaller.Marshaller;
import MessageMarshaller.Message;
import Registry.Entry;
import Registry.Registry;
import RequestReply.Requestor;

public class InfoClient 
{
    public static void main(String[] args) 
    {
        new Configuration();

        Address dest=Registry.instance().get("Dispacher");

        Message msg= new Message("Client","InfoServer");

		Requestor r = new Requestor("Client");
		
		Marshaller m = new Marshaller();
			
		byte[] bytes = m.marshal(msg);

		bytes = r.deliver_and_wait_feedback(dest, bytes);
		
		Message answer = m.unmarshal(bytes);

		System.out.println("Client received message "+answer.data+" from "+answer.sender);

        int separator = answer.data.indexOf('|');

        String address = answer.data.substring(0,separator);
        int port =Integer.valueOf(answer.data.substring(separator + 1));

        //System.out.println(address + " op " + port);

        dest = new Entry(address, port);

        msg = new Message("Client", "Temp Arad");

        bytes = m.marshal(msg);

        bytes = r.deliver_and_wait_feedback(dest, bytes);
		
		answer = m.unmarshal(bytes);

		System.out.println("Client received message "+answer.data+" from "+answer.sender);

        msg = new Message("Client", "Road 123");

        bytes = m.marshal(msg);

        bytes = r.deliver_and_wait_feedback(dest, bytes);
		
		answer = m.unmarshal(bytes);

		System.out.println("Client received message "+answer.data+" from "+answer.sender);

        
        
    }
}
