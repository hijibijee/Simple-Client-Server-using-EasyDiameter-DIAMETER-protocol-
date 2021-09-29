import com.easydiameter.application.DiameterApplication;
import com.easydiameter.application.DiameterStack;
import com.easydiameter.application.server.EasyDiameterServer;
import com.easydiameter.exception.DiameterDictionaryException;
import com.easydiameter.packet.message.DiameterMessage;
import com.easydiameter.packet.message.factory.DiameterMessageFactory;
import com.easydiameter.util.ProtocolDefinitions;

import java.util.HashMap;
import java.util.Map;

// This is a very basic example of DiameterApplication, implement it as you need
public class Server implements DiameterApplication, ProtocolDefinitions, ExtractData {
    HashMap<String, String> userName_pass;
    HashMap<String, Integer> userName_time;

    Server(){
        userName_pass = new HashMap<>();
        userName_time = new HashMap<>();

        userName_pass.put("hijibijee", "lereve30percent");
        userName_time.put("hijibijee", 71099);
    }

    @Override
    public void receiveMessage(DiameterStack stack, DiameterMessage message) {
        System.out.println("Server got the request...");

        Map<Object, Object> data = getData(message);
        int time = maxTime;
        String userName = (String) data.get("User-Name");
        String inputPass = (String) data.get("User-Password");

        System.out.println("Received: " + userName + " " + inputPass);

        if(userName_pass.get(userName) != null && userName_pass.get(userName).equals(inputPass)){
            time = userName_time.get(userName);
        }
        // Creating message to sent
        DiameterMessage answer = DiameterMessageFactory.createMessage(HEADER_FLAG_NONE, COMMAND_CER_CEA, APP_ID_DIAMETER_BASE_ACCOUNTING);

        try {
            answer.addAVPFromDictionary(AC_USER_NAME, VENDOR_ID_NONE, userName);
            answer.addInteger32AVP(AC_ACCT_INTERIM_INTERVAL, (byte) 0, VENDOR_ID_NONE, time);
        } catch (DiameterDictionaryException e) {
            e.printStackTrace();
        }

        stack.sendMessage(answer.encodePacket());
    }

    @Override
    public void onConnectionSuccess(String localAddressStr, int localPort, String hostAddress, int remotePort) {
        System.out.println("Server: \"Bingo!!\"");
    }

    @Override
    public void onConnectionFail(String hostAddress, int remotePort) {
        System.out.println("Server: connection failed :(");
    }

    @Override
    public void onSendMessage() {
        System.out.println("Sending answer to the client...");
    }

    @Override
    public void onDisconnect(int i) {
        System.out.println("Server Disconnected");
    }

    public static void main(String[] args) {
        DiameterApplication serverApp = new Server();
        EasyDiameterServer server = new EasyDiameterServer(serverApp, "127.0.0.1", 3868); // Local Address to bind
        server.start(); // Start Server
        System.out.println("Server started...");
    }
}
