import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.Wave;

public class DemoMain {

    public static void main(String[] args)
    {
        Wave wave = new Wave("./music/annunciatemi.wav");
        byte[] fing1 = wave.getFingerprint();
        byte[] fing2 = wave.getFingerprint();
        FingerprintSimilarityComputer computer = new FingerprintSimilarityComputer(fing1, fing2);
        float similarity = computer.getFingerprintsSimilarity().getSimilarity();

        System.out.println(similarity);
    }
}
