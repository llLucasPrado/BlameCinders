package com.blamecinders.combate;

import java.util.Objects;

public class Inimigo {

    private final String nome;
    private final String identificadorVisual;
    private final int dificuldadeFurtividade;
    private int vida;

    public Inimigo(String nome, int vida, String identificadorVisual) {
        this(nome, vida, identificadorVisual, Math.min(40, 10 + vida));
    }

    public Inimigo(String nome, int vida, String identificadorVisual, int dificuldadeFurtividade) {
        this.nome = Objects.requireNonNull(nome, "nome");
        this.identificadorVisual = Objects.requireNonNull(identificadorVisual, "identificadorVisual");
        this.vida = Math.max(0, vida);
        this.dificuldadeFurtividade = Math.max(0, Math.min(60, dificuldadeFurtividade));
    }

    public String getNome() {
        return nome;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = Math.max(0, vida);
    }

    public String getIdentificadorVisual() {
        return identificadorVisual;
    }

    public int getDificuldadeFurtividade() {
        return dificuldadeFurtividade;
    }

    public Inimigo copiar() {
        return new Inimigo(nome, vida, identificadorVisual, dificuldadeFurtividade);
    }
}
