package com.innovation.web;

import java.io.InputStreamReader;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 * Created by ankur on 1/4/16.
 */

public class HttpRequest implements Runnable {

    final static Logger log = Logger.getLogger("HttpRequest");

    Socket socket;
    int threadNum = 0;
    ServerSocket server;
    final static String CRLF = "\r\n";

    public HttpRequest(ServerSocket server, Socket socket, int threadNum) {
        this.socket = socket;
        this.threadNum = threadNum;
        this.server = server;

    }
// #ppender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
    public void run() {

        try {
            log.info(" processing thread number  " + threadNum);
            processRequest();


        } catch (Exception e) {
            log.info(" thred " + e);
        }
    }

    private void processRequest() throws Exception {

        InputStreamReader is = new InputStreamReader(socket.getInputStream());
        BufferedReader br = new BufferedReader(is);
        OutputStream os = socket.getOutputStream();
        String headerLine = null;

        int line = 0;
        int length =0 ;
        while ((headerLine = br.readLine()).length() != 0) {
            log.info(line+" ) -> "+ headerLine);


            if (headerLine.startsWith("Content-Length: ")) { // get the
                // content-length
                int index = headerLine.indexOf(':') + 1;
                String len = headerLine.substring(index).trim();
                length = Integer.parseInt(len);
                log.info(" post call lenthh  "+length);
            }

            line++;
        }

        StringBuffer body = new StringBuffer();
        if (length > 0) {
            int read;
            while ((read = br.read()) != -1) {
                body.append((char) read);
                if (body.length() == length)
                    break;
            }
        }

        log.info(" body if post call "+body);
        log.info("Writing response...");
        // need to construct response bytes first
        byte[] response = "<html><body>Hello World</body></html>".getBytes("ASCII");

        String statusLine = "HTTP/1.1 200 OK" + CRLF;
        String contentLength = "Content-Length: " + response.length + CRLF;

        os.write(statusLine.getBytes("ASCII"));
        os.write(contentLength.getBytes("ASCII"));
        // signal end of headers
        os.write(CRLF.getBytes("ASCII"));
        // write actual response and flush
        os.write(response);
        os.flush();
        os.close();
        br.close();
        socket.close();
        server.close();
    }
}
