import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;


public class GetMessageExample {
	 public static void main(String args[]) throws Exception {
		    /*
		   if (args.length != 3) {
		      System.err.println("Usage: java MailExample host username password");
		      System.exit(-1);
		    }
		*/
		    String host = "outlook.office365.com";
		    String username = "aakella@amphorainc.com";
		    String password = "2923ujeT";

		    // Create empty properties
		    Properties props = new Properties();

		    // Get session
		    Session session = Session.getDefaultInstance(props, null);

		    // Get the store
		    Store store = session.getStore("imap");
		    store.connect(host, username, password);

		    // Get folder
		    Folder folder = store.getFolder("INBOX");
		    folder.open(Folder.READ_ONLY);

		    BufferedReader reader = new BufferedReader(new InputStreamReader(
		        System.in));

		    // Get directory
		    Message message[] = folder.getMessages();
		    for (int i = 0, n = message.length; i < n; i++) {
		      System.out.println(i + ": " + message[i].getFrom()[0] + "\t"
		          + message[i].getSubject());

		      System.out.println("Read message? [YES to read/QUIT to end]");
		      String line = reader.readLine();
		      if ("YES".equalsIgnoreCase(line)) {
		        System.out.println(message[i].getContent());
		      } else if ("QUIT".equalsIgnoreCase(line)) {
		        break;
		      }
		    }

		    // Close connection
		    folder.close(false);
		    store.close();
		  }
		
}
