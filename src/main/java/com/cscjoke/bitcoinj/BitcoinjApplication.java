package com.cscjoke.bitcoinj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.util.Scanner;

@SpringBootApplication
public class BitcoinjApplication {
    public static String port ;
    public static void main(String[] args) {
        System.out.println("输入要运行的端口号: ");
        Scanner scanner = new Scanner(System.in);
        port = scanner.nextLine();
        new SpringApplicationBuilder(BitcoinjApplication.class).properties("server.port=" + port).run(args);
//    SpringApplication.run(BitcoinjApplication.class, args);
    }
}
