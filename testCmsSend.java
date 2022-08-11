package dd;

import java.io.*;
import java.net.HttpURLConnection ;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL ;
import java.net.UnknownHostException ;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList ;
import java.util.Date ;
import java.util.Scanner ;

public class testCmsSend {
    private ServerSocket mServerSocket;
    private Socket mSocket1;
    
    private OutputStream out = null;
    private BufferedInputStream reader = null;
    private PrintWriter writer = null;
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
    SimpleDateFormat cFormat = new SimpleDateFormat("MMddHHmmss");
    static Scanner scan = new Scanner(System.in);
    
    String fileCode = "GA9122";
    String kindCode;
    String blockNo = "0000";
    String seqNo = "000";
    int LINE_SIZE = 200;
    int SEPERATE_SIZE = 2;

    public testCmsSend(String ip, int port) throws Exception {
    	setClient(ip, port);
        
        
    }

    public void doIt() throws Exception {
      
      //String filePath = getCommand("file Path : ");
      //String fileName = getCommand("file Name : ");
      String filePath = "C:\\Users\\user\\Desktop\\test\\";
      String fileName = "KFBTEST.txt";
    
    	
      File file = new File(filePath + "/" + fileName);
      BufferedReader reader = new BufferedReader(new FileReader(file));
      
      char[] buffer = new char[LINE_SIZE];
      char[] seperate = new char[SEPERATE_SIZE];
      ArrayList<char[]> dataList = new ArrayList<char[]>();
      
      while(reader.read(buffer) != -1) {
        dataList.add(buffer);
        buffer = new char[LINE_SIZE];
        reader.read(seperate);
      }


        String msg;
        String sendMsg;
        String toDay = dateFormat.format(new Date());
        String cDate = cFormat.format(new Date());
        byte[] seq = new byte[100];
        
        sendMsg = "KFBFTP   0080FTP3900600" + fileCode + "C000" + toDay + "000S" + cDate + "001                            0000000";
        writer.write(sendMsg);
        writer.flush();
        printMsg(sendMsg,"Send");
        
        while(true) {
          msg = readAllMessage();
          printMsg(msg,"Recv");
          
          switch(kindCode) {
          
          case "0600" : {
            String manageCode = msg.substring(55,58);
            
            if(manageCode.equals("001")) {
              sendMsg = "KFBFTP   0080FTP3900610" + fileCode + "C000" + toDay + "000S" + cDate + "001                            0000000";
              writer.write(sendMsg);
              writer.flush();
              printMsg(sendMsg,"Send");
              
              break;
            } else if(manageCode.equals("008")) {
              
              sendMsg = "KFBFTP   0392FTP3900320" + fileCode + "C000" + toDay + String.format("%03d", LINE_SIZE) + "S" + "0000" + "000" + "001" + new String(dataList.get(0));
              writer.write(sendMsg);
              writer.flush();
              printMsg(sendMsg,"Send");
              
              break;
            }
          }
          
          
          case "0610" : {
            String manageCode = msg.substring(55,58);
            
            if(manageCode.equals("001")) {
              sendMsg = "KFBFTP   0245FTP3900320" + fileCode + "C000" + toDay + String.format("%03d", LINE_SIZE) + "S" + "0000" + "000" +"001001"+ new String(dataList.get(0));
//              sendMsg = "KFBFTP   0142FTP3900320" + fileCode + "C000" + toDay + String.format("%03d", LINE_SIZE) + "S" + "001" + new String(dataList.get(0));
              writer.write(sendMsg);
              writer.flush();
              printMsg(sendMsg,"Send");
              
              break;
            } else if(manageCode.equals("003")) {
                Thread.sleep(1000);
                
                reader.close();
                writer.close();
                out.close();
                mSocket1.close();
            	
            	
            	
            	
              return;
            }
          }
          case "0330" : {
            
            if("0000".equals(msg.substring(45,49))) {
              seq = new byte[100];
              
              int totalRecordCount = dataList.size();
              //int blockNoInt = ((totalRecordCount-2)/100 + 1);
              int blockNoInt = 1;
              int seqNoInt = 1;
              //int lastSeq = (totalRecordCount-2)%100;
              int lastSeq = 3;
              int flieLineIdx = 0;
              
              for(int i = 1; i <= blockNoInt; i++) {
                int maxSeq = 100;
                if(i == blockNoInt) {
                  maxSeq = lastSeq;
                }
                
                for(;seqNoInt <= maxSeq; seqNoInt++) {
                  //sendMsg = "KFBFTP   0142FTP3900320" + fileCode + "C000" + toDay + String.format("%03d", LINE_SIZE) + "S" + String.format("%04d", i) 
                  //    + String.format("%03d", seqNoInt) + "001" + new String(dataList.get(flieLineIdx));
                  
                  sendMsg = "KFBFTP   0245FTP3900320" + fileCode + "C000" + toDay + String.format("%03d", LINE_SIZE) + "S" + String.format("%04d", i) 
                      + String.format("%03d", seqNoInt) + "011";
                  
                  String data = "";
//                  for(int a = 0; a < dataList.size(); a ++) {
//                	flieLineIdx = dataList.size() -a;
//                    data += new String(dataList.get(flieLineIdx)) ;
//                  }
//                  sendMsg += data;
//                  
//                  writer.write(sendMsg);
//                  writer.flush();
//                  printMsg(sendMsg,"Send");

                  for(int a = 0; a < 11; a ++) {
                      data += new String(dataList.get(flieLineIdx));
                      flieLineIdx++;
                    }
                    sendMsg += data;
                    
                    writer.write(sendMsg);
                    writer.flush();
                    printMsg(sendMsg,"Send");
                    
                }
                
//                sendMsg = "KFBFTP   0039FTP3900620" + fileCode + "C000" + toDay + String.format("%03d", LINE_SIZE) + "S" + String.format("%04d", i) 
                sendMsg = "KFBFTP   0245FTP3900620" + fileCode + "C000" + toDay + String.format("%03d", LINE_SIZE) + "S" + String.format("%04d", i) 
                    + String.format("%03d", (seqNoInt-1));
                writer.write(sendMsg);
                writer.flush();
                printMsg(sendMsg,"Send");
                
                msg = readAllMessage();
                printMsg(msg,"Recv");
                
                seqNoInt = 1;
              }
              
//              sendMsg = "KFBFTP   0142FTP3900320" + fileCode + "C000" + toDay + String.format("%03d", LINE_SIZE) + "S" + "9999" + "999" + "001" + new String(dataList.get(flieLineIdx));
              sendMsg = "KFBFTP   0245FTP3900320" + fileCode + "C000" + toDay + String.format("%03d", LINE_SIZE) + "S" + "9999" + "999" + "001" + new String(dataList.get(flieLineIdx));
              writer.write(sendMsg);
              writer.flush();
              printMsg(sendMsg,"Send");
              
              break;
              
            } else if("9999".equals(msg.substring(45,49))) {
              sendMsg = "KFBFTP   0080FTP3900600" + fileCode + "C000" + toDay + "000S" + cDate + "003                            0000000";
              writer.write(sendMsg);
              writer.flush();
              printMsg(sendMsg,"Send");
              
              break;
            }
          }
        }
      }
    }
    
