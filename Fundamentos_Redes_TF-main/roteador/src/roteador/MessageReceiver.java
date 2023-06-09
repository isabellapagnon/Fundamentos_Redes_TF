package roteador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageReceiver implements Runnable{
    private TabelaRoteamento tabela;
    
    public MessageReceiver(TabelaRoteamento t){
        tabela = t;
    }
    
    @Override
    public void run() {
        DatagramSocket serverSocket = null;
        
        try {
            
            /* Inicializa o servidor para aguardar datagramas na porta 5000 */
            serverSocket = new DatagramSocket(5000);
        } catch (SocketException ex) {
            Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        byte[] receiveData = new byte[1024];
        
        while(true){
            
            /* Cria um DatagramPacket */
            DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);
            
            try {
                /* Resetamndo o tamanho dos dados do pacote antes de receber */
                 receivedPacket.setLength(receiveData.length);
                /* Aguarda o recebimento de uma mensagem */
                serverSocket.receive(receivedPacket);
                
                if (receivedPacket.getData().length > 0) {
                    /* Obtem o IP de origem da mensagem */
                    InetAddress IPAddress = receivedPacket.getAddress();
                    System.out.println("Mensagem recebida do ip: " + IPAddress);

                    /* Transforma a mensagem em string */
                    String tabela_string = new String(receivedPacket.getData());
                    System.out.println("[Message receiver] Tabela recebida:" + tabela_string);
                    System.out.println("[Message receiver] Tabela pessoal:" + tabela.get_tabela_string());

                    ControleTabela.setIpAndTimeStamp(IPAddress.getHostAddress(), System.nanoTime());
                    tabela.update_tabela(tabela_string, IPAddress);
                }

            } catch (IOException ex) {
                Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
