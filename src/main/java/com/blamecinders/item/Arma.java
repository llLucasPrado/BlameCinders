package com.blamecinders.item;

import java.util.Objects;

public class Arma implements ItemBau {

    private final String nome;
    private final String identificadorVisual;
    private int durabilidade;

    public Arma(String nome, int durabilidade, String identificadorVisual) {
        this.nome = Objects.requireNonNull(nome, "nome");
        this.identificadorVisual = Objects.requireNonNull(identificadorVisual, "identificadorVisual");
        setDurabilidade(durabilidade);
    }

    public String getNome() {
        return nome;
    }

    public int getDurabilidade() {
        return durabilidade;
    }

    public void setDurabilidade(int durabilidade) {
        this.durabilidade = Math.max(0, durabilidade);
    }

    public boolean estaQuebrada() {
        return durabilidade <= 0;
    }

    public Arma copiar() {
        return new Arma(nome, durabilidade, identificadorVisual);
    }

    @Override
    public String getIdentificadorVisual() {
        return identificadorVisual;
    }
}
