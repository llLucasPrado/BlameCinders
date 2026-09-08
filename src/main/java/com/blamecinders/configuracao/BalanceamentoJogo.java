package com.blamecinders.configuracao;

/** Valores atuais das regras, reunidos para o próximo ciclo de balanceamento. */
public final class BalanceamentoJogo {

    public static final int VIDA_INICIAL_HEROI = 50;
    public static final int OBJETIVO_CHAMAS = 3;
    public static final int TOTAL_PERCENTUAL = 100;

    public static final int LIMITE_BAUS_TABULEIRO = 3;
    public static final int CHANCE_BAU_TABULEIRO = 20;
    public static final int CHANCE_PAREDE_TABULEIRO = 10;
    public static final int CHANCE_ARMA_NO_BAU = 55;

    public static final int CHANCE_BASE_FURTIVIDADE = 70;
    public static final int CHANCE_MINIMA_FURTIVIDADE = 25;
    public static final int CHANCE_MAXIMA_FURTIVIDADE = 80;
    public static final int DIFICULDADE_BASE_FURTIVIDADE = 10;
    public static final int DIFICULDADE_PADRAO_MAXIMA = 40;
    public static final int DIFICULDADE_ABSOLUTA_MAXIMA = 60;
    public static final int DIVISOR_DANO_FURTIVIDADE_FALHA = 2;

    private BalanceamentoJogo() {
    }
}
