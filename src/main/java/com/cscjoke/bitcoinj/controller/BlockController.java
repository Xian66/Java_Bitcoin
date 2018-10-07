package com.cscjoke.bitcoinj.controller;

import com.cscjoke.bitcoinj.BitcoinjApplication;
import com.cscjoke.bitcoinj.MyClient;
import com.cscjoke.bitcoinj.MyServer;
import com.cscjoke.bitcoinj.bean.NoteBook;
import com.cscjoke.bitcoinj.bean.Block;
import com.cscjoke.bitcoinj.bean.MessageBean;
import com.cscjoke.bitcoinj.bean.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;

//添加网络访问接口
@RestController
public class BlockController {

    private NoteBook noteBook = new NoteBook();

    @RequestMapping(value = "/addGenesis", method = RequestMethod.POST)
    private String addGenesis(String genesis){

        try {
            noteBook.addGenesis(genesis);
            return "添加成功";
        } catch (Exception e) {
            e.printStackTrace();
            return "failed" + e.getMessage();
        }
    }

    @RequestMapping(value = "/addNote", method = RequestMethod.POST)
    private String addNote(Transaction transaction){

        try {
            //检验交易数据
            if (transaction.verify()){
                //将交易数据转换为字符串
                ObjectMapper objectMapper = new ObjectMapper();
                String transactionStr = objectMapper.writeValueAsString(transaction);
                //把交易信息包装成messagebean类 添加了消息类型
                MessageBean messageBean = new MessageBean(2, transactionStr);
                String msg = objectMapper.writeValueAsString(messageBean);
                server.broadcast(msg);
                //添加数据
                noteBook.addNote(transactionStr);
                return "添加交易成功";
            } else {
                throw  new RuntimeException("交易数据校验失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "failed" + e.getMessage();
        }
    }

    @RequestMapping(value = "/showlist", method = RequestMethod.GET)
    private ArrayList<Block> showList(){

        ArrayList<Block> arrayList = noteBook.showList();
        System.out.println("文件中的list内容是: "+arrayList);
        return arrayList;
    }

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public String check(){
        String check = noteBook.check();

        if (StringUtils.isEmpty(check)){
            return "数据校验通过";
        }

        return check;
    }

    private MyServer server;

    @PostConstruct //创建Controller后调用该方法
    //创建服务器, 开启服务器
    public void init(){
        //设置websocket服务器占用的端口号 = spring boot占用端口号+1
        server = new MyServer(Integer.parseInt(BitcoinjApplication.port) + 1);
        server.startServer();
    }
    private HashSet<String> set = new HashSet<>();

    //注册节点
    @RequestMapping("/regist")
    public String regist(String port){
        set.add(port);
        return "节点: " + port + "注册成功";
    }

    private ArrayList<MyClient> clients = new ArrayList<>();

    //连接其它节点
    @RequestMapping("/conn")
    public String conn(){
        try {
            for (String s : set){
                URI uri = new URI("ws://localhost:" + s);
                MyClient client = new MyClient(uri,"连接到__"+s+"__服务器的客户端");
                client.connect();
                clients.add(client);
            }
            return "连接成功";
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "连接失败";
        }
    }

    @RequestMapping("/broadcast")
    public String broadcast(String msg) {
        server.broadcast(msg);
        return "消息广播成功";
    }

    //跟其它节点同步数据
    @RequestMapping("/syncData")
    public String syncData(){
        for (MyClient client : clients) {
            client.send("把你的区块链数据给我一份");
        }
        return "广播成功";
    }

}
