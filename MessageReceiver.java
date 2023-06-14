package roteador;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageReceiver implements Runnable{
    private TabelaRoteamento tabela;
    private List<String> listaIp;
    private AtomicBoolean mudancaTabela;
    
    public MessageReceiver(TabelaRoteamento t, ArrayList<String> listaIp, AtomicBoolean mudancaTabela){
        tabela = t;
        this.listaIp = listaIp;
        this.mudancaTabela = mudancaTabela;
    }
    
    @Override
    public void run() {
        DatagramSocket serverSocket = null;
        Map<String, Long> vizinhos = new HashMap<>();
        listaIp.forEach(ip -> vizinhos.put(ip,System.currentTimeMillis()));
        
        try {
            
            /* Inicializa o servidor para aguardar datagramas na porta 5000 */
            serverSocket = new DatagramSocket(5000);
            serverSocket.setSoTimeout(30000);

        } catch (SocketException ex) {
            Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }

        byte[] receiveData = new byte[1024];
        
        while(true){
            remocaoInatividade(vizinhos);
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
                    //System.out.println("[Message receiver] Tabela recebida:" + tabela_string);

                    // ControleTabela.setIpAndTimeStamp(IPAddress.getHostAddress(), System.nanoTime());
                    tabela.update_tabela(tabela_string, IPAddress, mudancaTabela);
                    System.out.println(tabela.toString());
                    
                    String[] ipsReceived = tabela.getIps().split(";");
                    for(int i = 1; i < ipsReceived.length; i++){
                        System.out.println(i + ": " + ipsReceived[i]);
                        vizinhos.put(ipsReceived[i],System.currentTimeMillis());
                    }
                    //listaIp.forEach(ip -> vizinhos.put(ip,System.currentTimeMillis()));
        
                    
                }

            } catch (IOException ex) {
                Logger.getLogger(MessageReceiver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void remocaoInatividade(Map<String, Long> vizinhos) {
        long currentTime = System.currentTimeMillis();
        List<String> routesToRemove = new ArrayList<>();
    
        vizinhos.forEach((ip, lastConnection) -> {
            float tempo_segundos = (currentTime - lastConnection) / 1000F;
            System.out.println("lastConnection: " + lastConnection +  " | " + "tempo atual: " + tempo_segundos);
            if (tempo_segundos > 30) {
                tabela.removeVizinho(ip, mudancaTabela);
            }
        });
    }
}
