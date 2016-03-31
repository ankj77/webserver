package com.innovation.web;

import java.io.InputStreamReader;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

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

        while ((headerLine = br.readLine()).length() != 0) {
            log.info(headerLine);
        }
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
