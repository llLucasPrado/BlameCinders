package com.blamecinders.combate;

import com.blamecinders.item.ItemBau;

public class Arma extends EntidadeCombate implements ItemBau {

    public Arma(String nome, int durabilidade, String texturaPath) {
        super(nome, durabilidade, texturaPath);
    }

    public int getDurabilidade() {
        return pontosDeVida;
    }

    public void setDurabilidade(int durabilidade) {
        setPontosDeVida(durabilidade);
    }

    public boolean estaQuebrada() {
        return pontosDeVida <= 0;
    }

    public Arma copiar() {
        return new Arma(this.nome, this.pontosDeVida, this.texturaPath);
    }

    @Override
    public String getIdentificadorVisual() {
        return texturaPath;
    }
}
