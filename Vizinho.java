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

    public String toString(){
        return "*" + ip + ";" + metrica;
    }

}
