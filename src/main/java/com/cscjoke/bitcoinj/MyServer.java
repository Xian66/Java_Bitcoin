package com.cscjoke.bitcoinj;

import com.cscjoke.bitcoinj.bean.Block;
import com.cscjoke.bitcoinj.bean.MessageBean;
import com.cscjoke.bitcoinj.bean.NoteBook;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class MyServer extends WebSocketServer {

    private  int port;
    public MyServer(int port){
        super(new InetSocketAddress(port));
        this.port = port;
    }
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("WebSocket服务器__" + port + "__打开了一个连接,对方是:" + conn.getRemoteSocketAddress().toString());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("WebSocket服务器__" + port + "__关闭了一个连接,对方是:" + conn.getRemoteSocketAddress().toString());

    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("WebSocket服务器__" + port + "__收到了消息,对方是:" + conn.getRemoteSocketAddress().toString()+"消息内容是: "+message);

        try {
            if ("把你的区块链数据给我一份".equals(message)){
                //获取本地的区块链数据
                NoteBook noteBook = NoteBook.getInstance();
                ArrayList<Block> list = noteBook.showList();
                ObjectMapper objectMapper = new ObjectMapper();
                String blockChainData = objectMapper.writeValueAsString(list);
                MessageBean messageBean = new MessageBean(1, blockChainData);
                String msg = objectMapper.writeValueAsString(messageBean);
                broadcast(msg);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.out.println("WebSocket服务器__" + port + "__发生了错误 原因是: " + ex.getMessage());

    }

    @Override
    public void onStart() {
        System.out.println("WebSocket服务器__" + port + "启动成功");

    }

    public void startServer(){
        new Thread(this).start();
    }
}
