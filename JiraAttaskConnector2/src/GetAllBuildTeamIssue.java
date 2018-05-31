import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
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


public class GetAllBuildTeamIssue {
	private static String BASE_URL = "https://amphorainc.atlassian.net";

	public static void main(String[] args) throws IOException {
		
		
	        GetAllBuildTeamIssue gb=new GetAllBuildTeamIssue();
		
		
		Properties prop = new Properties();
		
		prop.load(GetAllBuildTeamIssue.class.getClassLoader().getResourceAsStream("conf.properties"));
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
			
			
			
			for (int i = 0; i < issuesArray.length(); i++) {
				JSONObject anIssue = issuesArray.getJSONObject(i);
				//System.out.println(anIssue.getJSONObject("reporter").getString("name"));
				//byTeju
				String mykeys=anIssue.get("key").toString();
				//teju
				System.out.println(anIssue.get("key"));
				
				String keyword=mykeys;
				//String keyword="ADSO-1847";
				System.out.println("started searching.....");
				//gb.searchEmail(keyword);
				
				System.out.println("search ended...");
				
				
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

	

}
