package roteador;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;


public class TabelaRoteamento {
    /*
     * Implemente uma estrutura de dados para manter a tabela de roteamento.
     * A tabela deve possuir: IP Destino, Métrica e IP de Saída.
     */
    private List<String> ipList;
    public List<Vizinho> parametroVizinhos;
    public final String meuIp = "";

    public TabelaRoteamento(List<String> ipList, Semaphore semaforo) {
        this.ipList = ipList;
        this.parametroVizinhos = new ArrayList<>();
    }

    public void start() {
        ipList.forEach(this::adicionaIpListaVizinhos);
    }

    private void adicionaIpListaVizinhos(String vizinho) {
        parametroVizinhos.add(new Vizinho(vizinho, 1, vizinho));
    }

    public boolean encontraVizinho(String vizinhoIp) {
        return parametroVizinhos.stream()
                .anyMatch(parametro -> parametro.getIp().equals(vizinhoIp) && parametro.getMetrica() == 1);
    }

    public void update_tabela(String tabela, InetAddress IPAddress, AtomicBoolean mudancaTabela) {
        List<Vizinho> parametroRecebido = formatacaoParametrosVizinhos(tabela, IPAddress);
        BiPredicate<Vizinho, Vizinho> compareparametroVizinhosByDestinationIp = (vizinho1, vizinho2) -> vizinho1.getIp().equals(vizinho2.getIp());
        if (ipList.contains(IPAddress.getHostAddress()) && !encontraVizinho(IPAddress.getHostAddress())) {
            adicionaIpListaVizinhos(IPAddress.getHostAddress());
        }

        parametroRecebido.stream()
                .filter(parametroRec -> !parametroRec.getIp().equals(meuIp))
                .forEach(parametroRec -> {
                    System.out.println("Os seguintes parametro foram recebidos: " + parametroRec);
                    Optional<Vizinho> parametroByDestinationIp = parametroVizinhos.stream()
                            .filter(parametro -> compareparametroVizinhosByDestinationIp.test(parametro, parametroRec))
                            .findFirst();

                    parametroByDestinationIp.ifPresent(foundparametro -> System.out.println("Parametro com o mesmo IP: " + foundparametro.getIp()));

                    if (parametroByDestinationIp.isEmpty()) {// esta vazio
                        parametroRec.setMetrica(parametroRec.getMetrica() + 1);
                        parametroVizinhos.add(parametroRec);
                        mudancaTabela.set(true);
                    } else if (parametroByDestinationIp.get().getMetrica() > (parametroRec.getMetrica() + 1)) {
                        System.out.println("Parametro recebido tem metrica menor que o atual. " + parametroRec.getIp());
                        Vizinho foundparametro = parametroByDestinationIp.get();
                        foundparametro.setMetrica(parametroRec.getMetrica() + 1);
                        foundparametro.setIpSaida(parametroRec.getIpSaida());
                        mudancaTabela.set(true);
                    }
                });

        boolean parametroVizinhosRemovido = parametroVizinhos.removeIf(parametro ->
                parametro.getIpSaida().equals(IPAddress.getHostAddress()) &&
                parametroRecebido.stream().noneMatch(receivedparametro -> compareparametroVizinhosByDestinationIp.test(receivedparametro, parametro)) &&
                parametro.getMetrica() != 1
        );
    }

    public void removeVizinho(String IPtoRemove, AtomicBoolean mudancaTabela) {
    if(parametroVizinhos.removeIf(parametro -> IPtoRemove.equals(parametro.getIpSaida()))){
        mudancaTabela.set(true);
    }
}

    // public void removeVizinho2(String ip, AtomicBoolean mudancaTabela){
    //     int index = -1;
    //     System.out.println("IP PARA REMOCAO: " + ip);
    //     for (int i = 0; i < parametroVizinhos.size(); i++){
    //         System.out.println("Ip comparacao: " + parametroVizinhos.get(i).getIp());
    //         if(parametroVizinhos.get(i).getIp().equals(ip)){
    //             index = i;
    //         }
    //     }
    //     parametroVizinhos.remove(index);
    //     mudancaTabela.set(true);
    // }

    public String formatacaoTabela() {
                /* Tabela de roteamento vazia conforme especificado no protocolo */
        if (parametroVizinhos.isEmpty())
            return "!";

        return parametroVizinhos.stream()
                .map(parametro -> "*" + parametro.getIp() + ";" + parametro.getMetrica())
                .collect(Collectors.joining());
    }

    public List<Vizinho> formatacaoParametrosVizinhos(String tabela, InetAddress IPAddress) {
        String[] parametroVizinhos = tabela.split("\\*");
        return Arrays.stream(parametroVizinhos)
                .map(vizinho -> {
                    String[] conexao = vizinho.split(";");
                    if (conexao.length > 1) {
                        return new Vizinho(conexao[0], Integer.valueOf(conexao[1]), IPAddress.getHostAddress());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public String getIps(){
        return parametroVizinhos.stream()
                .map(parametro -> ";" + parametro.getIp())
                .collect(Collectors.joining());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Ip - metrica - ip saida\n");
        parametroVizinhos.forEach(parametro ->
                stringBuilder.append(parametro.getIp())
                        .append(" - ")
                        .append(parametro.getMetrica())
                        .append(" - ")
                        .append(parametro.getIpSaida())
                        .append("\n")
        );
        return stringBuilder.toString();
    }

}