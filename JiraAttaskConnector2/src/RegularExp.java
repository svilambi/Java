
import java.util.regex.Matcher;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;


import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.naming.AuthenticationException;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

public class RegularExp {
	
	private static String BASE_URL = "https://amphorainc.atlassian.net";
	public static void main(String[] args) throws IOException {
	
		////jira
		Properties prop = new Properties();
		prop.load(FinalProject.class.getClassLoader().getResourceAsStream("conf.properties"));
		String up = prop.getProperty("unmpwd");		
		String auth = new String(Base64.encode(up));
		String uname = up.split(":")[0];
		String dateString = new Date().toString().replaceAll(" ", "_"); 
		dateString = dateString.replaceAll(":", "_");
		///jira 
		
		
   
  
      String pan = "panose-1";
      String ADBDR = "ADBDR";
      String changeSet;
      String user;
      String regex = "(\\d{6,9})";
      //String regex2 = "(issue.*\\d{6,9})";
      String regex2 = "\\[I#(.*?)\\]";
      String regex3 = "(\\d{6,9})";
      String regex4 = "(\\w+\\-\\d{1,})";
      String regex5= "\\\\([A-Za-z])*";
      Pattern issuePattern = Pattern.compile(regex);
      Pattern issuePattern2 = Pattern.compile(regex2);
      Pattern issuePattern3 = Pattern.compile(regex3);
      Pattern issuePattern4 = Pattern.compile(regex4);
      Pattern issuePattern5 = Pattern.compile(regex5);
      
     
            	    	 ////this is crucial part
            	    	 String testing="";
            	    	 String assigneeName="";
            	        	String reporterName="";
            	        	JSONObject assignee=null;
            	        	JSONObject reporter=null;
            	        	String assigneeData="";
            	        	JSONObject issueData=null;
            	        	JSONObject issueFields=null;
            	       	 String str="ADSO-7672";
            	        	///
            	      	JSONObject issueDatalnk=null;
            	        	JSONObject issueFieldslnk=null;
            	        	String[] linkedIssues= new String[10];
            	        	
            	     
            	        	for(int i=0;i<args.length;i++){
            	        	
            	        	try{
            	        		String issuelnk = invokeGetMethod(auth, BASE_URL+"/rest/api/2/issue/"+args[i]+"?fields=issuelinks");
            	        		
            	        		System.out.println("********"+issuelnk);
            	        		
            	        		System.out.println("&&&&&&&&&&&&&&&&&&&&-------------------&&&&&&&&&&&&&&&&&&&&");
            	        		
            	        		/*issueDatalnk = new JSONObject(issuelnk);
            	        		issueFieldslnk = issueDatalnk.getJSONObject("fields");
            	        		
            	        		System.out.println("@@@@@@@@@@"+issueFieldslnk.toString());
            	        		for(int t=0;t<=10;t++){
            						linkedIssues[t]=issueFieldslnk.getJSONObject("summary").getString("key");
            						System.out.println("####################################---------Printed-----------###########"+linkedIssues[t]);
            					}*/
            	        	}
            	        	catch(Exception e){
            	        		System.out.println("-------------no issues------------");
            	        	} 
            	        	}
            	        	
            	        	///
            	        	
	}
	private static String invokeGetMethod(String auth, String url)
			throws AuthenticationException, ClientHandlerException {
		// TODO Auto-generated method stub
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource
				.header("Authorization", "Basic " + auth)
				.type("application/json").accept("application/json")
				.get(ClientResponse.class);
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}
		return response.getEntity(String.class);
		
	}

}
