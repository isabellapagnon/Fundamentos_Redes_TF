package roteador;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TabelaRoteamento {
    /*
     * Implemente uma estrutura de dados para manter a tabela de roteamento.
     * A tabela deve possuir: IP Destino, Métrica e IP de Saída.
     */

    public List<Vizinho> parametroVizinhos;
    Semaphore semaforo;

    public TabelaRoteamento(Semaphore s) {
        parametroVizinhos = new ArrayList<>();
        semaforo = s;
    }

    public void update_tabela2(String tabela_s, InetAddress IPAddress) {
        /* Atualize a tabela de rotamento a partir da string recebida. */

        System.out.println(IPAddress.getHostAddress() + ": " + tabela_s);

        int metrica = 0; // atualizar depois para ir contando os pulos
        
        boolean exist = false;
        for (int i = 0; i < parametroVizinhos.size(); i++) {
            if (parametroVizinhos.get(i).ip == IPAddress.getHostAddress()) {
                // ja possui na tabela de roteamento
                exist = true;

                // se encontra um com valor menor de pulos
                if (metrica < parametroVizinhos.get(i).metrica) {
                    parametroVizinhos.get(i).setIpSaida(tabela_s);
                    parametroVizinhos.get(i).setMetrica(metrica);
                }
            }
        }

        if (!exist) {
            parametroVizinhos.add(new Vizinho(IPAddress.getHostAddress(), metrica, tabela_s));
        }

    }

    public void update_tabela(String tabela_s,  InetAddress IPAddress) {
        /* Atualize a tabela de rotamento a partir da string recebida. */

        System.out.println(IPAddress.getHostAddress() + ": " + tabela_s);

        int metrica = 0; // atualizar depois para ir contando os pulos
    
        // Caso a tabela recebida contenha ! 
        if (tabela_s.contains("!")) {
            
            // Itera sobre as entradas e verfica se o Ip que enviou já esta cadastrado na tabela     
            boolean exist = false;
            for (int i = 0; i < parametroVizinhos.size(); i++) {
                if (parametroVizinhos.get(i).ip == IPAddress.getHostAddress()) {
                    // ja possui na tabela de roteamento
                    exist = true;
                }
            }

            // Caso o ip não exista, adiciona o Ip de quem enviou como Ip de saída como destino e como IP de destino
            if (!exist) {
                Vizinho novaEntrada = new Vizinho(IPAddress.getHostAddress(), 1, IPAddress.getHostAddress());
                this.parametroVizinhos.add(novaEntrada);
                semaforo.release();
            }
       
        } 
        
        // Caso a tabela contenha * 
        if (tabela_s.contains("*")) { 
            
            // Divide as linhas usando o marcador *
            String[] lines = tabela_s.split("\\*");  

            
            // Itera sobre as linhas 
            for (int i = 0; i<lines.length; i++) {
                
                // O if ignora linhas vazias 
                if (!lines[i].equals("")) {
                    
                        // Separa a linha em colunas utilizando o marcador ; 
                        String[] columns = lines[i].split(";");

                        // Pegando a métrica da linha da tabela correspondente e transforma em inteiro
                        char aux = columns[1].charAt(0);
                        int met = Integer.parseInt(String.valueOf(aux));

                        // Caso a tabela do roteador esteja vazia adiciona o primeiro da tabela para continuar a iteração
                        if (this.parametroVizinhos.isEmpty()) {
                            
                            Vizinho novaEntrada = new Vizinho(columns[0], met, IPAddress.getHostAddress());
                            this.parametroVizinhos.add(novaEntrada);
                            semaforo.release();
                        
                        // Caso não esteja vazio performa o seguinte : 
                        } else {

                                // Cria booleano para verificar alterações e nova entrada para adicionar caso seja necessário
                                boolean deveAdicionar = false; 
                                Vizinho novaEntrada = new Vizinho(columns[0], met, IPAddress.getHostAddress());
                                
                                // Itera sobre todas as entradas da tabela de roteamento atual
                                // Altera o booleano caso deva adicionar na tabela
                                for (Vizinho entrada : this.parametroVizinhos) {
                                    
                                      // Verifica se o IP destino recebido não é igual ao que existe na tabela 
                                      if(!(entrada.getIp().equals(columns[0]))) {
                                          
                                          // Caso não exista soma a metrica da nova entrada em mais 1 e marcar o booleano para adicionar como true
                                          novaEntrada.addMetrica();
                                          deveAdicionar = true;
                                          
                                          // Caso seja igual verifica se as metricas são diferentes
                                      } else if (entrada.getMetrica() > novaEntrada.getMetrica()) {
                                          
                                          // Caso forem iguais e a metrica atual é a menor atualiza ip saida e metrica da tabela
                                          entrada.setIpSaida(novaEntrada.getIpSaida());
                                          novaEntrada.addMetrica();
                                          entrada.setMetrica(novaEntrada.getMetrica());
                                          semaforo.release();   
                                          
                                      }
                                }
                                
                                // Caso deva adicionar o faz aqui
                                if (deveAdicionar) {
                                    this.parametroVizinhos.add(novaEntrada);
                                    semaforo.release();
                                }

                            }

                }   
            }
            
            
        
        }
        
      System.out.println( "Tabela: de roteamento " + this.get_tabela_string() );
        
        


    }


    public String get_tabela_string() {
     /* Tabela de roteamento vazia conforme especificado no protocolo */
     String tabela_string = "";
        
     /* Converta a tabela de rotamento para string, conforme formato definido no protocolo . */
     if (this.parametroVizinhos.isEmpty()) {
         
         // Caso tabela de roteamento esteja vazia. Tabela String = "!"
         tabela_string = "!";
         
     } else {
         try {
             semaforo.acquire();
         } catch (InterruptedException ex) {
             Logger.getLogger(TabelaRoteamento.class.getName()).log(Level.SEVERE, null, ex);
         }
         for (Iterator<Vizinho> it = this.parametroVizinhos.iterator(); it.hasNext();) { 
               Vizinho entrada = it.next();
               tabela_string = tabela_string + "*"+entrada.getIp()+";"+entrada.getMetrica();      
         }
         semaforo.release();
         
     } 

        return tabela_string;
    }

}

