import Commons.Address;
import MessageMarshaller.Marshaller;
import MessageMarshaller.Message;
import Registry.*;
import RequestReply.Requestor;

public class BrokerApi implements IBroker
{
    
    Message msg;
    Requestor r;
    Marshaller m;
    Address DispAdd;

    public BrokerApi()
    {
        new Configuration();

        DispAdd = Registry.instance().get("Dispacher");   
        m = new Marshaller();
		r = new Requestor("BrokerApiInstance");	
    }

    @Override
    public void AddServer(String name, String Ip, int port)
    {
        String toBeSent = "Add " + name + " " + Ip + ":" + port;

        msg = new Message("BrokerApiInstance", toBeSent);

        byte[] bytes = m.marshal(msg);

        bytes = r.deliver_and_wait_feedback(DispAdd, bytes);
		
		Message answer = m.unmarshal(bytes);

        System.out.println(answer.data);
    }

    @Override
    public Address GetDispacher()
    {
        return this.DispAdd;
    }

    @Override
    public void RemoveServer(String name)
    {
        String toBeSent = "Remove " + name;

        msg = new Message("BrokerApiInstance", toBeSent);

        byte[] bytes = m.marshal(msg);

        bytes = r.deliver_and_wait_feedback(DispAdd, bytes);
		
		Message answer = m.unmarshal(bytes);

        System.out.println(answer.data);
    }

    @Override
    public Address FindServer(String name)
    {
        String toBeSent = "Find " + name;

        msg = new Message("BrokerApiInstance", toBeSent);

        byte[] bytes = m.marshal(msg);

        bytes = r.deliver_and_wait_feedback(DispAdd, bytes);
		
		Message answer = m.unmarshal(bytes);

        int separator = answer.data.indexOf(':');

        String address = answer.data.substring(0,separator);
        int port =Integer.valueOf(answer.data.substring(separator + 1));

        Address dest = new Entry(address, port);

        return dest;
    
    }
}
