package com.zxs.ssh.template.service.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Project Name:spring-socket
 * File Name:TestServer
 * Package Name:com.zxs.ssh.template.service.server
 * Date:2019/4/17
 * Author:zengxueshan
 * Description:
 * Copyright (c) 2019, 重庆云凯科技有限公司 All Rights Reserved.
 */


public class Test2Server {

    public static void main(String[] args) throws Exception {
        //main1();
        main2();
    }
    private static void main2() throws Exception{
        // 监听指定的端口
        int port = 55533;
        ServerSocket server = new ServerSocket(port);

        // server将一直等待连接的到来
        System.out.println("server将一直等待连接的到来");
        Socket socket = server.accept();
        // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
        InputStream inputStream = socket.getInputStream();
        byte[] bytes;
        // 因为可以复用Socket且能判断长度，所以可以一个Socket用到底
        while (true) {
            // 首先读取两个字节表示的长度
            int first = inputStream.read();
            System.out.println(first);
            //如果读取的值为-1 说明到了流的末尾，Socket已经被关闭了，此时将不能再去读取
            if(first==-1){
                break;
            }
            int second = inputStream.read();
            System.out.println(second);
            int length = (first << 8) + second;
            // 然后构造一个指定长的byte数组
            bytes = new byte[length];
            // 然后读取指定长度的消息即可
            inputStream.read(bytes);
            System.out.println("get message from client: " + new String(bytes, "UTF-8"));
        }
        inputStream.close();
        socket.close();
        server.close();

    }
    private static void main1() throws Exception{
        // 监听指定的端口
        int port = 55533;
        ServerSocket server = new ServerSocket(port);

        // server将一直等待连接的到来
        System.out.println("【系统】server将一直等待连接的到来");
        Socket socket = server.accept();
        // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
        System.out.println("【系统】客户端"+socket.getLocalPort()+"已连接");
        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];
        int len;
        StringBuilder sb = new StringBuilder();
        //只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
        while ((len = inputStream.read(bytes)) != -1) {
            // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
            sb.append(new String(bytes, 0, len, "UTF-8"));
        }
        System.out.println("【client " + socket.getLocalPort()+"】"+sb);

        OutputStream outputStream = socket.getOutputStream();
        outputStream.write("Hello Client,I get the message.".getBytes("UTF-8"));
        System.out.println("【server】" + "Hello Client,I get the message.");

        inputStream.close();
        outputStream.close();
        System.out.println("【系统】client "+socket.getLocalPort()+"已关闭");
        socket.close();
        System.out.println("【系统】server已关闭");
        server.close();
    }
}
