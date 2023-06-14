package roteador;

public class Vizinho {

    public Vizinho(String ip, int metrica, String ipSaida){
        this.ip = ip;
        this.metrica = metrica;
        this.ipSaida = ipSaida;
    }
    public String ip;
    public int metrica;
    public String ipSaida;

    public String getIp() {
        return ip;
    }

    public Integer getMetrica() {
        return metrica;
    }
    
    public void addMetrica () {
        this.metrica =+ 1; 
    }

    public String getIpSaida() {
        return ipSaida;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setMetrica(int metrica) {
        this.metrica = metrica;
    }

    public void setIpSaida(String ipSaida) {
        this.ipSaida = ipSaida;
    }

    public static boolean comparacaoIp(Vizinho vizinho1, Vizinho vizinho2) {
        return vizinho1.getIp().equals(vizinho2.getIp());
    }
    

    public String toString(){
        return "*" + ip + ";" + metrica;
    }

}
