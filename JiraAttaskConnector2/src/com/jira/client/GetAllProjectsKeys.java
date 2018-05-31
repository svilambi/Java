package com.jira.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.AuthenticationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

public class GetAllProjectsKeys {


	private static String BASE_URL = "https://amphorainc.atlassian.net";

	public static void main(String[] args) throws IOException {
		System.out.println(new Date()+" - Started");
		Properties prop = new Properties();
		prop.load(GetAllProjectsKeys.class.getClassLoader().getResourceAsStream("conf.properties"));
		String up = prop.getProperty("unmpwd");		
		String auth = new String(Base64.encode(up));
		PrintStream ps12 = new PrintStream(new FileOutputStream("D:/MigrationWork/AllProjectKeys.txt"));
		
		try {
			//Get Projects
			String projects = invokeGetMethod(auth, BASE_URL+"/rest/api/2/project");
			System.out.println(projects);
			JSONArray projectArray = new JSONArray(projects);
			for (int m = 0; m < projectArray.length(); m++) {
				JSONObject proj = projectArray.getJSONObject(m);
				//System.out.println("Key:"+proj.getString("key")+", Name:"+proj.getString("name"));
				String projectKey = proj.getString("key");
				String str = projectKey+"="+proj.getString("name");
				ps12.write(str.getBytes());
				ps12.println();			
				
			 if (projectKey.equals("ADAA")) {
				 File f = new File("D:/MigrationWork/bulkKeys/"+projectKey);
					f.mkdir();			
					FileOutputStream fos = new FileOutputStream("D:/MigrationWork/bulkKeys/"+projectKey+"/"+projectKey+"_Keys.txt");
					PrintStream ps = new PrintStream(fos);
					FileOutputStream fos1 = new FileOutputStream("D:/MigrationWork/bulkKeys/"+projectKey+"/"+projectKey+"_EpicKeyNames.txt");
					PrintStream ps1 = new PrintStream(fos1);			
					String result = invokeGetMethod(auth, BASE_URL+"/rest/api/latest/search?jql=project="+projectKey+"&maxResults=1");
					JSONObject obj = new JSONObject(result); 
					int total = Integer.parseInt(obj.getString("total"));
					int start = 0;
					if (total >= 1000) {				
						int limit = 1000;			
						int repeat = total/limit;
						if ((repeat*limit) < total) {
							repeat = repeat + 1;
						}
						
						for (int i = 0; i < repeat ; i++) {
							String result1 = invokeGetMethod(auth, BASE_URL+"/rest/api/latest/search?jql=project="+projectKey+"&startAt="+start+"&maxResults=1000");				
							FileOutputStream fos11 = new FileOutputStream("D:/MigrationWork/bulkKeys/"+projectKey+"/"+projectKey+"_"+start+"_Keys.txt");
							PrintStream ps11 = new PrintStream(fos11);
							ps11.write(result1.getBytes());
							ps11.println();
							ps11.close();
							try {
								FileReader fileReader = new FileReader("D:/MigrationWork/bulkKeys/"+projectKey+"/"+projectKey+"_"+start+"_Keys.txt");
								BufferedReader br = new BufferedReader(fileReader);
								String currentLine = "";
								for (int i1 = 0; (currentLine = br.readLine())!= null ; i1++) {
									currentLine = currentLine.substring(currentLine.indexOf("["));					
								JSONArray jp = new JSONArray(currentLine);
								for (int i11 = 0; i11 < jp.length(); i11++) {					
									String summary = jp.getJSONObject(i11).getJSONObject("fields").getString("summary");
									if (summary.startsWith("AT")) {
										String sArr[] = summary.split(" ", 2);
										String refno = sArr[0].substring(2,sArr[0].length());					
										ps.write(refno.getBytes());
										ps.write("=".getBytes());
										ps.write(jp.getJSONObject(i11).getString("key").getBytes());
										ps.println();
									}else{
										JSONObject epicObj = jp.getJSONObject(i11);
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
								System.out.println(start+" success");					
								}	
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							start = start + limit;
						}
					} else {
						String resdata = invokeGetMethod(auth, BASE_URL+"/rest/api/latest/search?jql=project="+projectKey+"&maxResults=1000");			
						FileOutputStream fos11 = new FileOutputStream("D:/MigrationWork/bulkKeys/"+projectKey+"/"+projectKey+"_"+start+"_Keys.txt");
						PrintStream ps11 = new PrintStream(fos11);
						ps11.write(resdata.getBytes());
						ps11.println();
						ps11.close();
						try {
							resdata = resdata.substring(resdata.indexOf("["));
							JSONArray jp = new JSONArray(resdata);
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
						}			
					 }
		 	 }
			
		   }
			/*String createIssueData = "{\"fields\":{\"project\":{\"key\":\"TEST\"},\"summary\":\"REST Test\",\"issuetype\":{\"name\":\"Bug\"},\"assignee\":{\"name\":\"Calvin.Hewitt\"},\"customfield_10000\": \"2013-07-19T00:00:00.000+1100\",\"customfield_10101\":\"139001\"}}";		

			String issue = invokePostMethod(auth, BASE_URL+"/rest/api/2/issue", createIssueData);
			System.out.println(issue);
			JSONObject issueObj = new JSONObject(issue);
			String newKey = issueObj.getString("key");
			System.out.println("Key:"+newKey);*/
			/*SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			JsonArrayParsing jap = new JsonArrayParsing();
			jap.load();
			//System.out.println(jap.issuArrayList);
			for (int i=0; i<jap.issuArrayList.size(); i++) {
			Hashtable<String, String> ht = (Hashtable<String, String>)JsonArrayParsing.issuArrayList.get(i);
			String priority = ht.get("Priority");
			priority = "Minor";
			String ssid = ht.get("Ref #");
			String summary = ssid+" - "+ht.get("Name");			
			String assignee = ht.get("Assignments");			
			//Create Issue
			String createIssueData = "{\"fields\":{\"project\":{\"key\":\"TEST\"},\"summary\":\"REST Test\",\"issuetype\":{\"name\":\"Bug\"},\"customfield_10000\": \"2013-07-19T00:00:00.000+1100\",\"customfield_10101\":\"139001\"}}";
			String createIssueData = "{\"fields\":{\"project\":{\"key\":\"TEST\"},\"summary\":\""+summary+"\",\"issuetype\":{\"name\":\"Bug\"},\"assignee\":{\"name\":\""+assignee+"\"},\"priority\":{\"name\":\""+priority+"\"},\"customfield_10200\":\""+dateFormat.format(new Date(ht.get("Entry")))+"\",\"customfield_10101\":\""+ssid+"\"}}";
			//String createIssueData = "{\"fields\":{\"project\":{\"key\":\"TEST\"},\"summary\":\""+ht.get("Name")+"\",\"issuetype\":{\"name\":\"Bug\"},\"assignee\":\""+ht.get("Assignments")+"\",\"priority\":\""+ht.get("Priority")+"\",\"status\":\""+ht.get("Status")+"\",\"customfield_10000\":\""+dateFormat.format(new Date(ht.get("Entry")))+"\",\"customfield_10101\":\""+ht.get("Ref #")+"\"}}";
			System.out.println(" issue "+i+")---->  "+createIssueData);
			String issue = invokePostMethod(auth, BASE_URL+"/rest/api/2/issue", createIssueData);
			System.out.println(issue);
			JSONObject issueObj = new JSONObject(issue);
			String newKey = issueObj.getString("key");
			System.out.println("Key:"+newKey);
			}*/
			//Update Issue			
			//String editIssueData = "{\"update\":{\"status\":{\"name\":\"Done\"}}}";
			//String editIssueData = "{\"transition\":{\"id\":\"21\"}}";	
			//String editIssueData2 = "{\"update\": {\"comment\": [{\"add\": {\"body\": \"Comment added when resolving issue\"}}]},\"transition\": {\"id\": \"21\"}}";
			//invokePutMethod(auth, BASE_URL+"/rest/api/2/issue/"+newKey, editIssueData);
			//invokePostMethod(auth, BASE_URL+"/rest/api/2/issue/TEST-22/transitions?expand=transitions.fields", editIssueData);
			
			//String issueData = invokeGetMethod(auth, BASE_URL+"/rest/api/2/issue/TEST-15");
			//System.out.println(issueData);			
			
			//invokeDeleteMethod(auth, BASE_URL+"/rest/api/2/issue/TEST-13");
			
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
		System.out.println(new Date()+" - Ended");
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
/*	private static ClientResponse invokeGetMethod1(String auth, String url) throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		//ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json").accept("application/json").get(ClientResponse.class);
	
		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").get(ClientResponse.class);
		int statusCode = response.getStatus();
		if (statusCode == 401) {
			throw new AuthenticationException("Invalid Username or Password");
		}
		return response;
	}*/
	
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


}
