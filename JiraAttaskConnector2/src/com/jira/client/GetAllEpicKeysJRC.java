package com.jira.client;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

public class GetAllEpicKeysJRC {


	private static String BASE_URL = "https://amphorainc.atlassian.net";

	public static void main(String[] args) throws IOException {
		
		Properties prop = new Properties();
		prop.load(GetAllEpicKeysJRC.class.getClassLoader().getResourceAsStream("conf.properties"));
		String up = prop.getProperty("unmpwd");		
		String auth = new String(Base64.encode(up));
		
		try {
			
			String projectKey = "ENIO"; 
			String issue = invokeGetMethod(auth, BASE_URL+"/rest/api/latest/search?jql=project="+projectKey+"&maxResults=1000");			
			issue = issue.substring(issue.indexOf("["));
			System.out.println(issue);
			FileOutputStream fos = new FileOutputStream("D:/MigrationWork/mapKeys/"+projectKey+"_Keys.txt");
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
			}			
			
		} catch (AuthenticationException e) {
			System.out.println("Username or Password wrong!");
			e.printStackTrace();
		} catch (ClientHandlerException e) {
			System.out.println("Error invoking REST method");
			e.printStackTrace();
		}/* catch (JSONException e) {
			System.out.println("Invalid JSON output");
			e.printStackTrace();
		}*/

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

}
