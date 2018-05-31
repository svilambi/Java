
package com.jira.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Map.Entry;
import java.util.*;


import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.naming.AuthenticationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import sun.security.mscapi.KeyStore.MY;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;


public class NewADSO {


	private static String BASE_URL = "https://amphorainc.atlassian.net";

	public static void main(String[] args) throws IOException {
		
		
	     GetAllBuildTeamIssues gb=new GetAllBuildTeamIssues();
		
		
		Properties prop = new Properties();
		
		prop.load(GetAllBuildTeamIssues.class.getClassLoader().getResourceAsStream("conf.properties"));
		String up = prop.getProperty("unmpwd");		
		String auth = new String(Base64.encode(up));
		String uname = up.split(":")[0];
		String dateString = new Date().toString().replaceAll(" ", "_"); 
		dateString = dateString.replaceAll(":", "_");
		String projectKey = "ADSO";
		FileOutputStream fos11 = new FileOutputStream("D:/WorkspaceJira/"+projectKey+"_Keys.txt");
		PrintStream ps1 = new PrintStream(fos11);
		
			    
		try {
			
			 
			String issue = invokeGetMethod(auth, BASE_URL+"/rest/api/latest/search?jql=project="+projectKey+"%20AND%20status=%27Check%20In%27%20and%20assignee!=null");		
			System.out.println(issue);
			JSONObject issuesData = new JSONObject(issue);
			JSONArray issuesArray =  issuesData.getJSONArray("issues");
			
			BufferedReader br3;
            String sCurrentLine;
            String patchname=null;
            //String issuenumber="ADSO-1963";
            String output="Patched in : ";

			
			for (int i = 0; i < issuesArray.length(); i++) {
				JSONObject anIssue = issuesArray.getJSONObject(i);
				//System.out.println(anIssue.getJSONObject("reporter").getString("name"));
				//byTeju
				String mykeys=anIssue.get("key").toString();
			
				System.out.println(anIssue.get("key"));
				
				///////////TEST//////////
				try {
                    br3 = new BufferedReader(new FileReader("D:\\eclipse_indigo - Copy\\Issues.txt"));

                    //System.out.print("Patched in : ");
                    while ((sCurrentLine = br3.readLine()) != null) {
                          //System.out.println("hai");
                          //System.out.println(sCurrentLine);
                          //System.out.println("hai");
                          if(sCurrentLine.contains("."))
                          {
                                 //System.out.println(sCurrentLine);
                                 patchname=sCurrentLine;
                                 patchname=patchname.substring(3, patchname.length());
                          }
                          if(sCurrentLine.trim().contains(mykeys))
                          {
                        	  
                                 
                                 if(patchname.trim().contains("FW:"))
                                 {
                                 patchname=patchname.substring(3, patchname.length());
                                 //System.out.print(", "+patchname.trim());
                                 output+=patchname+", ";
                                 }
                                 else if(patchname.trim().contains("RE:"))
                                 {
                                 patchname=patchname.substring(3, patchname.length());
                                 //System.out.print(", "+patchname.trim());
                                 output+=patchname+", ";
                                 }
                                 else
                                 {
                                        //System.out.print(", "+patchname.trim());
                                        output+=patchname+", ";
                                 }
                          }
                          
                          
                          
                    }
                    output=output.trim();
                    output=output.substring(0, output.length());
                    System.out.println(output);
                    output="";
                    
             } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
             }
//////////////////////////////
				
				
				String issue1=invokeGetMethod(auth, BASE_URL+"/rest/api/2/issue/"+mykeys+"?fields=assignee,reporter");
				//System.out.println(issue1);
				
				JSONObject issueData=new JSONObject(issue1);
				//String selfUrl=issuesData.getString("self");
				JSONObject issueFields=issueData.getJSONObject("fields");
				String assigneeName=issueFields.getJSONObject("assignee").getString("name");
				String repoterName=issueFields.getJSONObject("reporter").getString("name");
				
				
				String keyword=mykeys;
				//String keyword="ADSO-1848";
				System.out.println("started searching.....");
				//gb.searchEmail(keyword,repoterName);
				
				System.out.println("search ended...");
				
				
				
			
				
				System.out.println(assigneeName+" "+ repoterName);
				//System.out.println(stat);
				
		
				
			//	String curlCmdForUpdateIssue="curl -D -u "+up+" -X PUT --data {\"fields\":{\"assignee\":{\"name\":\""+repoterName+"\"}}} -H \"Content-Type:application/json\" "+selfUrl;
			//	System.out.println(curlCmdForUpdateIssue);
				//teju
			}
			/*FileOutputStream fos = new FileOutputStream("D:/MigrationWork/mapKeys/"+projectKey+"_Keys.txt");
			PrintStream ps = new PrintStream(fos);
			FileOutputStream fos1 = new FileOutputStream("D:/MigrationWork/mapKeys/"+projectKey+"_EpicKeyNames.txt");
			PrintStream ps1 = new PrintStream(fos1);
			try {
				JSONArray jp = new JSONArray(issue);
				for (int i = 0; i < jp.length(); i++) {					
					String summary = jp.getJSONObject(i).getJSONObject("fields").getString("summary");
					if (summary.startsWith("AT")) {
						String sArr[] = summary.split(" ", 2);
						String refno = sArr[0].substring(2,sArr[0].length());					
						ps.write(refno.getBytes());
						ps.write("=".getBytes());
						ps.write(jp.getJSONObject(i).getString("key").getBytes());
						ps.println();
					}else{
						JSONObject epicObj = jp.getJSONObject(i);
						String epicKey = epicObj.getString("key");
						JSONObject epicObjFields = epicObj.getJSONObject("fields");
						String name = epicObjFields.getString("summary");
						JSONObject epicObjType = epicObj.getJSONObject("fields").getJSONObject("issuetype");
						String type = epicObjType.getString("name");
						ps1.write(epicKey.getBytes());
						ps1.write("=".getBytes());
						ps1.write(name.getBytes());
						ps1.println();
					}
				}
				ps.close();
				fos.close();
				ps1.close();
				fos1.close();
				System.out.println("success");				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			System.out.println("success");
			
		} catch (AuthenticationException e) {
			System.out.println("Username or Password wrong!");
			e.printStackTrace();
		} catch (ClientHandlerException e) {
			System.out.println("Error invoking REST method");
			e.printStackTrace();
		} catch (JSONException e) {
			System.out.println("Invalid JSON output");
			e.printStackTrace();
		}

	}

	private static String invokeGetMethod(String auth, String url) throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		//ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
	
		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").get(ClientResponse.class);
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}		
		return response.getEntity(String.class);
	}
	private static Response invokeGetMethod2(String auth, String url) throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		//ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
	
		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").get(ClientResponse.class);
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}		
		return clientResponseToResponse(response);
	}
	public static Response clientResponseToResponse(ClientResponse r) {
	    // copy the status code
	    ResponseBuilder rb = Response.status(r.getStatus());
	    // copy all the headers
	    for (Entry<String, List<String>> entry : r.getHeaders().entrySet()) {
	        for (String value : entry.getValue()) {
	            rb.header(entry.getKey(), value);
	        }
	    }
	    // copy the entity
	    //rb.entity(r.getEntityInputStream());
	    rb.entity(r.getEntity(new GenericType<String>(){}));
	    // return the response
	    return rb.build();
	}
	
	private static String invokePostMethod(String auth, String url, String data) throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").post(ClientResponse.class, data);
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}
		return response.getEntity(String.class);
	}
	
	private static void invokePutMethod(String auth, String url, String data) throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").put(ClientResponse.class, data);
		int statusCode = response.getStatus();
		//System.out.println("status code: "+statusCode);
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}
		System.out.println("response : "+response);
	}
	
	private static void invokeDeleteMethod(String auth, String url) throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").delete(ClientResponse.class);
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}
		System.out.println(" execution success, please check the new data ");
	}

//-------------------------------------mail---------------------------------

	
public void searchEmail(String keyword,String reporterName){
		
	
	GetAllBuildTeamIssues g=new GetAllBuildTeamIssues();
		Properties props = new Properties();
		
	    props.setProperty("mail.store.protocol", "imaps");
	try{
		
		props.load(GetAllBuildTeamIssues.class.getClassLoader().getResourceAsStream("conf.properties"));
		String ups = props.getProperty("unmpwd");		
		String auth1 = new String(Base64.encode(ups));
		
		
	Session session = Session.getDefaultInstance(props);
    Store store = session.getStore();
    store.connect("outlook.office365.com", "aakella@amphorainc.com", "2923ujeT");
    //Folder inbox = store.getFolder("Inbox/Release_Notes");
    Folder inbox = store.getFolder("Inbox/Release_Notes");
    Folder inbox1 = store.getFolder("Inbox/DeploymentLogs");
    inbox.open(Folder.READ_ONLY);
    inbox1.open(Folder.READ_ONLY);
    SearchTerm term = null;

    Calendar cal = null;
    cal = Calendar.getInstance();
    Date minDate = new Date(cal.getTimeInMillis());   //get today date
    System.out.println("To Date: " + minDate);
    
    cal.add(Calendar.DAY_OF_MONTH, -20);                //add 1 day
    Date maxDate = new Date(cal.getTimeInMillis());   //get tomorrow date
    System.out.println("From Date: " + maxDate);
    
    ReceivedDateTerm minDateTerm = new ReceivedDateTerm(ComparisonTerm.LE, minDate);
    ReceivedDateTerm maxDateTerm = new ReceivedDateTerm(ComparisonTerm.GE, maxDate);
    
   // System.out.println("MIN Date term---" + minDateTerm);
   // System.out.println("MAX Date term---" + maxDateTerm);

    term = new AndTerm(maxDateTerm, minDateTerm);            //concat the search terms
   // term = new AndTerm(term, maxDateTerm);
   // System.out.println("term---" + term);
    
    Message[] messages = inbox.search(term);
    Message[] messages1 = inbox1.search(term);
    //  Message[] messages = inbox.getMessages();
      //System.out.println("messages.length---" + messages.length);
      
      int n = messages.length;
      int n1=messages1.length;
      String sub;
      for (int i = n-1;  i >= 0; --i) {
         Message message = messages[i];
        
         System.out.println("ReleaseNotes Email Number " + (i + 1));
        
         //System.out.println("Text: " + message.getContent().toString());
         String mailContent = message.getContent().toString();
         
         /*for(String st:mailContent.split(" ")){
     		if(st.startsWith("[I#")){
     			System.out.println(st);
     		}
     	}*/
         
         String mailLines[] = mailContent.split("-------");
        // System.out.println("messageline.leangth: " + mailLines.length);
         for (int j = 0; j < mailLines.length; j++) {
        	 //System.out.println("mailLines: " + mailLines[j]);
        	// System.out.println("----------------------end");
        	 int index = mailLines[j].indexOf("#ADSO");
        //	 String index1 = "[I";
        	
        	// String s1=mailContent.substring(index+1,index+10);
        	if(mailContent.contains(keyword)){
        		// System.out.println("Subject: " + message.getSubject());
                 System.out.println("From: " + message.getFrom()[0]);
                 System.out.println("Issue#  "+keyword+" is patched at "+message.getSubject());
                 String name=message.getSubject().toString();
                 
                 for (int i1 = n1-1;  i1 >= 0; --i1) {
               	  Message message1 = messages1[i1];
                    System.out.println("************SEarching for fish**************");
                     System.out.println("deployment Email Number " + (i1 + 1));
                     String mailContent1 = message1.getContent().toString();
                   //  System.out.println(mailContent1.toString());
                     System.out.println(message1.getSubject());
                     if(message1.getSubject().contains(name)){
                    	 String assigneeData = "{\"name\":\""+reporterName+"\"}";
          				System.out.println(assigneeData);
          				invokePutMethod(auth1, BASE_URL+"/rest/api/2/issue/"+keyword+"/assignee", assigneeData);
                    	 String editIssueData2 = "{\"update\": {\"comment\": [{\"add\": {\"body\":\" patched in "+ message.getSubject()+" and deployed\"}}]},\"transition\": {\"id\": \"141\"}}";
                		 System.out.println(invokePostMethod(auth1, BASE_URL+"/rest/api/2/issue/"+keyword+"/transitions", editIssueData2));
                		 System.out.println(editIssueData2);
                		
                System.out.println("**************Sucessfully modified************");
                    }
                     else if((i1 + 1)==1){
                    	 String assigneeData = "{\"name\":\"Deployment Team\"}";
          				System.out.println(assigneeData);
          				invokePutMethod(auth1, BASE_URL+"/rest/api/2/issue/"+keyword+"/assignee", assigneeData);
                    	 String editIssueData2 = "{\"update\": {\"comment\": [{\"add\": {\"body\":\" patched in "+ message.getSubject()+" ,yet to deploy\"}}]},\"transition\": {\"id\": \"141\"}}";
                		 System.out.println(invokePostMethod(auth1, BASE_URL+"/rest/api/2/issue/"+keyword+"/transitions", editIssueData2));
                		 System.out.println(editIssueData2);
                		
         				System.out.println("********Deployment pending*************");
                     }
                     
                 }
                 
                 
        		 
        		 
        	}
        	 
        	/* if(s1.equals(keyword)){
        		 System.out.println("Subject: " + message.getSubject());
                 System.out.println("From: " + message.getFrom()[0]);
        		 System.out.println("index:"+index);
        		 System.out.println("<<<<<<<<<<<<<<<<<<<<<<  OMG! Got it >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        		 System.out.println("Issue#  "+s1+" is patched at "+message.getSubject());
        		 
        		 String editIssueData2 = "{\"update\": {\"comment\": [{\"add\": {\"body\":\" patched in "+ message.getSubject()+"\"}}]},\"transition\": {\"id\": \"141\"}}";
        		 System.out.println(editIssueData2);
        		// String curlCmdForUpdateIssue = "curl -D- -u "+up+" -X PUT --data {\"fields\":{\"assignee\":{\"name\":\""+reporterName+"\"}}} -H \"Content-Type: application/json\" "+selfUrl;
        		String s= "curl -D- -u aruna.akella:2923Teju -X POST --data {\"update\":{\"comment\":[{\"add\":{\"body\":\" "+ message.getSubject()+"\"}}]}} -H \"Content-Type: application/json\"  \"https://amphorainc.atlassian.net/rest/api/2/issue/"+keyword+"/comment";
        		System.out.println(s);
        	 }*/
        	
        	 //if(index==-1){
        		// System.out.println("issue is: "+message.getSubject());
        		// int index1 = mailLines[j].indexOf("[Issue#");
        		// String s2=mailContent.substring(index+3,index+12);
            	// System.out.println("strimng is: "+s2);
        	 //}
        	 
            	 
            	 
        	// char ca=mailLines[j].charAt(index);
        	// System.out.println("character:"+ca);
        	
        	/* while (index != 0) {
        	     //System.out.println(index);
        	     sub = mailLines[j].substring(index+10, index+16 );
        	     System.out.println("Change set: " +sub);
        	 }*/
             } 
         
        // System.out.println("Text: " + message.getDescription());
        // getDescription()
      }

	}
	 catch (Exception mex) {
	        mex.printStackTrace();
	    }

}
}
