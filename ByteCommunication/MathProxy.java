import MessageMarshaller.Message;

public class MathProxy extends MessageServer implements MathServiceInterface 
{
    private MathServerOrig math;

    IBroker broker;

    public MathProxy(MathServerOrig math) 
    {
        this.math = math;
    }

    @Override
    public Message get_answer(Message msg) 
    {
        System.out.println("MathServer received " + msg.data + " from " + msg.sender);
        String data = msg.data;
        Message answer = new Message("MathServer", "null");

        if (data.startsWith("Add")) 
        {
            String[] parts = data.substring(4).split(" ");
            float a = Float.parseFloat(parts[0]);
            float b = Float.parseFloat(parts[1]);

            float result = math.add(a, b);
            answer = new Message("MathServer", Float.toString(result));
            
        } else if (data.startsWith("Sqrt")) 
        {
            float a = Float.parseFloat(data.substring(5));
            float result = math.doSqrt(a);
            answer = new Message("MathServer", Float.toString(result));
        }

        return answer;
    }

    @Override
    public float add(float a, float b) 
    {
        return math.add(a, b);
    }

    @Override
    public float doSqrt(float a) 
    {
        return math.doSqrt(a);
    }
}
