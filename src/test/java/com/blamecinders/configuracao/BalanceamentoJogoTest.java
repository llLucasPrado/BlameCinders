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
            () -> assertTrue(BalanceamentoJogo.DIVISOR_DANO_FURTIVIDADE_FALHA > 0),
            () -> assertTrue(
                BalanceamentoJogo.CHANCE_BAU_TABULEIRO
                    + BalanceamentoJogo.CHANCE_PAREDE_TABULEIRO
                    <= BalanceamentoJogo.TOTAL_PERCENTUAL
            ),
            () -> assertTrue(
                BalanceamentoJogo.CHANCE_MINIMA_FURTIVIDADE
                    <= BalanceamentoJogo.CHANCE_MAXIMA_FURTIVIDADE
            )
        );
    }
}
