import com.easydiameter.exception.DiameterDictionaryException;

import java.util.Scanner;

public class MultiClientTest {
    public static void main(String[] args) throws DiameterDictionaryException {
        Client1 client1 = new Client1();
        Client2 client2 = new Client2();

        String uName1, uName2, pass1, pass2;
        Scanner sc  = new Scanner(System.in);
        System.out.print("User name of client1: "); uName1 = sc.next();
        System.out.print("Password of client1: "); pass1 = sc.next();
        System.out.print("User name of client2: "); uName2 = sc.next();
        System.out.print("Password of client2: "); pass2 = sc.next();

        client1.connect();
        client2.connect();

        client1.getTime(uName1, pass1);
        client2.getTime(uName2, pass2);
    }
}
