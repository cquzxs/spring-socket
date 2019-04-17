package com.zxs.ssh.template.service.client;

import com.alibaba.fastjson.JSON;
import com.zxs.ssh.template.service.server.ChatServer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name:spring-socket
 * File Name:ChatClient
 * Package Name:com.zxs.ssh.template.service.client
 * Date:2019/4/16
 * Author:zengxueshan
 * Description:
 * Copyright (c) 2019, 重庆云凯科技有限公司 All Rights Reserved.
 */


public class ChatClient implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(ChatClient.class);

    private String deviceMac;
    private String msg;
    private String data;

    public ChatClient(String deviceMac, String msg, String data) {
        this.deviceMac = deviceMac;
        this.msg = msg;
        this.data = data;
    }

    @Override
    public void run() {
        try{
            Socket s = new Socket("127.0.0.1",3333);
            //构建IO
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            Map<String, Object> map = new HashMap<>();
            map.put("mac", deviceMac);
            map.put("type", ChatServer.SERVICE_SEND_MSG);
            map.put("data", data);
            map.put("topic", msg);
            map.put("timestamp", System.currentTimeMillis());
            JSONObject json =new JSONObject(map);
            String jsonString = json.toString()+"\n";
            //向服务器端发送一条消息
            bw.write(jsonString);
            bw.flush();
            //读取服务器返回的消息
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String mess = br.readLine();
            logger.info("接收到服务器消息：" + mess);
            s.close();
        }catch (Exception e){
            logger.error("客户端与服务器通信异常", e);
        }
    }
}
