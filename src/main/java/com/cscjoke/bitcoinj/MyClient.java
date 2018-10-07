package com.cscjoke.bitcoinj;

import com.cscjoke.bitcoinj.bean.Block;
import com.cscjoke.bitcoinj.bean.MessageBean;
import com.cscjoke.bitcoinj.bean.NoteBook;
import com.cscjoke.bitcoinj.bean.Transaction;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.util.StringUtils;

import java.awt.print.Book;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class MyClient extends WebSocketClient {
    private  String name;
    public MyClient(URI serverUri, String name) {
        super(serverUri);
        this.name = name;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("WebSocket客户端__" + name + "_连接成功");

    }

    @Override
    public void onMessage(String message) {
        System.out.println("WebSocket客户端__" + name + "_收到了消息, 内容是: "+ message);

        try {
            if(!StringUtils.isEmpty(message)){
                ObjectMapper objectMapper = new ObjectMapper();
                MessageBean bean = objectMapper.readValue(message, MessageBean.class);
                NoteBook book = NoteBook.getInstance();

                //判断消息类型 1 是同步数据
                if (bean.getType() == 1){
                    JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Block.class);
                    ArrayList<Block> list = objectMapper.readValue(bean.getMsg(), javaType);

                    //与自己本地的数据进行对比
                    book.compareList(list);
                    //如果消息类型为2 是传过来的交易数据 校验后保存到本地
                } else if (bean.getType() == 2){
                    //验证交易数据
                    Transaction transaction = objectMapper.readValue(bean.getMsg(), Transaction.class);
                    if (transaction.verify()){
                        //交易数据校验通过 存入区块中
                        book.addNote(bean.getMsg());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket客户端__" + name + "_连接关闭");

    }

    @Override
    public void onError(Exception ex) {
        System.out.println("WebSocket客户端__" + name + "_发生了错误 内容是: "+ex.getMessage());

    }
}
