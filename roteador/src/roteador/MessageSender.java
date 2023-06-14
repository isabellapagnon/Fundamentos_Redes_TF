package roteador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageSender implements Runnable{
    TabelaRoteamento tabela; /*Tabela de roteamento */
    ArrayList<String> vizinhos; /* Lista de IPs dos roteadores vizinhos */
    Semaphore semaforo; 
    AtomicBoolean mudancaTabela;
    
    public MessageSender(TabelaRoteamento t, ArrayList<String> v, Semaphore semaforo, AtomicBoolean mudancaTabela){
        tabela = t;
        vizinhos = v;
        this.semaforo = semaforo;
        this.mudancaTabela = mudancaTabela;
    }
    
    @Override
    public void run() {
        DatagramSocket clientSocket = null;
        byte[] sendData;
        InetAddress IPAddress = null;
        
        /* Cria socket para envio de mensagem */
        try {
            clientSocket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(MessageSender.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        while(true){
            
            /* Pega a tabela de roteamento no formato string, conforme especificado pelo protocolo. */
            String tabela_string = tabela.toString();
               
            /* Converte string para array de bytes para envio pelo socket. */
            sendData = tabela_string.getBytes();
            
            /* Anuncia a tabela de roteamento para cada um dos vizinhos */
            for (String ip : vizinhos){
                /* Converte string com o IP do vizinho para formato InetAddress */
                try {
                    IPAddress = InetAddress.getByName(ip);
                } catch (UnknownHostException ex) {
                    Logger.getLogger(MessageSender.class.getName()).log(Level.SEVERE, null, ex);
                    continue;
                }
                
                /* Configura pacote para envio da menssagem para o roteador vizinho na porta 5000*/
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 5000);   
                      
                
                /* Realiza envio da mensagem. */
                try {
                    clientSocket.send(sendPacket);
                    System.out.println("Enviando a mensagem para:  " + IPAddress);
                } catch (IOException ex) {
                    Logger.getLogger(MessageSender.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            /* Espera 10 segundos antes de realizar o próximo envio. CONTUDO, caso
             * a tabela de roteamento sofra uma alteração, ela deve ser reenvida aos
             * vizinho imediatamente.
             */
            try {
                Thread.sleep(10000);
                //semaforo.tryAcquire(10, TimeUnit.SECONDS);// usando semaforo para garantir multithreading
            } catch (InterruptedException ex) {
                Logger.getLogger(MessageSender.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
    }
    
}
