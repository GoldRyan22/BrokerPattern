import Commons.Address;
import MessageMarshaller.Marshaller;
import MessageMarshaller.Message;
import RequestReply.Requestor;


class InfoClientProxy implements InfoServiceInterface, IBroker
{
    Address dest;
    Address dispAddress;
    Message msg;
    Requestor r;
    Marshaller m;

    private IBroker broker;

    public InfoClientProxy() 
    {

        broker = new BrokerApi();
        dispAddress = broker.GetDispacher();

        m = new Marshaller();
        r = new Requestor("InfoClient");
    }

    @Override
    public void AddServer(String name, String Ip, int port)
    {
        broker.AddServer(name, Ip, port);
    }

    @Override
    public Address GetDispacher()
    {
        return broker.GetDispacher();
    }

    @Override
    public void RemoveServer(String name)
    {
        broker.RemoveServer(name);
    }

    public void SetDest(Address address)
    {
        this.dest = address;
    }

    @Override
    public Address FindServer(String name)
    { 
        Address result = broker.FindServer(name);
        SetDest(dest);

        return result;
    }

    @Override
    public String getCityTemp(String City)
    {
        msg = new Message("Client", "Temp " + City);

        byte[] bytes = m.marshal(msg);

        bytes = r.deliver_and_wait_feedback(dest, bytes);
		
		Message answer = m.unmarshal(bytes);

		String result = ("Client received message "+answer.data+" from "+answer.sender);

        System.out.println(result);

        return result;
    }

    @Override
    public String getRoadInfo(Integer Road)
    {
        msg = new Message("Client", "Road " + Road);

        byte[] bytes = m.marshal(msg);

        bytes = r.deliver_and_wait_feedback(dest, bytes);
		
		Message answer = m.unmarshal(bytes);

		String result = ("Client received message "+answer.data+" from "+answer.sender);

        System.out.println(result);

        return result;
    }
}

public class InfoClient 
{
    InfoClientProxy proxy;

    public InfoClient(InfoClientProxy proxy) 
    {
        this.proxy = proxy;
    }  

    public InfoClient() {
    }


    public static void main(String[] args) 
    {
        InfoClientProxy proxy = new InfoClientProxy();

        InfoClient client = new InfoClient(proxy);

        Integer Road = 123;

        client.proxy.SetDest(client.proxy.FindServer("InfoServer"));


        System.out.println(client.proxy.dest.dest() + client.proxy.dest.port());

        client.proxy.getCityTemp("Arad");
        client.proxy.getRoadInfo(Road);
        
    }
}
