package roteador;

import java.util.concurrent.Semaphore;

public class Vizinhos {
    private String ip;
    private int cont;
    private Semaphore semaforo;

    public Vizinhos(String ip, int contador) {
        this.ip = ip;
        this.cont = contador;
        semaforo = new Semaphore(1);
    }

    public void decrementaContador(){
        semaforo.tryAcquire();
        if(cont > 0){
            cont--;
        }
        semaforo.release();
    }

    public int getCont() {
        return cont;
    }

    public void setCont(int cont) {
        semaforo.tryAcquire();
        this.cont = cont;
        semaforo.release();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
