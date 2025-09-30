import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class QuizServerPeer {

    private final int port;
    private final List<Question> questionBank = new CopyOnWriteArrayList<>();

    public QuizServerPeer(int port) {
        this.port = port;
        loadPeerQuestions();
    }

    private void loadPeerQuestions() {
        questionBank.add(new Question("Python", "Qual função imprime no console?",
                Arrays.asList("echo()", "print()", "console.log()", "write()"), 1));
        questionBank.add(new Question("Banco de Dados", "Qual comando cria uma tabela em SQL?",
                Arrays.asList("CREATE TABLE", "INSERT INTO", "SELECT", "DROP TABLE"), 0));
        questionBank.add(new Question("Curso", "Como eu vou ficar até terminar o curso?",
                Arrays.asList("Louco", "Calvo", "Ansioso", "Todas as anteriores"), 3));
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor Peer rodando na porta " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
    }

    @SuppressWarnings("unchecked")
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
        for (Question q : questionBank) existingIds.add(q.id);

        for (Question q : newQuestions) {
            if (!existingIds.contains(q.id)) {
                questionBank.add(q);
                System.out.println("Nova pergunta importada: " + q.text);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 12346;
        QuizServerPeer server = new QuizServerPeer(port);
        server.start();
    }
}
