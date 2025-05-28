import Commons.Address;
import MessageMarshaller.*;
import RequestReply.Requestor;
import java.lang.reflect.*;

public class ClientProxyFactory 
{
    public static <T> T createProxy(Class<T> interfaceClass, String serverName, BrokerApi broker) 
    {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),new Class[]{interfaceClass},
            new ClientPrInvocationHandler(serverName, broker)
        );
    }
}


class ClientPrInvocationHandler implements InvocationHandler 
{

    private final Address dest;
    private final Requestor r;
    private final Marshaller m;
    private final String clientName;

    public ClientPrInvocationHandler(String serverName, BrokerApi broker) 
    {
        this.r = new Requestor(serverName + "Client");
        this.m = new Marshaller();
        this.clientName = serverName + "Client";

        //Address dispatcher = broker.GetDispacher();
        this.dest = broker.FindServer(serverName);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
    {
        StringBuilder req = new StringBuilder();
        req.append(method.getName());
        if (args != null) 
        {
            for (Object arg : args) 
            {
                req.append(" ").append(arg.toString());
            }
        }

        Message msg = new Message(clientName, req.toString());
        byte[] response = r.deliver_and_wait_feedback(dest, m.marshal(msg));
        Message answer = m.unmarshal(response);

        String result = answer.data;

        
        Class<?> returnType = method.getReturnType();

        if (returnType == void.class) return null;
        if (returnType == int.class) return Integer.parseInt(result);
        if (returnType == float.class) return Float.parseFloat(result);
        if (returnType == double.class) return Double.parseDouble(result);
        if (returnType == long.class) return Long.parseLong(result);
        if (returnType == boolean.class) return Boolean.parseBoolean(result);
        if (returnType == String.class) return result;

        throw new IllegalArgumentException("Unsupported return type: " + returnType);
    }
}

