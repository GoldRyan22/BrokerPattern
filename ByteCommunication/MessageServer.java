import MessageMarshaller.*;

public class MessageServer 
{
	public Message get_answer(Message msg)
	{
		System.out.println("Server received " + msg.data + " from " + msg.sender);
		Message answer = new Message("Server", "I am alive");
		return answer;
	}
}
