package com.blamecinders.combate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SistemaCombateTest {

    private final SistemaCombate sistema = new SistemaCombate();

    @Test
    void semArmaDanoDoInimigoVaiDiretoParaVida() {
        Jogador jogador = new Jogador(50);

        ResultadoCombate resultado = sistema.resolverCombate(
            jogador,
            new Inimigo("Lobo", 12, "INIMIGO_LOBO")
        );

        assertTrue(resultado.isJogadorVenceu());
        assertEquals(38, jogador.getVida());
        assertEquals(0, resultado.getDurabilidadeInicialArma());
    }

    @Test
    void armaAbsorveDanoAntesDaVida() {
        Jogador jogador = new Jogador(50);
        Arma arma = new Arma("Claymore", 15, "ARMA_CLAYMORE");
        jogador.setArmaEquipada(arma);

        ResultadoCombate resultado = sistema.resolverCombate(
            jogador,
            new Inimigo("Lobo", 12, "INIMIGO_LOBO")
        );

        assertTrue(resultado.isJogadorVenceu());
        assertFalse(resultado.isArmaQuebrou());
        assertEquals(50, jogador.getVida());
        assertEquals(3, arma.getDurabilidade());
        assertSame(arma, jogador.getArmaEquipada());
    }

    @Test
    void danoExcedenteDaArmaPassaParaVida() {
        Jogador jogador = new Jogador(50);
        jogador.setArmaEquipada(new Arma("Punhal", 5, "ARMA_PUNHAL"));

        ResultadoCombate resultado = sistema.resolverCombate(
            jogador,
            new Inimigo("Lobo", 12, "INIMIGO_LOBO")
        );

        assertTrue(resultado.isJogadorVenceu());
        assertTrue(resultado.isArmaQuebrou());
        assertEquals(43, jogador.getVida());
        assertNull(jogador.getArmaEquipada());
    }

    @Test
    void jogadorPerdeQuandoDanoEsgotaVida() {
        Jogador jogador = new Jogador(50);

        ResultadoCombate resultado = sistema.resolverCombate(
            jogador,
            new Inimigo("Chefe", 60, "INIMIGO_CHEFE")
        );

        assertFalse(resultado.isJogadorVenceu());
        assertEquals(0, jogador.getVida());
    }
}
