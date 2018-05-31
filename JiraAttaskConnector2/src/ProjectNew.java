
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

public class ProjectNew {
	
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
		
		Properties props = new Properties();
	    props.setProperty("mail.store.protocol", "imaps");
	    String host="172.16.170.145";
		props.put("mail.smtp.host",host);  
        props.put("mail.smtp.auth", "false");
        String username="";
        String password="";
	try{
		
		//newely added for manual authentication to out look
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter your outlook user id: ");
		username = br.readLine();
		BufferedReader brr = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter your outlook password: ");
		password = brr.readLine();
		//newely added
		
		
	Session session = Session.getDefaultInstance(props);
    Store store = session.getStore();
    store.connect("outlook.office365.com", username+"@amphorainc.com", password);
    //store.connect("outlook.office365.com", "aakella@amphorainc.com", "2923Teju");
    //Folder inbox = store.getFolder("Inbox/Release_Notes");
   // Folder inbox = store.getFolder("Inbox/Test"); 
    Folder inbox = store.getFolder("Inbox/FinalProject");
    inbox.open(Folder.READ_ONLY);
    SearchTerm term = null;

    Calendar cal = null;
    cal = Calendar.getInstance();
    Date minDate = new Date(cal.getTimeInMillis());   //get today date
    System.out.println("MIN Date---" + minDate);
    
    cal.add(Calendar.DAY_OF_MONTH, -7);                //add 1 day
    Date maxDate = new Date(cal.getTimeInMillis());   //get tomorrow date    
    System.out.println("MAX Date---" + maxDate);
    
    ReceivedDateTerm minDateTerm = new ReceivedDateTerm(ComparisonTerm.LE, minDate);
    ReceivedDateTerm maxDateTerm = new ReceivedDateTerm(ComparisonTerm.GE, maxDate);
    
    System.out.println("MIN Date term---" + minDateTerm);
    System.out.println("MAX Date term---" + maxDateTerm);

    term = new AndTerm(maxDateTerm, minDateTerm);            //concat the search terms    
   // term = new AndTerm(term, maxDateTerm);
    System.out.println("term---" + term);
    
    Message[] messages = inbox.search(term);
    
    //  Message[] messages = inbox.getMessages();
      System.out.println("messages.length---" + messages.length);
      
      int n = messages.length;
      String pan = "panose-1";
      String ADBDR = "ADBDR";
      String changeSet;
      String user;
      String regex = "(\\d{6,9})";
      //String regex2 = "(issue.*\\d{6,9})";
     // String regex2 = "\\[I#(.*?)\\]";
      String regex2 = "ADSO\\-\\d{3,4}";
      String regex3 = "(\\d{6,9})";
      String regex4 = "(\\w+\\-\\d{1,})";
      String regex5= "\\\\([A-Za-z])*";
      Pattern issuePattern = Pattern.compile(regex);
      Pattern issuePattern2 = Pattern.compile(regex2);
      Pattern issuePattern3 = Pattern.compile(regex3);
      Pattern issuePattern4 = Pattern.compile(regex4);
      Pattern issuePattern5 = Pattern.compile(regex5);
      
      HashMap<String, ArrayList<String>> issueMap = new HashMap<String, ArrayList<String>>();

      for (int i = n-1;  i >= 0; --i) {
         Message message = messages[i];
         System.out.println("---------------------------------");
         System.out.println("Email Number " + (i + 1));
         System.out.println("Subject: " + message.getSubject());
         System.out.println("From: " + message.getFrom()[0]);
         //System.out.println("Text: " + message.getContent().toString());
         String mailContent = message.getContent().toString();
         String mailLines[] = mailContent.split("-------");
         System.out.println("messageline.leangth: " + mailLines.length);
         for (int j = 0; j < mailLines.length; j++) {
        	 //System.out.println("mailLines: " + mailLines[j]);
        	 ArrayList<String> issuesList = new ArrayList<String>();
        	 System.out.println("**************************END*****************************");
        	 int index = mailLines[j].indexOf("Changeset:");
        	 int userIndex = mailLines[j].indexOf("User:");
        	 int userEndIndex = mailLines[j].indexOf("<o:",userIndex+5);
        	 //String index1 = "Changeset:";
        	 //System.out.println("index:"+index);  
        	 while (index > 0) {
        	     //System.out.println(index);
        	     changeSet = mailLines[j].substring(index+10, index+51 );
        	     //user= mailLines[j].substring(userIndex+6, userEndIndex); 
        	     user= mailLines[j].substring(userIndex+19, userEndIndex);   
        	     Matcher matcher = issuePattern.matcher(mailLines[j]);
        	     Matcher matcher2 = issuePattern2.matcher(mailLines[j]);
        	     Matcher matcher3 = issuePattern3.matcher(mailLines[j]);
        	     Matcher matcher4 = issuePattern4.matcher(mailLines[j]);
        	     Matcher matcher5 = issuePattern5.matcher(mailLines[j]);
        	     System.out.println("Change set: " +changeSet);
        	     System.out.println("User: " +user);
        	     //listMatches.add(changeSet);
        	     issuesList.add(user);
        	     
        	     String userN="";
        	     while(matcher5.find()&&matcher2.find()){
        	    	 String usr=matcher5.group(0);
        	    	 System.out.println("user name is"+usr);
        	    		int num=usr.indexOf(0);
        				userN=usr.substring(num+2);
        				System.out.println("^^^^^^^^^^^^^^"+userN);
        				
        				//for matcher2
        				
        				 if (matcher2.group().equals(pan) || matcher2.group().toLowerCase().contains(ADBDR.toLowerCase())) {
            	    	 }else{
            	    		 String str=matcher2.group(0).trim();
            	    	 System.out.println("Issues2:"+matcher2.group() );
            	    	 System.out.println("issue is : "+str);
            	    	 issuesList.add(matcher2.group());
            	    	 
            	    	 ////this is crucial part
            	    	 String testing="";
            	    	 String assigneeName="";
            	        	String reporterName="";
            	        	JSONObject assignee=null;
            	        	JSONObject reporter=null;
            	        	String assigneeData="";
            	        	JSONObject issueData=null;
            	        	JSONObject issueFields=null;
            	        	try{
            	        		String issue = invokeGetMethod(auth, BASE_URL+"/rest/api/2/issue/"+str+"?fields=assignee,reporter");
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
            					
            					
            					String editIssueData = "{\"body\": \"patched in "+ message.getSubject()+" and Commit number is: "+changeSet+"\"}";
          					String resp = invokePostMethod(auth, BASE_URL+"/rest/api/2/issue/"+str+"/comment", editIssueData);
            					System.out.println("**************"+resp);
            					System.out.println("-----"+editIssueData);
            					String editIssueData2 = "{\"transition\": {\"id\": \"141\"}}";
          					System.out.println(invokePostMethod(auth, BASE_URL+"/rest/api/2/issue/"+str+"/transitions", editIssueData2));
            				////Important///////	
            					/*String editIssueData2 = "{\"update\": {\"comment\": [{\"add\": {\"body\":\" patched in "+ message.getSubject()+"\"}}]},\"transition\": {\"id\": \"141\"}}";						
            					System.out.println(invokePostMethod(auth, BASE_URL+"/rest/api/2/issue/"+st+"/transitions", editIssueData2));	*/
            				/////Important//////
            					
            					//assignee = issueFields.getJSONObject("assignee");
            					reporter = issueFields.getJSONObject("reporter");
            					assignee = reporter;
            					//assigneeData = "{\"name\":\""+reporterName+"\"}";
            					testing="ITT";
            					assigneeData = "{\"name\":\""+testing+"\"}";
            					System.out.println("%%%%%%%"+assigneeData);
        					invokePutMethod(auth, BASE_URL+"/rest/api/2/issue/"+str+"/assignee", assigneeData);

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
            	    			    msg.addRecipient(Message.RecipientType.TO, new InternetAddress("aakella@amphorainc.com"));
            	    			  
            	    			    //msg.addRecipient(Message.RecipientType.TO, new InternetAddress(userN+"@amphorainc.com"));
            	    			    	
            	    			   // msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse("BuildTeam@amphorainc.com"));
            	    			    	//msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse("dvirk@amphorainc.com"));
            	    			  
            	    			    	msg.setSubject("wrong in "+message.getSubject()+" patch check in issue number "+str);
            	    			    BodyPart messageBody=new MimeBodyPart();
            	    			    messageBody.setText("You might have given wrong Issue Number(or format of Issue number) as "+str+" for Commit "+changeSet+" which is not found in JIRA. Please correct.");
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
            	    		}
            	        	catch(IllegalArgumentException e){
            	    			System.out.println("issue is wrong");
            	    			e.printStackTrace();
            	    			
            	    			try{
            	    			   
            	    				if(userN!=""){
            	    			    Message msg=new MimeMessage(session);
            	    			    msg.setFrom(new InternetAddress("aakella@amphorainc.com"));
            	    			  
            	    			    //msg.addRecipient(Message.RecipientType.TO, new InternetAddress(userN+"@amphorainc.com"));
            	    			    msg.addRecipient(Message.RecipientType.TO, new InternetAddress("aakella@amphorainc.com"));
            	    			    	
            	    			   // msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse("BuildTeam@amphorainc.com"));
            	    			   // 	msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse("dvirk@amphorainc.com"));
            	    			  
            	    			    msg.setSubject("wrong in "+message.getSubject()+" patch check in issue number "+str);
            	    			    BodyPart messageBody=new MimeBodyPart();
            	    			    messageBody.setText("You might have given wrong Issue Number (or format of Issue number) as "+str+" for Commit "+changeSet+" which is not found in JIRA. Please correct.");
            	    			    Multipart multipart = new MimeMultipart();
            	    			    multipart.addBodyPart(messageBody);
            	    			   
            	    			      msg.setContent(multipart);

            	    			      try{
            	    			      Transport.send(msg);System.out.println("----------------mail sucess------------------");}
            	    			      catch (Exception ee) {
										// TODO: handle exception
            	    			    	  ee.printStackTrace();
									}
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
            	    	 ////donot touch
            	    	 
            	    	 }
        	     }
        	     
        	     
        	     /*while (matcher.find() )
        	     {
        	    	 
        	    	 //listMatches.add(matcher.group());
        	    	
        	    	 if (matcher.group().equals(pan) || matcher.group().toLowerCase().contains(ADBDR.toLowerCase())) {
        	    	 }else{
        	    		 System.out.println("Issues:"+matcher.group() );
        	    		 
        	    		 issuesList.add(matcher.group());
        	    		}
        	    	// System.out.println("Issues:"+matcher.group(1));
        	    	 //System.out.println("Issues:"+matcher.group(2));
        	    	 //System.out.println("Issues:"+matcher.group(3));
        	    	 //System.out.println("Issues:"+matcher.group(4));
        	    	 
        	     
        	     }*/
        	    	//else{
        	    	 //System.out.println("No Matches");
        	     //}
        	     /*while(matcher2.find()){
        	    	 if (matcher2.group().equals(pan) || matcher2.group().toLowerCase().contains(ADBDR.toLowerCase())) {
        	    	 }else{
        	    		 String str=matcher2.group(1);
        	    	 System.out.println("Issues2:"+matcher2.group() );
        	    	 System.out.println("issue is : "+str.trim());
        	    	 issuesList.add(matcher2.group());
        	    	 }
        	     }*/
        	    /* while(matcher3.find()){
        	    	 if (matcher3.group().equals(pan) || matcher3.group().toLowerCase().contains(ADBDR.toLowerCase())) {
        	    	 }else{
        	    	 System.out.println("Issues3:"+matcher3.group() );
        	    	 issuesList.add(matcher3.group());
        	    	 }
        	     }*/
        	    /* while(matcher4.find()){
        	    	 if (matcher4.group().equals(pan) || matcher4.group().toLowerCase().contains(ADBDR.toLowerCase())) {
        	    	 
        	    	 }else{
        	    		 System.out.println("Issues4:"+matcher4.group() );
        	    		 issuesList.add(matcher4.group());
        	    	 }
        	     }*/
        	     index=0;
        	     issueMap.put(changeSet, issuesList);
        	 }
        	// for(String s : listMatches)
             //{
               //  System.out.println("Issue : "+s);
             //}
             }
         
        // System.out.println("Text: " + message.getDescription());
        // getDescription()
         while(issueMap.isEmpty()){
       	  Iterator it = issueMap.entrySet().iterator();
       	  Map.Entry pair = (Map.Entry)it.next();
             System.out.println("Changeset"+pair.getKey() + " = " + pair.getValue());
             it.remove();
         }

      }
      
	}
	 catch (Exception mex) {
	        mex.printStackTrace();
	    }
	}
	////dependent methods
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
