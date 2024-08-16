//grupo 03:
// Isaias Elias da Silva
// Jardel Santos Nascimento
// Sergio Santana dos Santos

package br.ufs.dcomp.ExemploRabbitMQ;

import java.util.Scanner;

public class Chat {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("User: ");
        String userName = scanner.nextLine();

        Receptor receptor = new Receptor(userName);
        Thread receptorThread = new Thread(receptor);

        Thread emissorThread = new Thread(new Emissor(userName, receptor));

        emissorThread.start();
        receptorThread.start();
    }
}
