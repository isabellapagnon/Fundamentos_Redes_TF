package roteador;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

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

    public void update_tabela(String tabela_s, InetAddress IPAddress) {
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

    public String get_tabela_string() {
        String tabela_string = "!"; /* Tabela de roteamento vazia conforme especificado no protocolo */

        /*
         * Converta a tabela de rotamento para string, conforme formato definido no
         * protocolo .
         */

        return tabela_string;
    }

}
