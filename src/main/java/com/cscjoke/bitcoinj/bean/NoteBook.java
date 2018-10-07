package com.cscjoke.bitcoinj.bean;

import com.cscjoke.bitcoinj.utils.HashUtils;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class NoteBook {

    public static volatile NoteBook instance;

    public NoteBook(){
        init();
    }

    private ArrayList<Block> list = new ArrayList<>();
    public void addGenesis(String genesis){
        String preHash = "0000000000000000000000000000000000000000000000000000000000000000";
        int nonce = mine(preHash+genesis);
        Block block = new Block(list.size()+1,genesis,HashUtils.sha256(nonce+preHash+genesis), nonce, preHash);
        if (list.size() > 0){
            throw new RuntimeException("要是全新的账本才能更改封面");
        }else {
            list.add(block);
        }

        save2disk();
    }

    public void addNote(String note){
        String preHash = list.get(list.size()-1).hash;
        int nonce = mine(preHash + note);
        Block block = new Block(list.size()+1,note,HashUtils.sha256(nonce+preHash+note), nonce,preHash);
        if (list.size() < 1){
            throw new RuntimeException("添加区块前要添加创世区块");
        }else {
            list.add(block);
        }
        save2disk();
    }

    public ArrayList<Block> showList(){
        return list;
    }

    public void save2disk() {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("a.json");
        try {
            objectMapper.writeValue(file,list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init(){
        File file = new File("a.json");
        if (file.exists() && file.length() > 0){

            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, Block.class);
            try {
                list = objectMapper.readValue(file, javaType);
                System.out.println(list);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //校验数据

    public String check() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Block currentBlock = list.get(i);
            String currentBlockHash = currentBlock.hash;
            String content = currentBlock.content;
            int id = currentBlock.id;
            int nonce = currentBlock.nonce;
            String preBlockHash = currentBlock.preHash;
            if (i == 0) {
                preBlockHash = "0000000000000000000000000000000000000000000000000000000000000000";
                String calculateHash = HashUtils.sha256(nonce + preBlockHash + content);
                if (!calculateHash.equals(currentBlockHash)) {
                    sb.append("第" + id + "个区块校验失败");
                }
            } else {
                //校验当前区块的hash
                String calculateHash = HashUtils.sha256(nonce + preBlockHash + content);
                if (!calculateHash.equals(currentBlockHash)) {
                    sb.append("第" + id + "个区块校验失败");
                }

                //校验前一个区块的哈希

                Block preBlock = list.get(i - 1);
                String preHash = preBlock.hash;

                if (!preBlockHash.equals(preHash)) {
                    sb.append("第" + (i - 1) + "个区块hash校验失败");
                }
            }

        }
        return sb.toString();
    }

    private int mine(String content){
        //求取一个符合特定规则的hash值, 并将运算次数返回
        for (int i = 0; i < Integer.MAX_VALUE; i++){
            String s = HashUtils.sha256(i+content);
            if (s.startsWith("00")){
                System.out.println("找到了符合要求的nonce值" + i);
                return i;
            }
                System.out.println("没找到符合要求的nonce值, 最后的随机数为: "+i);

        }
        throw new RuntimeException("没找到符合要求的nonce值, 请重新构建区块");
    }

    public static NoteBook getInstance(){
        if (instance == null){
            synchronized (NoteBook.class) {
                if (instance == null) {
                    instance = new NoteBook();
                }
            }
        }
        return instance;
    }


    public void compareList(ArrayList<Block> newList) {
        System.out.println("传入参数的长度为: "+newList.size());
        //如果别人的广播的数据长度比本地保存的长就把广播过来的数据保存到本地
        if (newList.size() > list.size()) {
            list = newList;
        }

        System.out.println("同步后的数据长度为: "+list.size());
    }
}
