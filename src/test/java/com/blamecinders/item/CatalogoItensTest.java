package com.blamecinders.item;

import com.blamecinders.configuracao.BalanceamentoJogo;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class CatalogoItensTest {

    @Test
    void usaLimiteConfiguradoParaSepararArmasEComidas() {
        ItemBau arma = CatalogoItens.gerarItemAleatorio(
            new RandomFixo(BalanceamentoJogo.CHANCE_ARMA_NO_BAU - 1, 0)
        );
        ItemBau comida = CatalogoItens.gerarItemAleatorio(
            new RandomFixo(BalanceamentoJogo.CHANCE_ARMA_NO_BAU, 0)
        );

        assertInstanceOf(Arma.class, arma);
        assertInstanceOf(Comida.class, comida);
    }

    @Test
    void armasEComidasUsamValoresCentralizados() {
        Arma claymore = CatalogoArmas.gerarArmaAleatoria(new RandomFixo(0));
        Arma espada = CatalogoArmas.gerarArmaAleatoria(new RandomFixo(1));
        Comida sopa = (Comida) CatalogoItens.gerarItemAleatorio(
            new RandomFixo(BalanceamentoJogo.CHANCE_ARMA_NO_BAU, 1)
        );

        assertEquals(BalanceamentoJogo.DURABILIDADE_CLAYMORE, claymore.getDurabilidade());
        assertEquals(BalanceamentoJogo.DURABILIDADE_ESPADA_CURTA, espada.getDurabilidade());
        assertEquals(BalanceamentoJogo.CURA_SOPA, sopa.getCura());
    }

    private static final class RandomFixo extends Random {
        private final int[] valores;
        private int indice;

        private RandomFixo(int... valores) {
            this.valores = valores;
        }

        @Override
        public int nextInt(int limite) {
            int valor = valores[Math.min(indice++, valores.length - 1)];
            return Math.min(valor, limite - 1);
        }
    }
}
