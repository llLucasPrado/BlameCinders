package com.blamecinders.tabuleiro;

import com.blamecinders.aplicacao.ControladorEncontro;
import com.blamecinders.aplicacao.ControladorTurno;
import com.blamecinders.aplicacao.DesfechoInimigo;
import com.blamecinders.aplicacao.EstadoPartida;
import com.blamecinders.aplicacao.MovimentoTabuleiro;
import com.blamecinders.aplicacao.ResultadoColetaBau;
import com.blamecinders.aplicacao.ResultadoColetaChama;
import com.blamecinders.aplicacao.ResultadoEncontroInimigo;
import com.blamecinders.combate.Inimigo;
import com.blamecinders.combate.Jogador;
import com.blamecinders.combate.SistemaCombate;
import com.blamecinders.combate.SistemaFurtividade;
import com.blamecinders.item.Arma;
import com.blamecinders.item.Comida;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControladorEncontroTest {

    @Test
    void coletaTresChamasEConcluiObjetivo() {
        CartaInfo[][] grid = criarGrid();
        EstadoPartida partida = criarPartida(grid, 50);
        ControladorEncontro controlador = new ControladorEncontro(partida);
        ResultadoColetaChama resultado = null;

        for (int quantidade = 1; quantidade <= Tabuleiro.OBJETIVO_CHAMAS; quantidade++) {
            grid[0][1] = new CartaInfo(TipoCarta.CHAMA);
            resultado = controlador.coletarChama(0, 1);
            assertEquals(quantidade, resultado.getQuantidadeColetada());
        }

        assertTrue(resultado.isObjetivoConcluido());
        assertTrue(partida.isFinalizada());
        assertNull(partida.getTabuleiro().getCartaInfo(0, 1));
    }

    @Test
    void permiteConcluirMovimentoDaTerceiraChamaAposVitoria() {
        CartaInfo[][] grid = criarGrid();
        EstadoPartida partida = criarPartida(grid, 50);
        ControladorEncontro encontros = new ControladorEncontro(partida);

        for (int quantidade = 1; quantidade <= Tabuleiro.OBJETIVO_CHAMAS; quantidade++) {
            grid[0][1] = new CartaInfo(TipoCarta.CHAMA);
            encontros.coletarChama(0, 1);
        }

        ControladorTurno turnos = new ControladorTurno(partida);
        MovimentoTabuleiro movimento = turnos.prepararMovimento(0, 1);
        turnos.concluirMovimento(movimento);

        assertTrue(partida.isFinalizada());
        assertTrue(movimento.isValido());
        assertEquals(0, partida.getTabuleiro().getJogadorLinha());
        assertEquals(1, partida.getTabuleiro().getJogadorColuna());
    }

    @Test
    void consomeComidaDoBauECuraHeroi() {
        CartaInfo[][] grid = criarGrid();
        CartaInfo bau = new CartaInfo(TipoCarta.BAU);
        Comida comida = new Comida("Frasco rubro", 18, "COMIDA: FRASCO RUBRO");
        bau.setItemDentro(comida);
        grid[0][1] = bau;
        EstadoPartida partida = criarPartida(grid, 50);
        partida.getJogador().setVida(30);

        ResultadoColetaBau resultado = new ControladorEncontro(partida).coletarBau(0, 1);

        assertSame(comida, resultado.getItem());
        assertEquals(18, resultado.getVidaCurada());
        assertEquals(48, partida.getJogador().getVida());
        assertNull(partida.getTabuleiro().getCartaInfo(0, 1));
    }

    @Test
    void equipaArmaDoBau() {
        CartaInfo[][] grid = criarGrid();
        CartaInfo bau = new CartaInfo(TipoCarta.BAU);
        Arma arma = new Arma("Claymore", 15, "ARMA: CLAYMORE");
        bau.setItemDentro(arma);
        grid[0][1] = bau;
        EstadoPartida partida = criarPartida(grid, 50);

        ResultadoColetaBau resultado = new ControladorEncontro(partida).coletarBau(0, 1);

        assertSame(arma, resultado.getItem());
        assertSame(arma, partida.getJogador().getArmaEquipada());
        assertEquals("Você equipou: Claymore", resultado.getMensagem());
    }

    @Test
    void furtividadeBemSucedidaTrocaPosicoesSemCausarDano() {
        CartaInfo[][] grid = criarGrid();
        CartaInfo inimigo = criarInimigo(8);
        grid[0][1] = inimigo;
        EstadoPartida partida = criarPartida(grid, 50);
        ControladorEncontro controlador = controladorComRolagem(partida, 0);

        ResultadoEncontroInimigo resultado = controlador.tentarFurtividade(inimigo);
        assertEquals(50, partida.getJogador().getVida());
        assertFalse(inimigo.isFurtividadeTentada());
        controlador.concluirInimigo(0, 1, resultado);

        assertEquals(DesfechoInimigo.FURTIVIDADE_SUCESSO, resultado.getDesfecho());
        assertTrue(resultado.getMensagem().contains("sem sofrer dano"));
        assertFalse(resultado.getMensagem().contains("derrotado"));
        assertEquals(0, resultado.getDanoRecebido());
        assertEquals(50, partida.getJogador().getVida());
        assertTrue(resultado.isTerminal());
        assertEquals(0, partida.getTabuleiro().getJogadorLinha());
        assertEquals(1, partida.getTabuleiro().getJogadorColuna());
        assertSame(inimigo, partida.getTabuleiro().getCartaInfo(0, 0));
        assertNull(partida.getTabuleiro().getCartaInfo(0, 1));
    }

    @Test
    void furtividadeFalhaTrocaPosicoesECausaMetadeDoDanoDireto() {
        CartaInfo[][] grid = criarGrid();
        CartaInfo inimigo = criarInimigo(15);
        grid[0][1] = inimigo;
        EstadoPartida partida = criarPartida(grid, 50);
        Arma arma = new Arma("Escudo improvisado", 15, "ARMA TESTE");
        partida.getJogador().setArmaEquipada(arma);
        ControladorEncontro controlador = controladorComRolagem(partida, 99);

        ResultadoEncontroInimigo resultado = controlador.tentarFurtividade(inimigo);
        assertEquals(50, partida.getJogador().getVida());
        assertEquals(15, arma.getDurabilidade());
        controlador.concluirInimigo(0, 1, resultado);

        assertEquals(DesfechoInimigo.FURTIVIDADE_FALHOU, resultado.getDesfecho());
        assertEquals(8, resultado.getDanoRecebido());
        assertEquals(42, partida.getJogador().getVida());
        assertEquals(15, arma.getDurabilidade());
        assertTrue(resultado.isTerminal());
        assertSame(inimigo, partida.getTabuleiro().getCartaInfo(0, 0));
        assertNull(partida.getTabuleiro().getCartaInfo(0, 1));
        assertThrows(IllegalStateException.class, () -> controlador.tentarFurtividade(inimigo));
    }

    @Test
    void falhaNaFurtividadeTrocaPosicoesAntesDeDerrotarHeroi() {
        CartaInfo[][] grid = criarGrid();
        CartaInfo inimigo = criarInimigo(8);
        grid[0][1] = inimigo;
        EstadoPartida partida = criarPartida(grid, 3);
        ControladorEncontro controlador = controladorComRolagem(partida, 99);

        ResultadoEncontroInimigo resultado = controlador.tentarFurtividade(inimigo);
        controlador.concluirInimigo(0, 1, resultado);

        assertEquals(DesfechoInimigo.JOGADOR_DERROTADO, resultado.getDesfecho());
        assertTrue(resultado.getMensagem().contains("Furtividade falhou"));
        assertTrue(resultado.getMensagem().contains("dano direto"));
        assertEquals(0, partida.getTabuleiro().getJogadorLinha());
        assertEquals(1, partida.getTabuleiro().getJogadorColuna());
        assertSame(inimigo, partida.getTabuleiro().getCartaInfo(0, 0));
        assertTrue(partida.isFinalizada());
    }

    @Test
    void combateDerrotadoFinalizaPartida() {
        CartaInfo[][] grid = criarGrid();
        CartaInfo inimigo = criarInimigo(10);
        grid[0][1] = inimigo;
        EstadoPartida partida = criarPartida(grid, 5);
        ControladorEncontro controlador = new ControladorEncontro(partida);

        ResultadoEncontroInimigo resultado = controlador.lutar(inimigo);
        controlador.concluirInimigo(0, 1, resultado);

        assertEquals(DesfechoInimigo.JOGADOR_DERROTADO, resultado.getDesfecho());
        assertTrue(partida.isFinalizada());
        assertSame(inimigo, partida.getTabuleiro().getCartaInfo(0, 1));
    }

    @Test
    void combateVencidoConsomeInimigo() {
        CartaInfo[][] grid = criarGrid();
        CartaInfo inimigo = criarInimigo(10);
        grid[0][1] = inimigo;
        EstadoPartida partida = criarPartida(grid, 50);
        ControladorEncontro controlador = new ControladorEncontro(partida);

        ResultadoEncontroInimigo resultado = controlador.lutar(inimigo);
        controlador.concluirInimigo(0, 1, resultado);

        assertEquals(DesfechoInimigo.COMBATE_VENCIDO, resultado.getDesfecho());
        assertEquals(40, partida.getJogador().getVida());
        assertNull(partida.getTabuleiro().getCartaInfo(0, 1));
        assertFalse(partida.isFinalizada());
    }

    @Test
    void recuoMantemInimigoNoTabuleiro() {
        CartaInfo[][] grid = criarGrid();
        CartaInfo inimigo = criarInimigo(10);
        grid[0][1] = inimigo;
        EstadoPartida partida = criarPartida(grid, 50);
        ControladorEncontro controlador = new ControladorEncontro(partida);
        ResultadoEncontroInimigo resultado = ResultadoEncontroInimigo.recuo();

        controlador.concluirInimigo(0, 1, resultado);

        assertEquals(DesfechoInimigo.RECUO, resultado.getDesfecho());
        assertSame(inimigo, partida.getTabuleiro().getCartaInfo(0, 1));
        assertFalse(partida.isFinalizada());
    }

    private EstadoPartida criarPartida(CartaInfo[][] grid, int vida) {
        return new EstadoPartida(
            new Tabuleiro(grid, 0, 0, new Random(1)),
            new Jogador(vida)
        );
    }

    private CartaInfo[][] criarGrid() {
        CartaInfo[][] grid = new CartaInfo[Tabuleiro.LINHAS][Tabuleiro.COLUNAS];
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                grid[linha][coluna] = new CartaInfo(TipoCarta.PAREDE);
            }
        }
        return grid;
    }

    private CartaInfo criarInimigo(int vida) {
        CartaInfo carta = new CartaInfo(TipoCarta.INIMIGO);
        carta.setInimigo(new Inimigo("Inimigo teste", vida, "INIMIGO TESTE", 20));
        return carta;
    }

    private ControladorEncontro controladorComRolagem(EstadoPartida partida, int rolagem) {
        return new ControladorEncontro(
            partida,
            new SistemaCombate(),
            new SistemaFurtividade(randomQueRetorna(rolagem))
        );
    }

    private Random randomQueRetorna(int valor) {
        return new Random() {
            @Override
            public int nextInt(int limite) {
                return Math.min(valor, limite - 1);
            }
        };
    }
}
