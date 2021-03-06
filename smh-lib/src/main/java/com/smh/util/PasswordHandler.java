package com.smh.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Version : 1.0
 */
public final class PasswordHandler {

  /**
   * Hex decimal character array. Used in selecting the mapping for each four
   * binary bits
   */
  private static final char HEX[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  private static final int HEX_CHAR_LENGTH = 4;

  private static final String TEMP_SECURE_WORD = "@@PREMANPAID@@";

  private Pattern pattern;

  private Matcher matcher;

  private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@,£^*?€\"!$]).{8,16})";
  
  private static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  public PasswordHandler() {
    pattern = Pattern.compile(PASSWORD_PATTERN);
  }

  /**
   * Encrypt the password string using MD5 encryption and return the Hex decimal
   * format of it
   * 
   * @param password
   * @return: MD5 encrypted password in Hex decimal format.
   * @throws Exception
   */
  public static String encodePassword(String password) {
    try {

      String tempPassword = TEMP_SECURE_WORD + password + TEMP_SECURE_WORD;
      MessageDigest messageDigest = MessageDigest.getInstance("MD5");
      byte[] md5Binary = messageDigest.digest(tempPassword.getBytes());
      String hexParam = encodeHex(md5Binary);
      
      return hexParam.toUpperCase();
    }
    catch(NoSuchAlgorithmException e) {
    }
    return null;
  }

  /**
   * Encode the byte array of binary data to hex decimal format
   * 
   * @param data
   *          : byte array
   * @return: Hex decimal string representing the bytes
   */
  private static String encodeHex(byte data[]) {
    int datalength = data.length;
    // multiply by 2 as every byte will be represented by two characters
    char out[] = new char[datalength * 2];
    int j = 0;
    for(int i = 0; i < datalength; i++) {
      out[j++] = HEX[(0xf0 & data[i]) >>> HEX_CHAR_LENGTH];
      out[j++] = HEX[0xf & data[i]];
    }
    return new String(out);
  }

  /**
   * Method to get System Generated Password
   * 
   * @return
   */
  public static String getSystemGeneratedPassword(int length) {
    final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    final String SPECIAL_CHARS = "@!$";
    final String NUMBERS="0123456789";
    final String CAPS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    Random rnd = new Random();
    StringBuilder sb = new StringBuilder(length);
    for(int i = 0; i < length; i++) {
    	if (i == 3) {
			sb.append(SPECIAL_CHARS.charAt(rnd.nextInt(SPECIAL_CHARS.length())));
		} else if (i == 4) {
			sb.append(NUMBERS.charAt(rnd.nextInt(NUMBERS.length())));
		} else if (i == 5) {
			sb.append(CAPS.charAt(rnd.nextInt(NUMBERS.length())));
		} else {
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		}
    }
    return sb.toString();
  }
  
  /**
   *  Method to get System Generated SecurityKey
   * 
   * @param length
   * @return
   */
  public static String getSystemGeneratedSecurityKey(int length) {
	    final String NUMBERS="0123456789";
	    Random rnd = new Random();
	    StringBuilder sb = new StringBuilder(length);
	    for(int i = 0; i < length; i++) {
	    	sb.append(NUMBERS.charAt(rnd.nextInt(NUMBERS.length())));
	    }
	    return sb.toString();
	  }
  
  public static Long getSystemGeneratedOTP(int length) {
	    final String NUMBERS="0123456789";
	    Random rnd = new Random();
	    StringBuilder sb = new StringBuilder(length);
	    for(int i = 0; i < length; i++) {
	    	sb.append(NUMBERS.charAt(rnd.nextInt(NUMBERS.length())));
	    }
	    return Long.valueOf(sb.toString());
	  }
  /**
   * Validate password with regular expression
   * 
   * @param password
   *          password for validation
   * @return true valid password, false invalid password
   */
  public boolean validate(final String password) {

    matcher = pattern.matcher(password);
    return matcher.matches();

  }
  
  /**
   * Validate password with Spring BCrypt
   * 
 * @param rawPassword
 * @param hashedPassword
 * @return
 */
public static boolean isValidPassword(final String rawPassword, final String hashedPassword) {
    return bCryptPasswordEncoder.matches(rawPassword, hashedPassword);
  }

/**
 * Method to get Spring encrypted value of MD5 string
 * @param md5Password
 * @return
 */
public static String bCryptEncode(final String md5Password){
	return bCryptPasswordEncoder.encode(md5Password);
}
  public static void main(String[] args) {
	  String enCode = "Phfi@123";
    System.out.println(enCode);
   BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    String test = bCryptPasswordEncoder.encode(enCode);
    System.out.println(test);
    System.out.println(bCryptPasswordEncoder.matches("Asd@123", test));
  }

}
