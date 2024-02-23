package kr.co.ubcn.rm.util;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class StringUtils {
	
	 private static final String PropertyFile="config/application.properties";
	 
	 public static String getConfigProp(String name) {
	    	
	    	String value=null;
	        try {
	        	FileReader resources = new FileReader(PropertyFile);
	            Properties prop = new Properties();
	        	prop.load(resources);
	            value = prop.getProperty(name);
	            
	        }catch(IOException e) {
	        	e.printStackTrace();
	        }
	        
	        if (value == null || value.trim().length() == 0) {
	        	//System.out.println("data ����");
	            return null;
	        } else {
	            return value;
	        }
	 }
	 
	 /**
	  * Spring 암호화 //properties 에서는 ENC(인코딩데이타) 로 사용
	  * @param data
	  */
	 public static void createEncData(String data) {
		 StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();  
		 encryptor.setAlgorithm("PBEWITHMD5ANDDES");  
		 encryptor.setPassword("!ubcn7880@");  
		 String encryptedPass = encryptor.encrypt(data);
		 String decryptedPass = encryptor.decrypt(encryptedPass);
		 System.out.println("Encrypted Password for admin is : "+encryptedPass);  
		 System.out.println("Decrypted Password for admin is : "+decryptedPass);
	 }
	 
	 public static void main(String[] args) {
		 createEncData("vanon"); //+ls5MG9ue4QLA8jPb7LmaA==
		 createEncData("vanon338");//4Gno7Wt329gyMZSLM783Kqbo1jGyb4yV
		 
		 createEncData("vmmsuser"); //MDePWGtjg9yUebuD4ytw5stjng70Hsvi
		 createEncData("ubcnvmmsuser"); //+f9VYYdewdG1y5jvU30N5+HScZC/zXjv
	 }
	 
	  /**
		 * exception to String
		 * @param e
		 * @return
		 */
	 public static String getPrintStackTrace(Exception e) {
	        
	        StringWriter errors = new StringWriter();
	        e.printStackTrace(new PrintWriter(errors));
	         
	        return errors.toString();         
	 }

}
