import javax.swing.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;


public class Client extends JFrame{
     private JTextField userText;
     private JTextArea chatWindow;
     private ObjectOutputStream output;
     private ObjectInputStream input;
     private String message = "";
     private String serverIP;
     private Socket connection;

     public Client(String host){
         super("Instant Chet");
         serverIP = host;
         userText = new JTextField();
         userText.setEditable(false);
         userText.addActionListener(
                 event -> {
                     sendMessage(event.getActionCommand());
                     userText.setText("");

                 }
        );

         add(userText, BorderLayout.NORTH);
         chatWindow = new JTextArea();
         chatWindow.setEditable(false);
         add(new JScrollPane(chatWindow),BorderLayout.CENTER);
         setSize(600,400);
         setVisible(true);
     }

     public void startRunning(){
         try {
             connectToServer();
             setupStreams();
             whileChatting();
         }catch (EOFException eofException){
             showMessage("\n Client Terminated Connection");
         }catch(IOException ioException){
             ioException.printStackTrace();
         }finally{
             closeCrap();
         }
     }

     //Connects CLient to Server

    private void connectToServer() throws IOException{
         showMessage("Attempting Connection...");
         connection = new Socket(InetAddress.getByName(serverIP), 4222);
         showMessage(" Connected to: " + connection.getInetAddress().getHostName());

    }

    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams Setup \n ");
    }

    private void whileChatting() throws IOException{
         ableToType(true);
         do {
             try {
                message = (String) input.readObject();
                showMessage("\n" + message);
             }catch (ClassNotFoundException classNotFoundException){
                 showMessage("\n Unknown Data");
             }

         }while(!message.equals("Server - End"));
    }

    private void closeCrap(){
         showMessage("\n Closing Chat Client");
         ableToType(false);
         try {
             output.close();
             input.close();
             connection.close();
         }catch (IOException ioException){
             ioException.printStackTrace();
         }
    }

    private void sendMessage(String message){
        try{
            output.writeObject("Client - " + message);
            output.flush();
            showMessage("\n Client -" + message);
        }catch(IOException ioException){
            chatWindow.append("\n ERROR: CAN'T SEND MESSAGE ");
        }
    }

    private void showMessage(final String message){
        SwingUtilities.invokeLater(
                () -> chatWindow.append(message)
        );
    }
    //Give Client Permission to use text box
    private void ableToType(final boolean bool){
        SwingUtilities.invokeLater(
                () -> userText.setEditable(bool)
        );
    }

}
