import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;


public class FindOddDocsList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String projectName = "Sinclair - Standard S & M Project";
		String projType = "SupportProjects";
		//String projType = "DevProjects";
		//String projType = "ServiceProjects";
		ArrayList<String> docs = new ArrayList<String>();
		try {			
            FileReader fileReader = new FileReader("D:/MigrationWork/"+projType+"/"+projectName+"/"+projectName+"_Docs.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);            
            String currentLine = null;
            while ((currentLine = bufferedReader.readLine()) != null) {
                String arr[] = currentLine.split("____");                
                String issueRefNum = arr[0];
                String issueDocCount = arr[1];
                String fname = arr[2];               
                docs.add(fname);
            }
            //System.out.println("done"+docs);
            FileReader fileReader2 = new FileReader("D:/MigrationWork/"+projType+"/"+projectName+"/docsList.txt");
            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);            
            String currentLine2 = null;
            while ((currentLine2 = bufferedReader2.readLine()) != null) {
                if (docs.contains(currentLine2)) {
					//System.out.println("");
				} else {
					System.out.println("odd doc name:"+currentLine2);
				}
            }
            System.out.println("success");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

}
