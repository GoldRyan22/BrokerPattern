import Commons.Address;
import MessageMarshaller.*;
import Registry.*;
import RequestReply.ByteStreamTransformer;
import RequestReply.Replyer;
import java.util.Hashtable;


class InfoMessageServer extends MessageServer
    {
        InfoServerData infoData;

        public InfoMessageServer(InfoServerData infoData ) 
        {
            this.infoData = infoData;
        }

        @Override
        public Message get_answer(Message msg)
        {
            System.out.println("InfoServer received " + msg.data + " from " + msg.sender);
            Message answer = new Message("InfoServer", "null");
            if(msg.data.startsWith("Temp"))
            {
                String find = msg.data.substring(5);

                String result ="" + infoData.cityTemp.get(find);

                answer = new Message("InfoServer", result);
            }
            else if(msg.data.startsWith("Road"))
            {
                String find = msg.data.substring(5);

                try 
                {
                    String ifFound = infoData.roadInfo.get(Integer.valueOf(find));
                    String result = "" + find + "is" + ifFound;
                    answer = new Message("InfoServer", result);
                    
                } catch (NumberFormatException e) 
                {
                    return answer = new Message("InfoServer", "null");
                }   
            }
            return answer;
        }
    }

class InfoServerData
{
    Address myAdd = new Entry("127.0.0.1", 1112);

    Hashtable<Integer, String> roadInfo = new Hashtable<>();
    Hashtable<String, Integer> cityTemp = new Hashtable<>();

    void addRoadInfo(Integer roadID, String context)
    {
        this.roadInfo.put(roadID, context);
    }

    void addCityTemp(String city, Integer temp)
    {
        this.cityTemp.put(city, temp);
    }

    String getRoadInfo(Integer roadID)
    {
        String result = "on road " + roadID + "is " + this.roadInfo.get(roadID);

        return result;
    }

    String getCityTemp(String cityName)
    {
        String result = "" + cityName + this.cityTemp.get(cityName);

        return result;
    }
    
}

public class InfoServer 
{
    public static void main(String[] args) 
    {
        new Configuration();

       InfoServerData InfoServ = new InfoServerData();

       InfoServ.addCityTemp("Arad", 15);
       InfoServ.addRoadInfo(123, "an accident");

       ByteStreamTransformer transformer = new ServerTransformer(new InfoMessageServer(InfoServ));

       Address myAddr = Registry.instance().get("InfoServer");
		
		Replyer r = new Replyer("InfoServer", myAddr);

		while (true) {
		  r.receive_transform_and_send_feedback(transformer);
		}
    }
    
}
