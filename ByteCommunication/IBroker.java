import Commons.Address;

public interface  IBroker 
{
    void AddServer(String name, String Ip, int port);
    Address GetDispacher();
    void RemoveServer(String name);
    Address FindServer(String name);
}
