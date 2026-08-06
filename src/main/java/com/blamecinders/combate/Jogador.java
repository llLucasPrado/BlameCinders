package com.blamecinders.combate;

public class Jogador {

    private int vida;
    private final int vidaMaxima;
    private Arma armaEquipada;

    public Jogador(int vida) {
        if (vida <= 0) {
            throw new IllegalArgumentException("A vida inicial deve ser positiva.");
        }
        this.vida = vida;
        this.vidaMaxima = vida;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = Math.max(0, Math.min(vidaMaxima, vida));
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    public int curar(int quantidade) {
        if (quantidade <= 0) return 0;
        int vidaAnterior = vida;
        setVida(vida + quantidade);
        return vida - vidaAnterior;
    }

    public boolean estaVivo() {
        return vida > 0;
    }

    public Arma getArmaEquipada() {
        return armaEquipada;
    }

    public void setArmaEquipada(Arma armaEquipada) {
        this.armaEquipada = armaEquipada;
    }

    public void removerArma() {
        this.armaEquipada = null;
    }
}
