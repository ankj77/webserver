package com.innovation.web;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 * Created by ankur on 1/4/16.
 */
public class WebServer {
    final static Logger log = Logger.getLogger("WebServer");

    public static void main(String[] args) throws InterruptedException {
        System.out.println("fgfd");
        int index = 1;
        while (true){
            int port = 8000 ;

            log.info(" listening .. "+port);

            try {
                ServerSocket server = new ServerSocket(port);
                Socket socket = server.accept();
                Request httpRequest = new Request(server,socket,index );
                httpRequest.run();
//                Thread.sleep(1000);


            } catch (IOException e) {
                log.info( " ec "+e);
//                e.printStackTrace();
            }
            log.info(" process completer on  "+port);

            index++;

        }
    }
}
