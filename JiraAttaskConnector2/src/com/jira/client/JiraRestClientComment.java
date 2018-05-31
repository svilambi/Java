package com.jira.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.Map.Entry;

import javax.naming.AuthenticationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.Base64;

public class JiraRestClientComment {


	private static String BASE_URL = "https://amphorainc.atlassian.net";

	public static void main(String[] args) throws IOException {
		System.out.println("started : "+new Date());
		Properties prop = new Properties();
		prop.load(JiraRestClientComment.class.getClassLoader().getResourceAsStream("conf.properties"));
		String up = prop.getProperty("unmpwd");		
		String auth = new String(Base64.encode(up));
		//FileReader fileReader = new FileReader("D:/MigrationWork/service_project_names.txt");
		//FileReader fileReader = new FileReader("D:/MigrationWork/pnames.txt");
		//FileReader fileReader = new FileReader("D:/MigrationWork/active_dev_pnames.txt");
		//FileReader fileReader = new FileReader("D:/MigrationWork/active_service_project_names.txt");
		//FileReader fileReader = new FileReader("D:/MigrationWork/inactive_support_project_names2.txt");
		//FileReader fileReader = new FileReader("D:/MigrationWork/inactive_dev_pnames.txt");
		FileReader fileReader = new FileReader("D:/MigrationWork/inactive_service_pnames2.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String currentLine = null;
        int count = 0; 
        while ((currentLine = bufferedReader.readLine()) != null) {
        	String arr[] = currentLine.split("=");
        	String projectName = arr[0];
    		String key =arr[1];
    		//String projType = "SupportProjects";
    		String projType = "ServiceProjects";	
    		//String projType = "DevProjects";
    		String commFileName = "D:/MigrationWork/"+projType+"/archives/"+projectName+"/"+projectName+"_PrivateComments.txt";
    		PrintStream ps = new PrintStream(new FileOutputStream(commFileName)); 
    		System.out.println(taskMethod(auth,projectName,key,projType,ps));
    		System.out.println(issueMethod(auth,projectName,key,projType,ps));
    		ps.close();    		
    		count++;
		}       
		
		System.out.println("Completed Successfully : "+new Date());
	}
	
	public static String issueMethod(String auth,String projectName,String key,String projType, PrintStream ps) throws FileNotFoundException{
		String docsFileName = "D:/MigrationWork/"+projType+"/archives/"+projectName+"/"+projectName+"_IssueCommentPermissions.json";	
		
		try {
			Properties p = new Properties();
			//String fileName = "D:/MigrationWork/mapKeys/"+key+"_Keys.txt";
			String fileName = "D:/MigrationWork/bulkKeys/"+key+"/"+key+"_Keys.txt";
			p.load(new FileInputStream(fileName));
			FileReader fileReader = new FileReader(docsFileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
           
            String currentLine = null;
            while ((currentLine = bufferedReader.readLine()) != null) {
                String refNum = currentLine.split("____")[0];
                refNum = refNum.substring(7,refNum.length());
                String issueKey =(String) p.get(refNum);
                String issue = invokeGetMethod(auth, BASE_URL+"/rest/api/2/issue/"+issueKey+"/comment");
    			//System.out.println(issue);
    			//System.out.println(refNum+"---"+issueKey);
    			JSONObject j1 = new JSONObject(issue);
    			JSONArray a1 =j1.getJSONArray("comments") ;
    			for (int i = 0; i < a1.length(); i++) {
    				if(a1.getJSONObject(i).getString("body").contains("Private Comment - ")){
    					JSONObject objTemp = a1.getJSONObject(i);
    					String selfUrl = objTemp.getString("self");
    					JSONObject obj =new JSONObject();			
    					obj.put("type", "role");
    					obj.put("value", "Service Desk Team");
    					objTemp.put("visibility", obj);
    					String statusmsg = ">>> "+issueKey+" Comment "+objTemp.getString("id")+" Update "+invokePutMethod1(auth, selfUrl, objTemp.toString())+" <<<";    					System.out.println(statusmsg);    				
    					ps.write(statusmsg.getBytes());
    					ps.println();
    				}
    			}
            }
		
		} catch (ClientHandlerException e) {
			System.out.println("Error invoking REST method");
			e.printStackTrace();
		}/* catch (JSONException e) {
			System.out.println("Invalid JSON output");
			e.printStackTrace();
		}*/ catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Issue flag success";
	} 
	public static String taskMethod(String auth,String projectName,String key,String projType, PrintStream ps) throws FileNotFoundException{
		String docsFileName = "D:/MigrationWork/"+projType+"/archives/"+projectName+"/"+projectName+"_TaskCommentPermissions.json";		
		try {
			Properties p = new Properties();
			//String fileName = "D:/MigrationWork/mapKeys/"+key+"_Keys.txt";
			String fileName = "D:/MigrationWork/bulkKeys/"+key+"/"+key+"_Keys.txt";
			p.load(new FileInputStream(fileName));
			FileReader fileReader = new FileReader(docsFileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);           
            String currentLine = null;
            while ((currentLine = bufferedReader.readLine()) != null) {
                String refNum = currentLine.split("____")[0];
                refNum = refNum.substring(7,refNum.length());
                String issueKey =(String) p.get(refNum);
                String issue = invokeGetMethod(auth, BASE_URL+"/rest/api/2/issue/"+issueKey+"/comment");
    			//System.out.println(issue);
    			//System.out.println(refNum+"---"+issueKey);
    			JSONObject j1 = new JSONObject(issue);
    			JSONArray a1 =j1.getJSONArray("comments") ;
    			for (int i = 0; i < a1.length(); i++) {
    				if(a1.getJSONObject(i).getString("body").contains("Private Comment - ")){
    					JSONObject objTemp = a1.getJSONObject(i);
    					String selfUrl = objTemp.getString("self");
    					JSONObject obj =new JSONObject();			
    					obj.put("type", "role");
    					obj.put("value", "Service Desk Team");
    					objTemp.put("visibility", obj);
    					String statusmsg = ">>> "+issueKey+" Comment "+objTemp.getString("id")+" Update "+invokePutMethod1(auth, selfUrl, objTemp.toString())+" <<<";    					
    					System.out.println(statusmsg);    				
    					ps.write(statusmsg.getBytes());
    					ps.println();
    				}
    			}
            }
		
		} catch (ClientHandlerException e) {
			System.out.println("Error invoking REST method");
			e.printStackTrace();
		}/* catch (JSONException e) {
			System.out.println("Invalid JSON output");
			e.printStackTrace();
		}*/ catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Task flag success";
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
		}else if (statusCode == 200){
			System.out.println("Success");
		}
		//System.out.println("response : "+response);
	}
	
	private static String invokePutMethod1(String auth, String url, String data) throws AuthenticationException, ClientHandlerException {
		Client client = Client.create();
		WebResource webResource = client.resource(url);
		ClientResponse response = webResource.header("Authorization", "Basic " + auth).type("application/json")
				.accept("application/json").put(ClientResponse.class, data);
		int statusCode = response.getStatus();
		//System.out.println("status code: "+statusCode);
		String msg = "";
		if (statusCode == 401) {
			msg = "failed";
			throw new AuthenticationException("Invalid Username or Password");
		}else if (statusCode == 200){
			msg="success";
		}
		return msg;
	}


}
