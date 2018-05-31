import java.io.IOException;
import java.security.KeyStore.Entry;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
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
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import com.jira.client.GetAllBuildTeamIssues;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;
import com.sun.mail.iap.Response;

public class FinalProject {

	private static String BASE_URL = "https://amphorainc.atlassian.net";

	public static void main(String[] args) throws IOException {
	
	Properties prop = new Properties();
	
/////jira	
	prop.load(FinalProject.class.getClassLoader().getResourceAsStream("conf.properties"));
	String up = prop.getProperty("unmpwd");		
	String auth = new String(Base64.encode(up));
	String uname = up.split(":")[0];
	String dateString = new Date().toString().replaceAll(" ", "_"); 
	dateString = dateString.replaceAll(":", "_");
/////jira	
	
		Properties props = new Properties();
	    props.setProperty("mail.store.protocol", "imaps");
	    String host="172.16.143.41";
		props.put("mail.smtp.host",host);  
        props.put("mail.smtp.auth", "false");
        
        StringBuilder sb=new StringBuilder();
        
	try{
		
		
	Session session = Session.getDefaultInstance(props);
	//session.setDebug(true);
    Store store = session.getStore();
    store.connect("outlook.office365.com", "aakella@amphorainc.com", "2923ujeT");
   // props.put("mail.smtp.port", "587");
    Folder inbox = store.getFolder("Inbox/FinalProject");
   // Folder inbox = store.getFolder("Inbox/Release_Notes");
   
    inbox.open(Folder.READ_ONLY);
   
    SearchTerm term = null;

    Calendar cal = null;
    cal = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    
    Date minDate = new Date(cal.getTimeInMillis());   //get today date
   // System.out.println("MIN Date---" + minDate);
    
    cal.add(Calendar.DAY_OF_MONTH, -1);                //add 1 day
    Date maxDate = new Date(cal.getTimeInMillis());   //get tomorrow date
   // System.out.println("MAX Date---" + maxDate);
    
    ReceivedDateTerm minDateTerm = new ReceivedDateTerm(ComparisonTerm.LE, minDate);
    ReceivedDateTerm maxDateTerm = new ReceivedDateTerm(ComparisonTerm.GE, maxDate);
    
   // System.out.println("MIN Date term---" + minDateTerm);
   // System.out.println("MAX Date term---" + maxDateTerm);

    term = new AndTerm(maxDateTerm, minDateTerm);            //concat the search terms

    //System.out.println("term---" + term);
    
    Message[] messages = inbox.search(term);
   

     // System.out.println("messages.length---" + messages.length);
      
      int n = messages.length;
      
      ////
      for (int i = n-1;  i >= 0; --i) {
          Message message = messages[i];
         
         // System.out.println("ReleaseNotes Email Number " + (i + 1));
          System.out.println();
          String mailContent = message.getContent().toString();
          System.out.println("-----------");
          System.out.println((i + 1)+". "+message.getSubject());
          System.out.println("-----------");
          
          Pattern p=Pattern.compile("\\[I(.*?)\\]");
         // Pattern p=Pattern.compile("Comment:\\s\\n\\s\\s\\[I#(.*?)\\]");
          
          Matcher m=p.matcher(mailContent);
         
      //   System.out.println(mailContent);
         /* while(m.find()){
        	  String str1=m.group(1);
          	String st1=str1.trim(); 
           	// System.out.println(m.group(1));
          	System.out.println("this is "+str1);
          	System.out.println("this is trimmed "+st1);
          }*/
          /*int indexNum=mailContent.indexOf("Changeset:");
          String chSet=mailContent.substring(indexNum+11, indexNum+16);
          System.out.println(chSet);*/
        //  String cs="";
          Pattern pp=Pattern.compile("Changeset: (\\d{5,5})");
          Matcher mm=pp.matcher(mailContent);
         /* while (mm.find()) {
        	String cs=mm.group(1);
          	System.out.println("********"+cs);
			
		}*/
          Pattern ppp=Pattern.compile("\\\\([A-Za-z])*");
          Matcher mmm=ppp.matcher(mailContent);
         /* while (mmm.find()) {
        	  String usr=mmm.group(0);
          	System.out.println("********"+usr);
			
		}*/
       //   
          String userN="";
        while(m.find()&&mm.find()&&mmm.find()){
        	
        	
        	String str=m.group(1);
        	String st=str.trim();
         	// System.out.println(m.group(1));
        	System.out.println("this is "+str);
        	System.out.println("this is trimmed "+st);
        	
        			 String cs=mm.group(1);
     	        	System.out.println("--------------"+cs);
        	
        			 String usr=mmm.group(0);
        	           	System.out.println("********"+usr);
        	           	int num=usr.indexOf(0);
        				userN=usr.substring(num+2);
        				System.out.println("^^^^^^^^^^^^^^"+userN);
        	 			
      
        	
        	String assigneeName="";
        	String reporterName="";
        	JSONObject assignee=null;
        	JSONObject reporter=null;
        	String assigneeData="";
        	JSONObject issueData=null;
        	JSONObject issueFields=null;
        	try{
        		String issue = invokeGetMethod(auth, BASE_URL+"/rest/api/2/issue/"+st+"?fields=assignee,reporter");
        		System.out.println("got issue "+issue);
        		
        		issueData = new JSONObject(issue);
				//String selfUrl = issueData.getString("self");
				issueFields = issueData.getJSONObject("fields");
				/*assigneeName = issueFields.getJSONObject("assignee").getString("name");
				reporterName = issueFields.getJSONObject("reporter").getString("name");
				System.out.println("to: "+assigneeName+"  from: "+reporterName);*/
				reporterName = issueFields.getJSONObject("reporter").getString("name");
				try{
					assigneeName = issueFields.getJSONObject("assignee").getString("name");
					}
				
			
				catch (Exception e) {
					// TODO: handle exception
				System.out.println("unassigned assignee");
				
					/*reporter = issueFields.getJSONObject("reporter");
					//assignee = issueFields.getJSONObject("assignee");
					assignee = reporter;
					assigneeData = "{\"name\":\""+reporterName+"\"}";
					System.out.println(assigneeData);
					invokePutMethod(auth, BASE_URL+"/rest/api/2/issue/"+st+"/assigneeData", assignee);*/
					}
				
				
				String editIssueData = "{\"body\": \"patched in "+ message.getSubject()+" and ChangeSet number is: "+cs+"\"}";
				String resp = invokePostMethod(auth, BASE_URL+"/rest/api/2/issue/"+st+"/comment", editIssueData);
				System.out.println("**************"+resp);
				
				String editIssueData2 = "{\"transition\": {\"id\": \"141\"}}";
				System.out.println(invokePostMethod(auth, BASE_URL+"/rest/api/2/issue/"+st+"/transitions", editIssueData2));
			////Important///////	
				/*String editIssueData2 = "{\"update\": {\"comment\": [{\"add\": {\"body\":\" patched in "+ message.getSubject()+"\"}}]},\"transition\": {\"id\": \"141\"}}";						
				System.out.println(invokePostMethod(auth, BASE_URL+"/rest/api/2/issue/"+st+"/transitions", editIssueData2));	*/
			/////Important//////
				
				//assignee = issueFields.getJSONObject("assignee");
				reporter = issueFields.getJSONObject("reporter");
				assignee = reporter;
				assigneeData = "{\"name\":\""+reporterName+"\"}";
				System.out.println(assigneeData);
				invokePutMethod(auth, BASE_URL+"/rest/api/2/issue/"+st+"/assignee", assigneeData);

        	}
        	/*catch (Exception msg) {
				System.out.println("not working...fucked off");
			}*/
        	catch (AuthenticationException e) {
    			System.out.println("Username or Password wrong!");
    			e.printStackTrace();
    		} catch (ClientHandlerException e) {
    			System.out.println("Error invoking REST method");
    			e.printStackTrace();
    		} catch (JSONException e) {
    			System.out.println("Invalid JSON output");

    			try{
    			   
    				if(userN!=""){
    			    Message msg=new MimeMessage(session);
    			    msg.setFrom(new InternetAddress("aakella@amphorainc.com"));
    			  
    			    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(userN+"@amphorainc.com"));
    			    	
    			    msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse("BuildTeam@amphorainc.com"));
    			    	msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse("dvirk@amphorainc.com"));
    			  
    			    	msg.setSubject("wrong in "+message.getSubject()+" patch check in issue number "+st);
    			    BodyPart messageBody=new MimeBodyPart();
    			    messageBody.setText("You might have given wrong Issue Number(or format of Issue number) as "+st+" for changeset "+cs+" which is not found in JIRA. Please correct.");
    			    Multipart multipart = new MimeMultipart();
    			    multipart.addBodyPart(messageBody);
    			   
    			      msg.setContent(multipart);

    			      
    			      Transport.send(msg);System.out.println("----------------mail sucess------------------");
    				}
    				else{
    					System.out.println("USER Name Not found. so, failed to send mail");
    				}
    			}
    			catch (Exception ee) {
					// TODO: handle exception
    				System.out.println("-------------------Mail Sent FAILED-------------------");
    			}
    			e.printStackTrace();
    		}catch(IllegalArgumentException e){
    			System.out.println("issue is wrong");
    			e.printStackTrace();
    			
    			try{
    			   
    				if(userN!=""){
    			    Message msg=new MimeMessage(session);
    			    msg.setFrom(new InternetAddress("aakella@amphorainc.com"));
    			  
    			    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(userN+"@amphorainc.com"));
    			    	
    			    msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse("BuildTeam@amphorainc.com"));
    			    	msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse("dvirk@amphorainc.com"));
    			  
    			    msg.setSubject("wrong in "+message.getSubject()+" patch check in issue number "+st);
    			    BodyPart messageBody=new MimeBodyPart();
    			    messageBody.setText("You might have given wrong Issue Number (or format of Issue number) as "+st+" for changeset "+cs+" which is not found in JIRA. Please correct.");
    			    Multipart multipart = new MimeMultipart();
    			    multipart.addBodyPart(messageBody);
    			   
    			      msg.setContent(multipart);

    			      
    			      Transport.send(msg);System.out.println("----------------mail sucess------------------");
    				}
    				else{
    					System.out.println("USER Name Not found. so, failed to send mail");
    				}
    			}
    			catch (Exception ee) {
					// TODO: handle exception
    				System.out.println("-------------------Mail Sent FAILED-------------------");
    			}
    			
    		}
        	catch (UniformInterfaceException e) {
				// TODO: handle exception
        		System.out.println("status and Comment updated Sucessfully");
        		e.printStackTrace();
        		
			}
      }
          
}
}
	 catch (Exception mex) {
	        mex.printStackTrace();
	    }
}

	private static String invokeGetMethod(String auth, String url)
			throws AuthenticationException, ClientHandlerException {
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

	private static String invokePostMethod(String auth, String url, String data)
			throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource
				.header("Authorization", "Basic " + auth)
				.type("application/json").accept("application/json")
				.post(ClientResponse.class, data);
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}

		return response.getEntity(String.class);
	}

	private static void invokePutMethod(String auth, String url, String data)
			throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource
				.header("Authorization", "Basic " + auth)
				.type("application/json").accept("application/json")
				.put(ClientResponse.class, data);
		int statusCode = response.getStatus();
		// System.out.println("status code: "+statusCode);
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		} else if (statusCode == 204) {
			throw new UniformInterfaceException(" updated", response);
		}
		System.out.println("response : " + response);
	}

}
