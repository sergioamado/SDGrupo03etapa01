package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

public class Emissor implements Runnable {
    private final static String EXCHANGE_NAME = "chat_exchange";
    private final String userName;
    private String currentRecipient;
    private Receptor receptor;

    public Emissor(String userName, Receptor receptor) {
        this.userName = userName;
        this.currentRecipient = "";
        this.receptor = receptor;
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.252"); //
        factory.setUsername("admin"); //
        factory.setPassword("password");
        factory.setVirtualHost("/"); 

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            Scanner scanner = new Scanner(System.in);

            System.out.print(">> ");
            while (true) {
                String input = scanner.nextLine();

                if (input.startsWith("@")) {
                    currentRecipient = input.substring(1).trim();
                    receptor.setCurrentRecipient(currentRecipient); // Update receiver with the new recipient
                   // System.out.println("Switched to chat with " + currentRecipient);
                    System.out.print("@" + currentRecipient + ">> ");
                } else {
                    if (currentRecipient.isEmpty()) {
                        System.out.println("Please specify a recipient using @username.");
                        System.out.print(">> ");
                    } else {
                        String fullMessage = userName + ": " + input;
                        channel.basicPublish(EXCHANGE_NAME, currentRecipient, MessageProperties.PERSISTENT_TEXT_PLAIN, fullMessage.getBytes("UTF-8"));
                       // System.out.println(" [x] Sent to " + currentRecipient + ": '" + fullMessage + "'");
                        System.out.print("@" + currentRecipient + ">> ");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
