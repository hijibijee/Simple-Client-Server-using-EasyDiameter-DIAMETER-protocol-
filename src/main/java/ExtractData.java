import com.easydiameter.packet.message.DiameterMessage;

import java.util.HashMap;

public interface ExtractData {
    public static final int maxTime = 1000000;
    default HashMap<Object, Object> getData(DiameterMessage msg){
        StringBuilder sb = new StringBuilder();
        msg.printContent(sb);

        String content = new String(sb);
        String [] lines = content.split("\n");

        HashMap<Object, Object> data = new HashMap<>();
        for(int i = 1; i < lines.length; i++){
            lines[i] = lines[i].strip();
            String [] val = lines[i].split( " ");
            data.put(val[0], val[2]);
        }

//        data.forEach((key, value) -> {
//            System.out.println(key.toString() + "    " + value.toString());
//        });

        return data;
    }
}
