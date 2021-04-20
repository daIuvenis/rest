import java.util.Arrays;
import java.util.Random;

public class RandomForTest {

public static String randomName(){
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String randomName = "";
        int length = 5;

        java.util.Random random = new java.util.Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(random.nextInt(characters.length()));

        }
        for (int i = 0; i < length; i++) {
            randomName += (text[i]);
        }
        return randomName;
}
public static final Random r = new Random();
        static final String RANDOM_NAME = r.ints(48, 122)
                        .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                        .mapToObj(i -> (char) i)
                        .limit(16)
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                        .toString();

}