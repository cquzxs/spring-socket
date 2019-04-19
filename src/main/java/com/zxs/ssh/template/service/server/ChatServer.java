package com.zxs.ssh.template.service.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Project Name:spring-socket
 * File Name:ChatServer
 * Package Name:com.zxs.ssh.template.service.server
 * Date:2019/4/16
 * Author:zengxueshan
 * Description:
 * Copyright (c) 2019, 重庆云凯科技有限公司 All Rights Reserved.
 */

@Service("chatServer")
public class ChatServer {

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);

    //设备端发送消息
    public static final int DEVICE_SEND_MSG = 1;
    //服务端发送消息
    public static final int SERVICE_SEND_MSG = 2;

    private boolean started = false;
    private ServerSocket ss = null;
    private static Map<String, Object> soc = new HashMap<>();

    /**
     * 启动服务器
     */
    public void startChatServer(){
        try{
            ss = new ServerSocket(6666);
            started = true;
            logger.info("端口已开启....");
        }catch (Exception e){
            logger.error("启动socket服务器失败", e);
        }
        try{
            while (started) {
                final Socket s = ss.accept();
                Client c = new Client(s);
                Thread thread = new Thread(c);
                thread.start();
                logger.info("已与客户端 "+s.getInetAddress()+" 建立socket连接");
            }
        }catch (Exception e){

        } finally {
            try {
                ss.close();
                logger.info("socket 服务器已关闭");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 内部类：处理服务端与客户端连接
     */
    private class Client implements Runnable {
        private Socket s;
        private InputStream dis = null;
        private OutputStream dos = null;
        private boolean bConnected = false;

        public Client(Socket s) {
            this.s = s;
            try {
                dis = s.getInputStream();
                dos = s.getOutputStream();
                bConnected = true;
            } catch (Exception e) {
                logger.error("获取socket输入输出流异常", e);
            }
        }

        @Override
        public void run() {
            try{
                while (bConnected) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(dis));
                    boolean flag = true;
                    String message = null;
                    try {
                        message = br.readLine();
                        flag = true;
                    } catch (Exception e) {
                        flag = false;
                        logger.error("从socket输入流中读取数据异常", e);
                    }
                    if (flag) {
                        Map<String, Object> parse = (Map) JSONObject.parse(message.trim());
                        String deviceId = (String) parse.get("mac");
                        Integer type = (Integer) parse.get("type");
                        if (type == SERVICE_SEND_MSG) {  //转发服务端消息
                            Socket socClient = (Socket) soc.get(deviceId);
                            if (socClient != null) {
                                JSONObject json = new JSONObject(parse);
                                try {
                                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socClient.getOutputStream()));
                                    bw.write(json.toString() + "\n");
                                    bw.flush();
                                } catch (IOException e) {
                                    soc.remove(deviceId);
                                    logger.info("对方退出了！我从List里面去掉了！");
                                }
                            }
                        }else if(type == DEVICE_SEND_MSG){  //将消息加入到rabbitmq队列中
                            JSONObject object = JSON.parseObject(message);
                            String topic = object.getString("topic");
                            if ("call".equals(topic)) { //呼叫
                                logger.info("已将 call 消息 加入到rabbitmq队列："+object.toString());
                            } else if ("take_a_picture".equals(topic)) {  //拍照
                                logger.info("已将 take_a_picture 消息 加入到rabbitmq队列："+object.toString());
                            } else {   //人脸检测与识别结果上传中没有“topic”字段
                                logger.info("topic 内容格式不正确");
                            }
                        }else{
                            logger.error("客户端消息格式不正确");
                        }
                    }
                }
            }catch (Exception e){
                logger.error("");
            }
        }
    }

}
