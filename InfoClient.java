import Commons.Address;
import MessageMarshaller.Marshaller;
import MessageMarshaller.Message;
import Registry.Entry;
import Registry.Registry;
import RequestReply.Requestor;


interface InfoClientService
{
    String getTemp();
    String getRoad();
}

class InfoClientProxy implements InfoClientService
{
    Address dest;
    Address dispAddress;
    Message msg;
    Requestor r;
    Marshaller m;

    public InfoClientProxy() 
    {
        new Configuration();
        dispAddress=Registry.instance().get("Dispacher");

        msg= new Message("Client","InfoServer");

		r = new Requestor("Client");
		
		m = new Marshaller();

        byte[] bytes = m.marshal(msg);

		bytes = r.deliver_and_wait_feedback(dispAddress, bytes);
		
		Message answer = m.unmarshal(bytes);

		System.out.println("Client received message "+answer.data+" from "+answer.sender);

        int separator = answer.data.indexOf('|');

        String address = answer.data.substring(0,separator);
        int port =Integer.valueOf(answer.data.substring(separator + 1));

        dest = new Entry(address, port);
    }

    public String getTemp()
    {
        msg = new Message("Client", "Temp Arad");

        byte[] bytes = m.marshal(msg);

        bytes = r.deliver_and_wait_feedback(dest, bytes);
		
		Message answer = m.unmarshal(bytes);

		String result = ("Client received message "+answer.data+" from "+answer.sender);

        System.out.println(result);

        return result;
    }

    public String getRoad()
    {
        msg = new Message("Client", "Road 123");

        byte[] bytes = m.marshal(msg);

        bytes = r.deliver_and_wait_feedback(dest, bytes);
		
		Message answer = m.unmarshal(bytes);

		String result = ("Client received message "+answer.data+" from "+answer.sender);

        System.out.println(result);

        return result;

    }
}

public class InfoClient implements InfoClientService
{
    InfoClientProxy proxy;

    public InfoClient(InfoClientProxy proxy) 
    {
        this.proxy = proxy;
    }

    

    public InfoClient() {
    }

    @Override
    public String getRoad() 
    {
        
        return proxy.getRoad();
    }

    @Override
    public String getTemp() 
    {
        return proxy.getTemp();
    }

    public static void main(String[] args) 
    {
        InfoClientProxy proxy = new InfoClientProxy();

        InfoClient client = new InfoClient(proxy);

        client.getRoad();
        
    }
}
