package com.zxs.ssh.template.service.client;

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


public class TestClient {
    public static void main(String args[]) throws Exception {
        // 要连接的服务端IP地址和端口
        String host = "127.0.0.1";
        int port = 55533;
        // 与服务端建立连接
        Socket socket = new Socket(host, port);
        // 建立连接后获得输出流
        OutputStream outputStream = socket.getOutputStream();
        String message="你好  yiwangzhibujian";
        socket.getOutputStream().write(message.getBytes("UTF-8"));
        outputStream.close();
        socket.close();
    }
}
