package services;

import java.util.Random;

public class HashGenerator {
    public static String generateHash() {
        int LENGTH = 10;
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
     
        String hash = random.ints(leftLimit, rightLimit + 1)
          .limit(LENGTH)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();
     
        return hash;
      }
}
