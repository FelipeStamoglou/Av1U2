import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class QuizServer {
    private final int port;
    private final List<Question> questionBank = new CopyOnWriteArrayList<>();

    public QuizServer(int port) {
        this.port = port;
        loadSampleQuestions(); // carrega algumas perguntas iniciais
    }

    private void loadSampleQuestions() {
        questionBank.add(new Question(
            "Java",
            "Qual palavra-chave é usada para herança em Java?",
            Arrays.asList("implements", "extends", "inherits", "instanceof"),
            1
        ));
        questionBank.add(new Question(
            "Redes",
            "Qual protocolo é usado para enviar e-mails?",
            Arrays.asList("HTTP", "SMTP", "FTP", "SNMP"),
            1
        ));
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor rodando na porta " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
    }

    private void handleClient(Socket socket) {
        try (
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
        ) {
            String command = (String) in.readObject();

            switch (command) {
                case "LIST":
                    out.writeObject(new ArrayList<>(questionBank));
                    break;
                case "SYNC":
                    List<Question> peerQuestions = (List<Question>) in.readObject();
                    mergeQuestions(peerQuestions);
                    out.writeObject("OK");
                    break;
                default:
                    out.writeObject("Comando inválido");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mergeQuestions(List<Question> newQuestions) {
        Set<String> existingIds = new HashSet<>();
        for (Question q : questionBank) {
            existingIds.add(q.id);
        }

        for (Question q : newQuestions) {
            if (!existingIds.contains(q.id)) {
                questionBank.add(q);
                System.out.println("Nova pergunta importada: " + q.text);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 12345;
        QuizServer server = new QuizServer(port);
        server.start();
    }
}