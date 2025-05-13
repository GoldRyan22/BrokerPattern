import MessageMarshaller.*;
import RequestReply.*;


public class ServerTransformer implements ByteStreamTransformer
{
	private MessageServer originalServer;

	public ServerTransformer(MessageServer s)
	{
		originalServer = s;
	}

	public byte[] transform(byte[] in)
	{
		Message msg;
		Marshaller m = new Marshaller();
		msg = m.unmarshal(in);

		Message answer = originalServer.get_answer(msg);

		byte[] bytes = m.marshal(answer);
		return bytes;

	}
}