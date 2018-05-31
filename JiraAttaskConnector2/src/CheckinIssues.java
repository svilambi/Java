import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.SearchTerm;
import javax.naming.AuthenticationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;


public class CheckinIssues {

	
private static String BASE_URL = "https://amphorainc.atlassian.net";
	
	public static void main(String[] args) throws IOException {
		
		Properties prop = new Properties();
		prop.load(CheckinIssues.class.getClassLoader().getResourceAsStream("conf.properties"));
		String up = prop.getProperty("unmpwd");		
		String auth = new String(Base64.encode(up));
		//ArrayList<String> issueAL = new ArrayList<>();
		String[] issuels;
		
		try {
			
			String projectKey = "ADSO"; 
			String issue = invokeGetMethod(auth,BASE_URL+"/rest/api/latest/search?jql=project="+projectKey+"%20AND%20status=%27Check%20In%27%20and%20assignee!=null");		
			System.out.println(issue);
			JSONObject issuesData = new JSONObject(issue);
			JSONArray issuesArray=issuesData.getJSONArray("issues");
	
			//issuels = new String[issueArray.length()];
			//-------------------------------------------------------------------------------------
			for (int i = 0; i < issuesArray.length(); i++) {
				JSONObject anIssue = issuesArray.getJSONObject(i);
				//System.out.println(anIssue.getJSONObject("reporter").getString("name"));
				//byTeju
				String mykeys=anIssue.get("key").toString();
				//teju
				System.out.println(anIssue.get("key"));
				
				String keyword=mykeys;
				CheckinIssues ci=new CheckinIssues();
				
				//for comments
				String issue0=invokeGetMethod(auth, BASE_URL+"/rest/api/2/issue/"+mykeys+"/comment?");
				//System.out.println(issue0);  
				JSONObject issueData0=new JSONObject(issue0);
				
				JSONArray issueComments=issueData0.getJSONArray("comments");
				for(int c=0;c<issueComments.length();c++)
				{
					JSONObject printComment=issueComments.getJSONObject(c);
					String print=printComment.get("body").toString();
					//System.out.println(print);
				}
				//by teju
			
				String issue1=invokeGetMethod(auth, BASE_URL+"/rest/api/2/issue/"+mykeys+"?fields=assignee,reporter");
				//System.out.println(issue1);
				
				JSONObject issueData=new JSONObject(issue1);
				//String selfUrl=issuesData.getString("self");
				JSONObject issueFields=issueData.getJSONObject("fields");
				String assigneeName=issueFields.getJSONObject("assignee").getString("name");
				String repoterName=issueFields.getJSONObject("reporter").getString("name");
				//System.out.println(assigneeName+" "+ repoterName);
		
				
		//		String curlCmdForUpdateIssue="curl -D -u "+up+" -X PUT --data {\"fields\":{\"assignee\":{\"name\":\""+repoterName+"\"}}} -H \"Content-Type:application/json\" "+selfUrl;
			//	System.out.println(curlCmdForUpdateIssue);
				//teju
			}
			
			//-----------------------------------------------------------------
			
			HashMap<String, ArrayList<String>> mailHS = readEmail();
			
			//for (String key : mailHS.keySet()) {
				//   System.out.println("------------------------------------------------");
				//   System.out.println("Iterating or looping map using java5 foreach loop");
				  // System.out.println("key: " + key + " value: " + mailHS.get(key));
				//}
							
			
			/*for (int i = 0; i < issueArray.length(); i++) {
				
				forloop: for (String patch : mailHS.keySet()) {
					  System.out.println("------------------------------------------------");
					   //System.out.println("Iterating or looping map using java5 foreach loop");
					   //System.out.println("key: " + patch );
					String mailTemp=mailHS.get(patch).toString();
					//System.out.println("MailTemp: " + mailTemp );
				boolean contains =mailTemp.toLowerCase().contains(issuels[i].toLowerCase());
				if(contains){
					System.out.println("issue Found :" + issuels[i]);
					System.out.println("Patch Name is :" + patch);
					break forloop;
				}else{
					System.out.println("issue Not Found :" + issuels[i]);
				}
				}
			}*/
			
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

public static HashMap<String, ArrayList<String>> readEmail(){
	//ArrayList<String> mailAL = new ArrayList<>();
	ArrayList<String> str2 = new ArrayList<>();
	HashMap<String, ArrayList<String>> mailHS = new HashMap<String, ArrayList<String>>();
    Properties props = new Properties();
    props.setProperty("mail.store.protocol", "imaps");
   // String str1 = "ADSO-1647";
    try {
    	//Session ses = Session.getInstance(props, null);
    	
    	Session session = Session.getDefaultInstance(props);
        Store store = session.getStore();
        store.connect("outlook.office365.com", "aakella@amphorainc.com", "2923ujeT");
        //Folder inbox = store.getFolder("Inbox/Release_Notes");
        Folder inbox = store.getFolder("Inbox/Release_Notes");
        inbox.open(Folder.READ_ONLY);
       /* Message msg = inbox.getMessage(inbox.getMessageCount());
        Address[] in = msg.getFrom();
        int count = msg.getMessageNumber();
        for (int a=0;a<=count;a++) {
          //System.out.println("FROM:" + address.toString());
        
       // Multipart mp = (Multipart) msg.getContent();
        //BodyPart bp = mp.getBodyPart(0);
        System.out.println("SENT DATE:" + msg.getSentDate());
        System.out.println("SUBJECT:" + msg.getSubject());
      //  System.out.println("CONTENT:" + bp.getContent());
        }
        */
       SearchTerm term = null;

       /*  Calendar cal = null;
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
        System.out.println("term---" + term);*/

        //Message messages[] = inbox.search(term); 
    Message[] messages = inbox.search(term);
        
      //  Message[] messages = inbox.getMessages();
       // System.out.println("messages.length---" + messages.length);
        
        int n = messages.length;
        for (int i = n-1;  i >= 0; --i) {
           Message message = messages[i];
           //System.out.println("---------------------------------");
           //System.out.println("Email Number " + (i + 1));
           //System.out.println("Subject: " + message.getSubject());
           //System.out.println("From: " + message.getFrom()[0]);
           //System.out.println("Text: " + message.getContent().toString());
           str2.add(message.getContent().toString());
           String str3 = message.getSubject();
           mailHS.put(str3,str2);
          // boolean a = str1.toLowerCase().contains(str2.toLowerCase());
           //if(str2.toLowerCase().contains(str1.toLowerCase())){
           		//if(str2.toLowerCase().contains(str1.toLowerCase())){
        	   //System.out.println("Subject matched is: " + message.getSubject());
        	  // i=-1;
          // }
    }
       
        System.out.println("Successfully read mails");
    }catch (Exception mex) {
        mex.printStackTrace();
    }
    return mailHS;
}


}
