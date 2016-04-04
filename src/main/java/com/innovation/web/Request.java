package com.innovation.web;

import java.io.InputStreamReader;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;

/**
 * Created by ankur on 1/4/16.
 */

public class Request implements Runnable {

    final static Logger log = Logger.getLogger("HttpRequest");

    Socket socket;
    int threadNum = 0;
    ServerSocket server;
    final static String CRLF = "\r\n";
    public static final String CONTENT_LENGTH = "Content-Length";
    String requestUri;
    String method;
    Map<String, String> headers;
    byte[] data;
    StringBuffer bodyBuffer = null;


    public Request(ServerSocket server, Socket socket, int threadNum) {
        this.socket = socket;
        this.threadNum = threadNum;
        this.server = server;

    }

    // #ppender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
    public void run() {

        try {
            log.info(" processing thread number  " + threadNum);
            processHttpRequest();
        } catch (Exception e) {
            log.info(" thred " + e);
        }
    }

    private void processHttpRequest() throws Exception {
        InputStreamReader is = new InputStreamReader(socket.getInputStream());
        OutputStream os = socket.getOutputStream();

        log.info("parser request");
        parseRequest(is);
        log.info("parser request done ");


        log.info(" os  ");
        log.info("handle request done ");
        String bodyStarts = "<html><body><table>";
        String bodyEnds = "</table></body></html>";

/*
*     String requestUri;
    String method;
    Map<String, String> headers;
    byte[] data;
    StringBuffer bodyBuffer = null;*/

        String bodyMiddle = "<tr>"+"Request URI :" + requestUri+"</tr>";
        bodyMiddle +="<tr>"+ "Request Method :" + method+"</tr>";

        for (Map.Entry<String,String> entry : headers.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            bodyMiddle +="<tr>"+ key+":" + value+"</tr>";
        }
        bodyMiddle +="<tr>"+ "Request Body :" + bodyBuffer+"</tr>";

        String body = bodyStarts + bodyMiddle + bodyEnds;
        byte[] response = body.getBytes("ASCII");

        String statusLine = "HTTP/1.1 200 OK" + CRLF;
        String contentLength = "Content-Length: " + response.length + CRLF;
        os.write(statusLine.getBytes("ASCII"));
        os.write(contentLength.getBytes("ASCII"));
        os.write(CRLF.getBytes("ASCII"));
        os.write(response);
        os.flush();
        os.close();
        socket.close();
        server.close();


    }


//    private void processRequest() throws Exception {
//
//        InputStreamReader is = new InputStreamReader(socket.getInputStream());
//        BufferedReader br = new BufferedReader(is);
//        OutputStream os = socket.getOutputStream();
//        String headerLine = null;
//
//        int line = 0;
//        int length = 0;
//        while ((headerLine = br.readLine()).length() != 0) {
//            log.info(line + " ) -> " + headerLine);
//
//
//            if (headerLine.startsWith("Content-Length: ")) { // get the
//                // content-length
//                int index = headerLine.indexOf(':') + 1;
//                String len = headerLine.substring(index).trim();
//                length = Integer.parseInt(len);
//                log.info(" post call lenthh  " + length);
//            }
//
//            line++;
//        }
//
//        StringBuffer body = new StringBuffer();
//        if (length > 0) {
//            int read;
//            while ((read = br.read()) != -1) {
//                body.append((char) read);
//                if (body.length() == length)
//                    break;
//            }
//        }
//
//        log.info(" body if post call " + body);
//        log.info("Writing response...");
//        // need to construct response bytes first
//        byte[] response = "<html><body>Hello World</body></html>".getBytes("ASCII");
//
//        String statusLine = "HTTP/1.1 200 OK" + CRLF;
//        String contentLength = "Content-Length: " + response.length + CRLF;
//
//        os.write(statusLine.getBytes("ASCII"));
//        os.write(contentLength.getBytes("ASCII"));
//        // signal end of headers
//        os.write(CRLF.getBytes("ASCII"));
//        // write actual response and flush
//        os.write(response);
//        os.flush();
//        os.close();
//        br.close();
//        socket.close();
//        server.close();
//    }


    protected void parseRequest(InputStreamReader inputStream) throws Exception {

        BufferedReader br = new BufferedReader(inputStream);
        String firstLine = br.readLine();
        requestUri = (firstLine.split(" ", 3)[1]);
        method = extractMethod(firstLine);
        headers = new HashMap<String, String>();
        String nextLine = "";

//        while( !(nextLine = br.readLine()).equals("") )
        while ((nextLine = br.readLine()).length() != 0) {
            String values[] = nextLine.split(":", 2);
            headers.put(values[0], values[1].trim());
        }
        if (headers.containsKey(CONTENT_LENGTH)) {
            int size = Integer.parseInt(headers.get(CONTENT_LENGTH));
            data = new byte[size];
            bodyBuffer = new StringBuffer();
            log.info(" body length " + size);
            if (size > 0) {
                int read;
                while ((read = br.read()) != -1) {
                    bodyBuffer.append((char) read);
                    if (bodyBuffer.length() == size)
                        break;
                }
            } else {
                data = null;
            }
            log.info("body is " + bodyBuffer);


        }
//        br.close();

    }

//    protected void handleRequest(OutputStream os) throws Exception {
//
//        String bodyStarts = "<html><body>";
//        String bodyEnds = "</body></html>";
//        String bodyMiddle = "Hello World";
//        String body = bodyStarts + bodyMiddle + bodyEnds;
//        byte[] response = body.getBytes("ASCII");
//        String statusLine = "HTTP/1.1 200 OK" + CRLF;
//        String contentLength = "Content-Length: " + response.length + CRLF;
//        os.write(statusLine.getBytes("ASCII"));
//        os.write(contentLength.getBytes("ASCII"));
//        os.write(CRLF.getBytes("ASCII"));
//        os.write(response);
//        os.flush();
//        os.close();
////        socket.close();
////        server.close();
//    }

    public static String extractMethod(String headerLine) {
        String method = headerLine.split(" ")[0];
        if (method != null) {
            return method;
        } else {
            return null;
        }
    }

}
