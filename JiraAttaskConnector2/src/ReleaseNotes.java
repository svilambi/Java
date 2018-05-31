

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import java.util.regex.*;
import javax.mail.internet.*;
import javax.activation.*;



public class ReleaseNotes {

	public static void main(String[] args) throws IOException {
		
		Properties props = new Properties();
	    props.setProperty("mail.store.protocol", "imaps");
	    String host="172.16.143.41";
		props.put("mail.smtp.host",host);  
        props.put("mail.smtp.auth", "false");
        
        StringBuilder sb=new StringBuilder();
        
	try{
		
		
	Session session = Session.getDefaultInstance(props);
	//session.setDebug(true);
    Store store = session.getStore();
    store.connect("outlook.office365.com", "aakella@amphorainc.com", "2923ujeT");
   // props.put("mail.smtp.port", "587");
    Folder inbox = store.getFolder("Inbox/FinalProject");
   
    inbox.open(Folder.READ_ONLY);
   
    SearchTerm term = null;

    Calendar cal = null;
    cal = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    
    Date minDate = new Date(cal.getTimeInMillis());   //get today date
   // System.out.println("MIN Date---" + minDate);
    
    cal.add(Calendar.DAY_OF_MONTH, -1);                //add 1 day
    Date maxDate = new Date(cal.getTimeInMillis());   //get tomorrow date
   // System.out.println("MAX Date---" + maxDate);
    
    ReceivedDateTerm minDateTerm = new ReceivedDateTerm(ComparisonTerm.LE, minDate);
    ReceivedDateTerm maxDateTerm = new ReceivedDateTerm(ComparisonTerm.GE, maxDate);
    
   // System.out.println("MIN Date term---" + minDateTerm);
   // System.out.println("MAX Date term---" + maxDateTerm);

    term = new AndTerm(maxDateTerm, minDateTerm);            //concat the search terms

    //System.out.println("term---" + term);
    
    Message[] messages = inbox.search(term);
   

     // System.out.println("messages.length---" + messages.length);
      
      int n = messages.length;
      System.out.println(n);
   
    Message msg=new MimeMessage(session);
    msg.setFrom(new InternetAddress("aakella@amphorainc.com"));
    //msg.addRecipient(Message.RecipientType.TO, new InternetAddress("aakella@amphorainc.com"));
    msg.addRecipient(Message.RecipientType.TO, new InternetAddress("aakella@amphorainc.com"));
  //  msg.addRecipients(Message.RecipientType.CC, InternetAddress.parse("BuildTeam@amphorainc.com"));
    msg.setSubject("PATCHES RELEASED DETAILS FOR "+dateFormat.format(maxDate));
    
    BodyPart messageBody=new MimeBodyPart();
    messageBody.setText("Please find attched text file for Release Notes of patches and their respective Issue numbers of "+dateFormat.format(maxDate));
    Multipart multipart = new MimeMultipart();
    multipart.addBodyPart(messageBody);

    System.out.println("******RELEASE NOTES AND RELEATED ISSUE NUMBERS******");
      for (int i = n-1;  i >= 0; --i) {
         Message message = messages[i];
        
        // System.out.println("ReleaseNotes Email Number " + (i + 1));
         System.out.println();
         String mailContent = message.getContent().toString();
         System.out.println("-----------");
         System.out.println((i + 1)+". "+message.getSubject());
         System.out.println("-----------");
         
         Pattern p=Pattern.compile("\\[M#(.*?)\\]");
         Pattern p1=Pattern.compile("\\[Issue#(.*?)\\]");
         Matcher m=p.matcher(mailContent);
         Matcher m1=p1.matcher(mailContent);
         Pattern p2=Pattern.compile("\\[DB #(.*?)\\]");
         Matcher m2=p2.matcher(mailContent);
         Pattern p3=Pattern.compile("\\[DB#(.*?)\\]");
         Matcher m3=p2.matcher(mailContent);
         
        /* Pattern commentPattern=Pattern.compile("\\[Comment:(.*?)?=Items:\\]");
         Matcher commentMatcher=commentPattern.matcher(mailContent);
         */
        		 
        		 
         while(m.find()){
        	 System.out.println(m.group(1));
        	
        	 //msg.setText(message.getSubject()+"\n"+m.group(1));
        	/* sb.append(message.getSubject()).append(System.lineSeparator());
        	 sb.append(m.group(1)).append(System.lineSeparator());*/
        	 //msg.setText(sb.toString());
         }
        while(m1.find()){
        	 System.out.println(m1.group(1));
        	/* sb.append(message.getSubject()).append(System.lineSeparator());
        	 sb.append(m.group(1)).append(System.lineSeparator());*/
        }
        while(m2.find()){
       	 System.out.println(m2.group(1));
        }
        while(m3.find()){
          	 System.out.println(m3.group(1));
           }
       /* while(commentMatcher.find()){
        	System.out.println("Comment");
         	 System.out.println(commentMatcher.group(1));*/
         // }
         //msg.setText(sb.toString());
        // String mailLines[] = mailContent.split("-------");
        // System.out.println("messageline.leangth: " + mailLines.length);
         
        
         
      }
      messageBody = new MimeBodyPart();
      String filename = "D:/Test.txt";
      DataSource source = new FileDataSource(filename);
      messageBody.setDataHandler(new DataHandler(source));
      messageBody.setFileName(filename);
      multipart.addBodyPart(messageBody);
      msg.setContent(multipart);

      
      Transport.send(msg);System.out.println("mail sucess");
      }
    

	
	 catch (Exception mex) {
	        mex.printStackTrace();
	    }
	}}

	
	




	
	
	