    public void setServer(int port) throws Exception {
      //mServerSocket = new ServerSocket(port);
      mSocket1 = mServerSocket.accept();
      out = mSocket1.getOutputStream();
      reader = new BufferedInputStream(mSocket1.getInputStream());
      writer = new PrintWriter(new OutputStreamWriter(out));
      
      System.out.println("Server [" + port + "] connected.");
      
      this.doIt();
      

    }
    
    public void setClient(String ip, int port) throws Exception {
        mSocket1 = new Socket(ip,port);
        out = mSocket1.getOutputStream();
        reader = new BufferedInputStream(mSocket1.getInputStream());
        writer = new PrintWriter(new OutputStreamWriter(out));
        
        System.out.println("Server [" + port + "] connected.");
        
        this.doIt();
      }
    
    public String readAllMessage() throws IOException {
       String msg = "";
       int ch = 0;
       
       while((ch = reader.read()) != -1) {
          msg += (char)ch;
          if(reader.available() == 0) {
             break;
          }
       }
       return msg;
   }
    
    public void printMsg (String msg, String preFix) throws IOException {
      
      kindCode = "";
      fileCode = "";
      kindCode = msg.substring(19,23);
      fileCode = msg.substring(23,29);
      
      System.out.println(preFix + " kind [" + kindCode + "] fileCoode [" + fileCode + "] msg : [" + msg + "]");
      
    }
    
    public static String getCommand(String comment) throws Exception {
      
      String msg = "";
      if(comment == null || comment.equals("")) {
        System.out.println("get Input бщ");
      } else {
        System.out.println(comment + " бщ");
      }
      
      msg = scan.nextLine();
      
      return msg;
    }
    
    public void sendCms(String msg, int dataLengthSize) throws IOException {
        String data = String.format("%0" + dataLengthSize + "d", msg.getBytes().length) + msg;
        writer.write(data);
        writer.flush();
        
        System.out.println("send msg : [" + data + "]");
     }
    
    public static void main(String[] args) throws Exception {
        //String ip = getCommand("IP : ");
    	String ip = "192.168.20.72";
        //int port = Integer.parseInt(getCommand("Port : "));
    	int port = 8282;
    }
}
