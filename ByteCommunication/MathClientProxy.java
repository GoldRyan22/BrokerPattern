import Commons.Address;
import MessageMarshaller.*;
import RequestReply.Requestor;

public class MathClientProxy implements MathServiceInterface, IBroker 
{
    Address dest;
    IBroker broker;
    Requestor r;
    Marshaller m;

    public MathClientProxy() 
    {
        this.broker = new BrokerApi();
        this.r = new Requestor("MathClient");
        this.m = new Marshaller();

        Address disp = broker.GetDispacher();
        this.dest = broker.FindServer("MathServer");
    }

    @Override
    public float add(float a, float b) 
    {
        String req = "Add " + a + " " + b;

        Message msg = new Message("MathClient", req);
        byte[] response = r.deliver_and_wait_feedback(dest, m.marshal(msg));
        Message answer = m.unmarshal(response);

        return Float.parseFloat(answer.data);
    }

    @Override
    public float doSqrt(float a) 
    {
        String req = "Sqrt " + a;

        Message msg = new Message("MathClient", req);
        byte[] response = r.deliver_and_wait_feedback(dest, m.marshal(msg));
        Message answer = m.unmarshal(response);
        
        return Float.parseFloat(answer.data);
    }

    @Override 
    public void AddServer(String name, String Ip, int port) 
    { 
        broker.AddServer(name, Ip, port); 
    }

    @Override 
    public void RemoveServer(String name) 
    { 
        broker.RemoveServer(name); 
    }
    @Override 
    public Address FindServer(String name) 
    { 
        return broker.FindServer(name); 
    }
    @Override 
    public Address GetDispacher() 
    { 
        return broker.GetDispacher(); 
    }
}
