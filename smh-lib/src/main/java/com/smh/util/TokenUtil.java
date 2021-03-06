package com.smh.util;

import java.util.Random;

public final class TokenUtil {
  
  
  /**
   * This method generate the token value based on length
   * 
   * @param length
   * @return String
   */
  public static String generateToken(int length) {
    String finalRandString = "";
    Random randomObj = new Random();
    for(int j = 0; j < length; j++) {
      int rand_int = randomObj.nextInt(72);
      finalRandString += rand_int;
      if(finalRandString.length() >= length) {
        finalRandString = finalRandString.substring(0, length);
        break;
      }
    }
    return finalRandString;
  }

}
