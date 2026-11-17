package com.blamecinders.configuracao;

/** Valores atuais das regras, reunidos para o próximo ciclo de balanceamento. */
public final class BalanceamentoJogo {

    public static final int VIDA_ASSASSINO = 32;
    public static final int VIDA_ARQUEIRA = 38;
    public static final int VIDA_CAVALEIRO = 50;
    public static final int VIDA_ANAO = 60;
    public static final int VIDA_INICIAL_HEROI = VIDA_CAVALEIRO;
    public static final int OBJETIVO_CHAMAS = 3;
    public static final int TOTAL_PERCENTUAL = 100;

    public static final int LIMITE_BAUS_TABULEIRO = 3;
    public static final int LIMITE_PAREDES_TABULEIRO = 4;
    public static final int CHANCE_BAU_TABULEIRO = 18;
    public static final int CHANCE_PAREDE_TABULEIRO = 15;
    public static final int CHANCE_ARMA_NO_BAU = 45;

    public static final int CHANCE_INIMIGO_FRACO = 40;
    public static final int CHANCE_INIMIGO_MEDIO_BAIXO = 30;
    public static final int CHANCE_INIMIGO_MEDIO_ALTO = 20;
    public static final int CHANCE_INIMIGO_MEDIO_DIFICIL = 10;

    public static final int DURABILIDADE_ESPADA_CURTA = 12;
    public static final int DURABILIDADE_CLAYMORE = 22;
    public static final int CURA_RACAO = 8;
    public static final int CURA_SOPA = 14;
    public static final int CURA_FRASCO = 20;

    public static final int CHANCE_BASE_FURTIVIDADE_ASSASSINO = 90;
    public static final int CHANCE_BASE_FURTIVIDADE_ARQUEIRA = 75;
    public static final int CHANCE_BASE_FURTIVIDADE_CAVALEIRO = 40;
    public static final int CHANCE_MINIMA_FURTIVIDADE = 5;
    public static final int CHANCE_MAXIMA_FURTIVIDADE = 90;
    public static final int DIFICULDADE_BASE_FURTIVIDADE = 10;
    public static final int DIFICULDADE_PADRAO_MAXIMA = 40;
    public static final int DIFICULDADE_ABSOLUTA_MAXIMA = 60;
    public static final int DIVISOR_DANO_FURTIVIDADE_FALHA = 2;

    private BalanceamentoJogo() {
    }
}
