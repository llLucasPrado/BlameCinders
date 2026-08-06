package com.root.game.Combate;

public class Inimigo extends EntidadeCombate {

    private final int dificuldadeFurtividade;

    public Inimigo(String nome, int vida, String texturaPath) {
        this(nome, vida, texturaPath, Math.min(40, 10 + vida));
    }

    public Inimigo(String nome, int vida, String texturaPath, int dificuldadeFurtividade) {
        super(nome, vida, texturaPath);
        this.dificuldadeFurtividade = Math.max(0, Math.min(60, dificuldadeFurtividade));
    }

    public int getVida() {
        return pontosDeVida;
    }

    public int getDificuldadeFurtividade() {
        return dificuldadeFurtividade;
    }

    public Inimigo copiar() {
        return new Inimigo(this.nome, this.pontosDeVida, this.texturaPath, this.dificuldadeFurtividade);
    }
}
