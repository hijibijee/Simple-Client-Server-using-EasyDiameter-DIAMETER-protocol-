import com.easydiameter.application.DiameterApplication;
import com.easydiameter.application.DiameterStack;
import com.easydiameter.application.client.EasyDiameterClient;
import com.easydiameter.exception.DiameterDictionaryException;
import com.easydiameter.exception.DiameterParseException;
import com.easydiameter.packet.message.DiameterMessage;
import com.easydiameter.packet.message.factory.DiameterMessageFactory;
import com.easydiameter.util.ProtocolDefinitions;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// This is a very basic example of DiameterApplication, implement it as you need
public class Client implements DiameterApplication, ProtocolDefinitions, ExtractData {
    static private boolean authenticated = false;

    @Override
    public void receiveMessage(DiameterStack stack, DiameterMessage message) {
        System.out.println("Client got the answer...");

        StringBuilder sb = new StringBuilder();
        message.printContent(sb);
        System.out.println("Answer from server:");
        System.out.println(sb.toString());

        Map<Object, Object> data = getData(message);
        if(Integer.parseInt((String) data.get("Acct-Interim-Interval")) < maxTime){
            authenticated = true;
            System.out.println("Time usage: " + Integer.parseInt((String) data.get("Acct-Interim-Interval")));
        }
        else{
            authenticated = false;
            System.out.println("Invalid user-name/password.");
        }
    }

    @Override
    public void onConnectionSuccess(String localAddressStr, int localPort, String hostAddress, int remotePort) {
        System.out.println("Client: \"Bingo!!\"");
    }

    @Override
    public void onConnectionFail(String hostAddress, int remotePort) {
        System.out.println("Client: Connection failed :(");
    }

    @Override
    public void onSendMessage() {
        System.out.println("Client requesting...");
    }

    @Override
    public void onDisconnect(int i) {
        System.out.println("Client disconnected.");
    }

    public static void main(String[] args) throws DiameterDictionaryException, IOException {
        Scanner sc = new Scanner(System.in);

        DiameterApplication clientApp = new Client();
        EasyDiameterClient client = new EasyDiameterClient(clientApp, "127.0.0.1", 3868);
        client.bindToLocalAddress("127.0.0.1", 7777);

        //while(!authenticated) {
            //Creating message
            System.out.print("User Name: ");
            String userName = sc.next();

            System.out.print("Password: ");
            String password = sc.next();

            DiameterMessage request = DiameterMessageFactory.createMessage(HEADER_FLAG_R_P, COMMAND_CER_CEA, APP_ID_DIAMETER_BASE_ACCOUNTING);
            request.addAVPFromDictionary(AC_USER_NAME, VENDOR_ID_NONE, userName);
            request.addAVPFromDictionary(AC_USER_PASSWORD, VENDOR_ID_NONE, password);

            try {
                client.send(request.encodePacket()); // Sending message to the server
            } catch (IOException e) {
                e.printStackTrace();
            }
        //}
    }
}
