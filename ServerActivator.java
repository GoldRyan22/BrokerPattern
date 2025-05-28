import Commons.Address;
import MessageMarshaller.Message;
import Registry.Entry;
import RequestReply.ByteStreamTransformer;
import RequestReply.Replyer;
import java.util.HashMap;
import java.util.Map;

class ActivatorMessageServer extends MessageServer 
{
    private final ServerActivator activator = new ServerActivator();

    @Override
    public Message get_answer(Message msg) 
    {
        String data = msg.data;
        if (data.startsWith("Start")) 
        {
            String serverName = data.substring(6);
            String port = activator.activateServer(serverName);
            return new Message("Activator", port);

        } else if (data.startsWith("Stop")) 
        {
            String serverName = data.substring(5);
            boolean success = activator.deactivateServer(serverName);
            return new Message("Activator", success ? "OK" : "Failed");
        }
        return new Message("Activator", "Invalid Command");
    }
}


public class ServerActivator 
{
    private final Map<String, Process> serverProcesses = new HashMap<>();
    private final Map<String, Integer> serverPorts = new HashMap<>();

    public String activateServer(String serverName) 
    {
        if (serverProcesses.containsKey(serverName)) 
        {
            return "" + serverPorts.get(serverName);
        }

        int port = getFreePort();

        //String actualPath = serverName + ".java"; // can change later


        try {
            ProcessBuilder pb = new ProcessBuilder(
                "java", 
                serverName,
                String.valueOf(port)
            );

            pb.inheritIO(); // show server output in console
            Process process = pb.start();

            serverProcesses.put(serverName, process);
            serverPorts.put(serverName, port);

            System.out.println("Activated server: " + serverName + " on port " + port);
            Thread.sleep(100);
            return "" + port;

        } catch (Exception e) 
        {
            e.printStackTrace();
            return "null";
        }
    }

    public boolean deactivateServer(String serverName) 
    {
        Process proc = serverProcesses.get(serverName);
        if (proc != null) 
        {
            proc.destroy(); // sends SIGTERM
            serverProcesses.remove(serverName);
            serverPorts.remove(serverName);
            System.out.println("Deactivated server: " + serverName);
            return true;
        }
        return false;
    }

    private int getFreePort() 
    {
        return 2222;
    }

    public static void main(String[] args) 
    {

        BrokerApi broker = new BrokerApi();

        broker.AddServer("MathServer", "127.0.0.1", 2220);

        ByteStreamTransformer transformer = new ServerTransformer(new ActivatorMessageServer());

        Address myAddr = new Entry("127.0.0.1", 2220);

        Replyer r = new Replyer("Dispacher", myAddr);

        while (true) 
        {
		  r.receive_transform_and_send_feedback(transformer);
		}
        
    }
}
