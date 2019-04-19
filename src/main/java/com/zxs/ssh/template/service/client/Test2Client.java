package com.zxs.ssh.template.service.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Project Name:spring-socket
 * File Name:TestClient
 * Package Name:com.zxs.ssh.template.service.client
 * Date:2019/4/17
 * Author:zengxueshan
 * Description:
 * Copyright (c) 2019, 重庆云凯科技有限公司 All Rights Reserved.
 */


public class Test2Client {
    public static void main(String args[]) throws Exception {
        main1();
        //main2();
    }

    private static void main2() throws Exception{
        // 要连接的服务端IP地址和端口
        String host = "127.0.0.1";
        int port = 55533;
        // 与服务端建立连接
        Socket socket = new Socket(host, port);
        // 建立连接后获得输出流
        OutputStream outputStream = socket.getOutputStream();
        String message = "你好  yiwangzhibujian";
        //首先需要计算得知消息的长度
        byte[] sendBytes = message.getBytes("UTF-8");
        //然后将消息的长度优先发送出去
        outputStream.write(sendBytes.length >>8);
        outputStream.write(sendBytes.length);
        //然后将消息再次发送出去
        outputStream.write(sendBytes);
        outputStream.flush();
        //==========此处重复发送一次，实际项目中为多个命名，此处只为展示用法
        message = "第二条消息";
        sendBytes = message.getBytes("UTF-8");
        outputStream.write(sendBytes.length >>8);
        outputStream.write(sendBytes.length);
        outputStream.write(sendBytes);
        outputStream.flush();
        //==========此处重复发送一次，实际项目中为多个命名，此处只为展示用法
        message = "the third message!";
        sendBytes = message.getBytes("UTF-8");
        outputStream.write(sendBytes.length >>8);
        outputStream.write(sendBytes.length);
        outputStream.write(sendBytes);

        outputStream.close();
        socket.close();
    }

    private static void main1() throws Exception{
        // 要连接的服务端IP地址和端口
        String host = "127.0.0.1";
        int port = 55533;
        // 与服务端建立连接
        Socket socket = new Socket(host, port);
        // 建立连接后获得输出流
        OutputStream outputStream = socket.getOutputStream();
        String message = "你好  yiwangzhibujian";
        socket.getOutputStream().write(message.getBytes("UTF-8"));
        //通过shutdownOutput高速服务器已经发送完数据，后续只能接受数据
        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];
        int len;
        StringBuilder sb = new StringBuilder();
        while ((len = inputStream.read(bytes)) != -1) {
            //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
            sb.append(new String(bytes, 0, len,"UTF-8"));
        }
        System.out.println("get message from server: " + sb);

        inputStream.close();
        outputStream.close();
        socket.close();
    }
}
