import MessageMarshaller.Message;
import java.lang.reflect.Method;

public class GeneralServerProxy extends MessageServer 
{
    private final Object origin;
    private final String serverName;

    public GeneralServerProxy(Object origin, String serverName) 
    {
        this.origin = origin;
        this.serverName = serverName;
    }

    @Override
    public Message get_answer(Message msg) 
    {
        System.out.println(serverName + " received " + msg.data + " from " + msg.sender);

        String data = msg.data;
        Message answer = new Message(serverName, "null");

        try 
        {
            String[] parts = data.split(" ");
            String methodName = parts[0];
            String[] argStrings = new String[parts.length - 1];
            System.arraycopy(parts, 1, argStrings, 0, argStrings.length);

            Method targetMethod = null;
            for (Method method : origin.getClass().getDeclaredMethods()) 
            {
                if (method.getName().equals(methodName) && method.getParameterCount() == argStrings.length) 
                {
                    targetMethod = method;
                    break;
                }
            }

            if (targetMethod == null) 
            {
                return new Message(serverName, "Method not found");
            }

            Class<?>[] paramTypes = targetMethod.getParameterTypes();
            Object[] convertedArgs = new Object[argStrings.length];

            for (int i = 0; i < argStrings.length; i++) 
            {
                convertedArgs[i] = convertArgument(argStrings[i], paramTypes[i]);
            }

            Object result = targetMethod.invoke(origin, convertedArgs);

            answer = new Message(serverName, result != null ? result.toString() : "null");
        } catch (Exception e) 
        {
            answer = new Message(serverName, "Error: " + e.getMessage());
        }

        return answer;
    }

    private Object convertArgument(String arg, Class<?> type) 
    {
        if (type == int.class || type == Integer.class) return Integer.parseInt(arg);
        if (type == float.class || type == Float.class) return Float.parseFloat(arg);
        if (type == double.class || type == Double.class) return Double.parseDouble(arg);
        if (type == long.class || type == Long.class) return Long.parseLong(arg);
        if (type == boolean.class || type == Boolean.class) return Boolean.parseBoolean(arg);
        if (type == String.class) return arg;
        throw new IllegalArgumentException("Unsupported parameter type: " + type.getName());
    }
}
