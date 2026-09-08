package com.blamecinders.persistencia;

import com.badlogic.gdx.utils.Json;
import com.blamecinders.aplicacao.EstadoPartida;
import com.blamecinders.item.Arma;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.tabuleiro.EstadoCarta;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.tabuleiro.TipoCarta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RepositorioPartidaTest {

    @Test
    void salvaECarregaEstadoCompleto() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        RepositorioPartida repositorio = new RepositorioPartida(armazenamento, new Json());
        EstadoPartida original = new EstadoPartida();
        original.getJogador().setVida(37);
        original.getJogador().setArmaEquipada(new Arma("Claymore", 9, "ARMA: CLAYMORE"));

        CartaInfo cartaRevelada = primeiraCarta(original.getTabuleiro());
        cartaRevelada.setEstado(EstadoCarta.REVELADA);
        if (cartaRevelada.getInimigo() != null) {
            cartaRevelada.registrarTentativaFurtividade();
        }
        if (cartaRevelada.getItemDentro() != null) {
            cartaRevelada.registrarAberturaBau();
        }

        repositorio.salvar(original);
        EstadoPartida carregada = repositorio.carregar();

        assertTrue(repositorio.existe());
        assertNotNull(carregada);
        assertEquals(37, carregada.getJogador().getVida());
        assertEquals(50, carregada.getJogador().getVidaMaxima());
        assertNotNull(carregada.getJogador().getArmaEquipada());
        assertEquals(9, carregada.getJogador().getArmaEquipada().getDurabilidade());
        compararTabuleiros(original.getTabuleiro(), carregada.getTabuleiro());
    }

    @Test
    void removeSaveETrataAusencia() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        RepositorioPartida repositorio = new RepositorioPartida(armazenamento, new Json());
        repositorio.salvar(new EstadoPartida());

        repositorio.remover();

        assertFalse(repositorio.existe());
        assertNull(repositorio.carregar());
    }

    @Test
    void rejeitaSaveCorrompido() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        armazenamento.escrever("{invalido");
        RepositorioPartida repositorio = new RepositorioPartida(armazenamento, new Json());

        assertThrows(IllegalStateException.class, repositorio::carregar);
    }

    @Test
    void preservaTrocaDePosicoesDaFurtividade() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        RepositorioPartida repositorio = new RepositorioPartida(armazenamento, new Json());
        EstadoPartida original = new EstadoPartida();

        original.getTabuleiro().trocarJogadorComCarta(0, 1);
        repositorio.salvar(original);
        EstadoPartida carregada = repositorio.carregar();

        assertNotNull(carregada);
        assertEquals(0, carregada.getTabuleiro().getJogadorLinha());
        assertEquals(1, carregada.getTabuleiro().getJogadorColuna());
        assertEquals(TipoCarta.INIMIGO, carregada.getTabuleiro().getCarta(0, 0));
        assertNull(carregada.getTabuleiro().getCartaInfo(0, 1));
    }

    private static CartaInfo primeiraCarta(Tabuleiro tabuleiro) {
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                CartaInfo carta = tabuleiro.getCartaInfo(linha, coluna);
                if (carta != null) return carta;
            }
        }
        throw new AssertionError("O tabuleiro deveria possuir ao menos uma carta.");
    }

    private static void compararTabuleiros(Tabuleiro esperado, Tabuleiro atual) {
        assertEquals(esperado.getJogadorLinha(), atual.getJogadorLinha());
        assertEquals(esperado.getJogadorColuna(), atual.getJogadorColuna());
        assertEquals(esperado.getChamasColetadas(), atual.getChamasColetadas());
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                CartaInfo cartaEsperada = esperado.getCartaInfo(linha, coluna);
                CartaInfo cartaAtual = atual.getCartaInfo(linha, coluna);
                if (cartaEsperada == null) {
                    assertNull(cartaAtual);
                } else {
                    assertNotNull(cartaAtual);
                    assertEquals(cartaEsperada.getTipo(), cartaAtual.getTipo());
                    assertEquals(cartaEsperada.getEstado(), cartaAtual.getEstado());
                    assertEquals(
                        cartaEsperada.isFurtividadeTentada(),
                        cartaAtual.isFurtividadeTentada()
                    );
                    assertEquals(cartaEsperada.isBauAberto(), cartaAtual.isBauAberto());
                }
            }
        }
    }

    private static final class ArmazenamentoMemoria implements ArmazenamentoPartida {
        private String conteudo = "";

        @Override
        public String ler() {
            return conteudo;
        }

        @Override
        public void escrever(String conteudo) {
            this.conteudo = conteudo;
        }

        @Override
        public void remover() {
            conteudo = "";
        }
    }
}
