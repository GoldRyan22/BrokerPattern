import Commons.Address;
import MessageMarshaller.*;
import Registry.*;
import RequestReply.*;

class NamingMessageServer extends MessageServer
{
    @Override
    public Message get_answer(Message msg)
    {
        System.out.println("Dispacher received " + msg.data + " from " + msg.sender);

        Message answer = new Message("Dispacher", "null");

        if(msg.data.startsWith("Add"))
        {
            String nameIpAndPort = msg.data.substring(4);
            
            int nameSeparator = nameIpAndPort.indexOf(' ');

            String key = nameIpAndPort.substring(0, nameSeparator);

            String ipAndPort = nameIpAndPort.substring(nameSeparator+1);

            int separator = ipAndPort.indexOf(':');

            String address = ipAndPort.substring(0,separator);
            int port =Integer.valueOf(ipAndPort.substring(separator + 1));
            
            Entry ent = new Entry(address, port);
            Registry.instance().put(key, ent);

            answer = new Message("Broker", "Added server " + key);
            System.out.println("added " + key);
        }

        if(msg.data.startsWith("Remove"))
        {
            String name = msg.data.substring(7);
            if(Registry.instance().remove(name))
            {
                answer = new Message("Broker", "removed server " + name);
            }
            else 
            {
                answer = new Message("Broker", "no such server " + name);
            }    
        }

        if(msg.data.startsWith("Find"))
        {
            String name = msg.data.substring(5);

            Entry serverAddress = Registry.instance().get(name);
            String result = "";

            if(serverAddress == null)
            {
                result = "null";
                answer = new Message("Dispacher", result);
                return answer;
            }

            /// talk with activator

            Requestor r = new Requestor("DispacherToAct");
            Marshaller m = new Marshaller();


            Message msgAct = new Message("MathClient", "Start " + name);
            byte[] responseAct = r.deliver_and_wait_feedback(serverAddress, m.marshal(msgAct));
            Message answerAct = m.unmarshal(responseAct);


            ///
        
            result = "" + serverAddress.dest() + ":" + answerAct.data;
            answer = new Message("Dispacher", result);

            System.out.println("dispacher returned :  " + result);
            return answer;
        }

        if(msg.data.startsWith("Stop"))
        {
            String name = msg.data.substring(5);
            String result = "";

            Entry serverAddress = Registry.instance().get(name);
            if(serverAddress == null)
            {
                result = "null"; 
                answer = new Message("Dispacher", result);
                return answer;
            }

             Requestor r = new Requestor("DispacherToAct");
            Marshaller m = new Marshaller();


            Message msgAct = new Message("MathClient", "Stop " + name);
            byte[] responseAct = r.deliver_and_wait_feedback(serverAddress, m.marshal(msgAct));
            Message answerAct = m.unmarshal(responseAct);

            result =  answerAct.data;
            answer = new Message("Dispacher", result);

            System.out.println("dispacher returned :  " + result);
            return answer;


        }

        return answer;
    }
}

public class BrokerAndDispacher 
{
    
    public static void main(String[] args) 
    {
        new Configuration();// hand added ip and port

		ByteStreamTransformer transformer = new ServerTransformer(new NamingMessageServer());
		
		Address myAddr = Registry.instance().get("Dispacher");
		
		Replyer r = new Replyer("Dispacher", myAddr);

		while (true) 
        {
		  r.receive_transform_and_send_feedback(transformer);
		}
    }
}
