package com.jira.client;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JsonArrayParsing2 {
public static ArrayList<Hashtable<String, String>> issuArrayList = new ArrayList<Hashtable<String,String>>();
public static Properties properties = null;
public static JSONArray jsonArray = null;

static {
    properties = new Properties();
}

public static void main(String args[])
{
 load();	
}
public static void load() {

    try {

        JSONParser jsonParser = new JSONParser();

        File file = new File("src/all2.json");        
        //File file = new File("src/jissue.json");
        System.out.println(file.getName());
        Object object = jsonParser.parse(new FileReader(file));

        jsonArray = (JSONArray) object;
        
        parseJson(jsonArray);
        System.out.println("Total Size: "+issuArrayList.size()+"....");
        System.out.println("final data: "+issuArrayList);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

public static void getArray(Object object2) throws ParseException, JSONException {

    JSONArray jsonArr = (JSONArray) object2;

    for (int k = 0; k < jsonArr.length(); k++) {

        if (jsonArr.get(k) instanceof JSONObject) {
            parseJsonObject((JSONObject) jsonArr.get(k));
        } else {
            System.out.println(jsonArr.get(k));
        }

    }
}
public static void parseJsonObject(JSONObject jsonObject)throws ParseException, JSONException{
	Hashtable<String, String> ht = new Hashtable<String,String>();
	
    Iterator iterator = jsonObject.keys();
    while (iterator.hasNext()) {
        Object obj = iterator.next();
        if (jsonObject.get((String) obj) instanceof JSONArray) {
            //System.out.println(obj.toString());
            getArray(jsonObject.get((String) obj));
        } else {            
                //System.out.println(obj.toString() + "\t"+ jsonObject.get(obj));
               ht.put(obj.toString(),jsonObject.get((String) obj).toString());               
            }
       }
    Enumeration<String> k = ht.keys();
    while (k.hasMoreElements()) {
		String string = (String) k.nextElement();
		System.out.println("Keys "+string);
		
	}
    issuArrayList.add(ht);
}	

public static void parseJson(JSONArray jsonArray) throws ParseException, JSONException { 
	for (int i = 0; i < jsonArray.length(); i++) {
		Object obj = jsonArray.get(i);
		if (obj instanceof JSONArray) {
			getArray(obj);
		} else {
			if (obj instanceof JSONObject) {
                parseJsonObject((JSONObject) obj);
            } else {
                System.out.println(obj.toString() + "\t"+ obj);
            }
		}
	}
}
}