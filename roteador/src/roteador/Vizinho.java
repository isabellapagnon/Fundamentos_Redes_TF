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

    public int getMetrica() {
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

    public String toString(){
        return "*" + ip + ";" + metrica;
    }

}
