import java.io.*;
import java.net.*;
import java.util.*;

public class QuizClient {
    private final String host;
    private final int port;

    public QuizClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @SuppressWarnings("unchecked")
    public List<Question> fetchQuestions() throws IOException, ClassNotFoundException {
        try (
            Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject("LIST");
            return (List<Question>) in.readObject();
        }
    }

    public void syncWithPeer(String peerHost, int peerPort) throws IOException, ClassNotFoundException {
        QuizClient peerClient = new QuizClient(peerHost, peerPort);
        List<Question> peerQuestions = peerClient.fetchQuestions();

        try (
            Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            out.writeObject("SYNC");
            out.writeObject(peerQuestions);
            System.out.println("Resposta do servidor: " + in.readObject());
        }
    }

    public void play() throws IOException, ClassNotFoundException {
        List<Question> questions = fetchQuestions();
        Scanner scanner = new Scanner(System.in);

        for (Question q : questions) {
            System.out.println("\n[TEMA: " + q.topic + "]");
            System.out.println(q.text);
            for (int i = 0; i < q.options.size(); i++) {
                System.out.println((i + 1) + ". " + q.options.get(i));
            }
            System.out.print("Sua resposta: ");
            int answer = scanner.nextInt() - 1;

            if (answer == q.correct) {
                System.out.println("✅ Correto!");
            } else {
                System.out.println("❌ Errado! Resposta certa: " + q.options.get(q.correct));
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 12345;

        QuizClient client = new QuizClient(host, port);

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- MENU ---");
            System.out.println("1. Jogar");
            System.out.println("2. Sincronizar com peer");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            int op = sc.nextInt();

            if (op == 1) {
                client.play();
            } else if (op == 2) {
                System.out.print("Host do peer: ");
                String peerHost = sc.next();
                System.out.print("Porta do peer: ");
                int peerPort = sc.nextInt();
                client.syncWithPeer(peerHost, peerPort);
            } else {
                break;
            }
        }
    }
}