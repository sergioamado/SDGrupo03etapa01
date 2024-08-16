package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.*;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Receptor implements Runnable {
    private final static String EXCHANGE_NAME = "chat_exchange";
    private final String userName;
    private String currentRecipient;

    public Receptor (String userName) {
        this.userName = userName;
        this.currentRecipient = "";
    }

    @Override
    public void run() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.252"); //
        factory.setUsername("admin"); //
        factory.setPassword("password");
        factory.setVirtualHost("/"); 

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, "direct");
            String queueName = channel.queueDeclare(userName, false, false, false, null).getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, userName);

           // System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                String emissor = message.split(":")[0];
                String content = message.substring(message.indexOf(":") + 2);

                String timeStamp = new SimpleDateFormat("dd/MM/yyyy 'às' HH:mm").format(new Date());
                System.out.println("\n(" + timeStamp + ") " + emissor + " diz: " + content);

                // Restore the prompt to the current recipient
                System.out.print("@" + currentRecipient + ">> ");
            };
            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCurrentRecipient(String recipient) {
        this.currentRecipient = recipient;
    }
}
