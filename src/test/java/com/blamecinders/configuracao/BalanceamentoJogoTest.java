package com.blamecinders.configuracao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BalanceamentoJogoTest {

    @Test
    void configuracaoMantemLimitesValidos() {
        assertAll(
            () -> assertTrue(BalanceamentoJogo.VIDA_INICIAL_HEROI > 0),
            () -> assertTrue(BalanceamentoJogo.OBJETIVO_CHAMAS > 0),
            () -> assertTrue(BalanceamentoJogo.LIMITE_BAUS_TABULEIRO >= 0),
            () -> assertTrue(BalanceamentoJogo.LIMITE_PAREDES_TABULEIRO >= 0),
            () -> assertTrue(
                BalanceamentoJogo.VIDA_ASSASSINO < BalanceamentoJogo.VIDA_ARQUEIRA
            ),
            () -> assertTrue(
                BalanceamentoJogo.VIDA_ARQUEIRA < BalanceamentoJogo.VIDA_CAVALEIRO
            ),
            () -> assertTrue(
                BalanceamentoJogo.VIDA_CAVALEIRO < BalanceamentoJogo.VIDA_ANAO
            ),
            () -> assertTrue(BalanceamentoJogo.DURABILIDADE_ESPADA_CURTA > 0),
            () -> assertTrue(
                BalanceamentoJogo.DURABILIDADE_CLAYMORE
                    > BalanceamentoJogo.DURABILIDADE_ESPADA_CURTA
            ),
            () -> assertTrue(BalanceamentoJogo.CURA_RACAO > 0),
            () -> assertTrue(BalanceamentoJogo.CURA_SOPA > BalanceamentoJogo.CURA_RACAO),
            () -> assertTrue(BalanceamentoJogo.CURA_FRASCO > BalanceamentoJogo.CURA_SOPA),
            () -> assertTrue(BalanceamentoJogo.DIVISOR_DANO_FURTIVIDADE_FALHA > 0),
            () -> assertTrue(
                BalanceamentoJogo.CHANCE_BAU_TABULEIRO
                    + BalanceamentoJogo.CHANCE_PAREDE_TABULEIRO
                    <= BalanceamentoJogo.TOTAL_PERCENTUAL
            ),
            () -> assertTrue(
                BalanceamentoJogo.CHANCE_INIMIGO_FRACO
                    + BalanceamentoJogo.CHANCE_INIMIGO_MEDIO_BAIXO
                    + BalanceamentoJogo.CHANCE_INIMIGO_MEDIO_ALTO
                    + BalanceamentoJogo.CHANCE_INIMIGO_MEDIO_DIFICIL
                    == BalanceamentoJogo.TOTAL_PERCENTUAL
            ),
            () -> assertTrue(
                BalanceamentoJogo.CHANCE_MINIMA_FURTIVIDADE
                    <= BalanceamentoJogo.CHANCE_MAXIMA_FURTIVIDADE
            ),
            () -> assertTrue(
                BalanceamentoJogo.CHANCE_BASE_FURTIVIDADE_ASSASSINO
                    > BalanceamentoJogo.CHANCE_BASE_FURTIVIDADE_ARQUEIRA
            ),
            () -> assertTrue(
                BalanceamentoJogo.CHANCE_BASE_FURTIVIDADE_ARQUEIRA
                    > BalanceamentoJogo.CHANCE_BASE_FURTIVIDADE_CAVALEIRO
            )
        );
    }
}
