package group339.cyconnect;

/**
 * Created by apple on 11/30/15.
 */
public class Password {

    public static String encryptPassword(String password) {
        String key = "CyConnect";
        StringBuilder sb = new StringBuilder();
        char[] passwordArr = password.toCharArray();
        int j = 0;
        for(int i = 0; i < password.length(); i++) {
            sb.append((char) (key.charAt(j) ^ passwordArr[i]));
            j++;
            if (j == key.length() - 1) {
                j = 0;
            }
        }
        password = sb.toString();
        return password;
    }
}
