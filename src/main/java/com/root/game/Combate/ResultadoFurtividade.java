package com.root.game.Combate;

public class ResultadoFurtividade {

    private final boolean sucesso;
    private final int chancePercentual;
    private final int rolagem;

    public ResultadoFurtividade(boolean sucesso, int chancePercentual, int rolagem) {
        this.sucesso = sucesso;
        this.chancePercentual = chancePercentual;
        this.rolagem = rolagem;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public int getChancePercentual() {
        return chancePercentual;
    }

    public int getRolagem() {
        return rolagem;
    }
}
