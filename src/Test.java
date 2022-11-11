import application.Client;

public class Test {

    public static void main(String[] args) {
        Client client1 = new Client();
        client1.launch(args);

        Client client2 = new Client();
        client2.launch(args);
    }
}
