package com.jira.client;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;



public class mail {

	public static void main(String[] args) throws IOException {
		
		Properties props = new Properties();
	    props.setProperty("mail.store.protocol", "imaps");
	try{
		
		
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
    System.out.println("MIN Date---" + minDate);
    
    cal.add(Calendar.DAY_OF_MONTH, -12);                //add 1 day
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
    Message[] messages1 = inbox1.search(term);
    //  Message[] messages = inbox.getMessages();
      System.out.println("messages.length---" + messages.length);
      
      int n = messages.length;
      int n1=messages1.length;
      String sub;
      for (int i = n-1;  i >= 0; --i) {
         Message message = messages[i];
        
         System.out.println("ReleaseNotes Email Number " + (i + 1));
        /* System.out.println("Subject: " + message.getSubject());
         System.out.println("From: " + message.getFrom()[0]);*/
         //System.out.println("Text: " + message.getContent().toString());
         String mailContent = message.getContent().toString();
         
         /*for(String st:mailContent.split(" ")){
     		if(st.startsWith("[I#")){
     			System.out.println(st);
     		}
     	}*/
         
         String mailLines[] = mailContent.split("-------");
         System.out.println("messageline.leangth: " + mailLines.length);
         for (int j = 0; j < mailLines.length; j++) {
        	 //System.out.println("mailLines: " + mailLines[j]);
        	 System.out.println("----------------------end");
        	// int index = mailLines[j].indexOf("#ADSO");
        //	 String index1 = "[I";
        	// System.out.println("index:"+index);
        	// String s1=mailContent.substring(index+1,index+10);
        	// System.out.println("strimng is: "+s1);
        	
        	
        	 
        	 if(mailContent.contains("ADSO-1931")){
         		// System.out.println("Subject: " + message.getSubject());
                  System.out.println("From: " + message.getFrom()[0]);
                  System.out.println("Issue#  ADSO-1918 is patched at "+message.getSubject());
                  String name=message.getSubject().toString();
                  
                  for (int i1 = n1-1;  i1 >= 0; --i1) {
                	  Message message1 = messages1[i1];
                     System.out.println("************SEarching for fish**************");
                      System.out.println("deployment Email Number " + (i1 + 1));
                      String mailContent1 = message1.getContent().toString();
                    //  System.out.println(mailContent1.toString());
                      System.out.println(message1.getSubject());
                      if(mailContent1.contains(name)){
                    	  System.out.println("$$$$$$$$$$$$$$  Fish found  $$$$$$$$$$$$$");
                     }
                      
                  }
                  
        	 }
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
