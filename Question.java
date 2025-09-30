import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    public final String id;       // gerado determinísticamente
    public final String topic;    // tema da pergunta
    public final String text;     // enunciado
    public final List<String> options; // alternativas
    public final int correct;     // índice da correta

    public Question(String topic, String text, List<String> options, int correct) {
        this.id = generateId(topic, text, options, correct);
        this.topic = topic;
        this.text = text;
        this.options = List.copyOf(options);
        this.correct = correct;
    }

    private static String generateId(String topic, String text, List<String> options, int correct) {
        String key = topic + "|" + text + "|" + String.join(";", options) + "|" + correct;
        return Integer.toHexString(key.hashCode());
    }
}