package com.blamecinders.aplicacao;

/** Descreve um movimento validado antes de sua animação. */
public final class MovimentoTabuleiro {

    private final int linhaOrigem;
    private final int colunaOrigem;
    private final int linhaDestino;
    private final int colunaDestino;
    private final boolean valido;

    MovimentoTabuleiro(
        int linhaOrigem,
        int colunaOrigem,
        int linhaDestino,
        int colunaDestino,
        boolean valido
    ) {
        this.linhaOrigem = linhaOrigem;
        this.colunaOrigem = colunaOrigem;
        this.linhaDestino = linhaDestino;
        this.colunaDestino = colunaDestino;
        this.valido = valido;
    }

    public int getLinhaOrigem() {
        return linhaOrigem;
    }

    public int getColunaOrigem() {
        return colunaOrigem;
    }

    public int getLinhaDestino() {
        return linhaDestino;
    }

    public int getColunaDestino() {
        return colunaDestino;
    }

    public boolean isValido() {
        return valido;
    }
}
