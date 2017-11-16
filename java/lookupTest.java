// These are the packages we need
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.net.UnknownHostException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;

public class lookupTest {
        public static void main(String[] args) {
                
                        // print time and lookup

                      while(true){
                      try
                     {
                      Thread.sleep(1000); // sleeping so that the query time and logging time isn't too much for the system IO to handle

                        logTimestamp();
                        makeLookup("www.google.com"); 
                        logTimestamp();
                        makeLookup("www.yahoo.com"); 
                        logTimestamp();
                     
                     
                     }
                      catch(InterruptedException ex)
                     { Thread.currentThread().interrupt(); } }

        }

        public static void logTimestamp () {

            // This function is for seeing the timestamps on STDOUT
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            System.out.print("--------------------------\n");
            Date date = new Date();
            System.out.println(dateFormat.format(date));
            System.out.print("--------------------------\n");
        }


        public static void makeLookup(String lookupUri) {

                // Define logging
                Logger logger = Logger.getLogger("DNS-Lookup");
                FileHandler fh;
                try {
                        // if this file /var/log/Java-DNS-Lookup.log doensn't exist, create it manually first or add the logic in here to create it properly
                        fh = new FileHandler("/var/log/Java-DNS-Lookup.log", true);
                        logger.addHandler(fh);
                        SimpleFormatter formatter = new SimpleFormatter();
                        fh.setFormatter(formatter);
                     }
                 catch (SecurityException e) { e.printStackTrace(); }
                 catch (java.io.IOException e) { e.printStackTrace(); }
      

               // Define slack stuff o- your machineName where this Java program is running from, your Slack web-hook URL
               String machineName = "my-servername-001.mydomain.com";

               String requestUrl="https://hooks.slack.com/services/ZZZZZZZZZ/YYYYYYYYY/XXXXXXXXXXXXXXXXXXXXXXXXX";

/* 
// Had hoped I could've made this dynamic , but I couldn't so this works temporarily

               try {
                    String machineName = InetAddress.getLocalHost().getHostName();
                    String secondPart = firstPart + lookupUri +  " on machine " +machineName + "\"";
               String thirdPart = secondPart + ",\"mrkdwn_in\": [\"text\"],\"author_name\": \"lookupTest\",\"text\": \"A lookup has failed\"}]}";
               String payload = thirdPart;
               }
               catch (UnknownHostException m) 
               { 
                  m.printStackTrace(); 
               }
                 //   System.out.print(" The name of the machine is "+machineName);

               String payload = thirdPart;

*/

                   // assumes 'dns' is the name of the slack-channel
                   String firstPart="{\"channel\": \"dns\",\"attachments\": [{\"fallback\": \"This slack integration or application is not supported yet.\",\"color\": \"#ff0000\",\"pretext\": \"A JAVA DNS problem has  been found for path " ;
                    String secondPart = firstPart + lookupUri +  " on the machine " +machineName + "\"";
                    String thirdPart = secondPart + ",\"mrkdwn_in\": [\"text\"],\"author_name\": \"lookupTest\",\"text\": \"A lookup has failed\"}]}";
                    String payload = thirdPart;



            try {   

                InetAddress[] inetAddressArray = InetAddress.getAllByName(lookupUri);
                logger.info ("Lookup SUCESSFUL for - " +lookupUri + "\n");
                        System.out.println("***********************************\n");
                        for (int i = 0; i < inetAddressArray.length; i++) {
                                displayResult(lookupUri + "#" + (i + 1), inetAddressArray[i]);
                        }
                }        

         catch (UnknownHostException e) {
                  
                        sendPostRequest(requestUrl, payload);
                        System.out.print("!!!!! ERROR FOUND for " +lookupUri + " !!!! \n");
                        logger.severe ("Lookup FAILED for - " +lookupUri + "\n");
                        System.out.print("+++++++++++++++++++++++++++++++++++\n");
                        e.printStackTrace();
                        System.out.print("+++++++++++++++++++++++++++++++++++\n");


                
                }

        }
        public static void displayResult(String whichHost, InetAddress inetAddress) {
                System.out.println("Which Host:" + whichHost);
                System.out.println("Canonical Host Name:" + inetAddress.getCanonicalHostName());
                System.out.println("Host Name:" + inetAddress.getHostName());
                System.out.println("Host Address:" + inetAddress.getHostAddress() + "\n");
        }


// Hit slack and post the alert!
public static void sendPostRequest(String requestUrl, String payload) {
    try {
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
        writer.write(payload);
        writer.close();
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuffer jsonString = new StringBuffer();
        String line;
        while ((line = br.readLine()) != null) {
                jsonString.append(line);
        }
        br.close();
        connection.disconnect();


    } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
    }
}


}
