package com.jira.client;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JsonArrayParsing {
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

        //File file = new File("src/all2.json");
        File file = new File("sample.json");
        //File file = new File("src/jissue.json");

        //Object object = jsonParser.parse(new FileReader(file));

        jsonArray = (JSONArray)jsonParser.parse(new FileReader(file));
        
        parseJson(jsonArray);
        System.out.println("Total Size: "+issuArrayList.size()+"....");
        System.out.println("final data: "+issuArrayList);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
}

public static void getArray(Object object2) throws ParseException {

    JSONArray jsonArr = (JSONArray) object2;

    for (int k = 0; k < jsonArr.size(); k++) {

        if (jsonArr.get(k) instanceof JSONObject) {
            parseJsonObject((JSONObject) jsonArr.get(k));
        } else {
            System.out.println(jsonArr.get(k));
        }

    }
}
public static void parseJsonObject(JSONObject jsonObject)throws ParseException{
	Hashtable<String, String> ht = new Hashtable<String,String>();
	Set<Object> set = jsonObject.keySet();
	
    Iterator iterator = set.iterator();
    while (iterator.hasNext()) {
        Object obj = iterator.next();
        if (jsonObject.get(obj) instanceof JSONArray) {
            //System.out.println(obj.toString());
            getArray(jsonObject.get(obj));
        } else {            
                //System.out.println(obj.toString() + "\t"+ jsonObject.get(obj));
               ht.put(obj.toString(),jsonObject.get(obj).toString());               
            }
       }
   // System.out.println(ht);
    issuArrayList.add(ht);
}	

public static void parseJson(JSONArray jsonArray) throws ParseException {    
    for (Object object : jsonArray) {
		Object obj = object;
		 if (obj instanceof JSONArray) {
	            //System.out.println(obj.toString());
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