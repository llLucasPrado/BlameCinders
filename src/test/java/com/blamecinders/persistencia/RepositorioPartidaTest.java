package com.blamecinders.persistencia;

import com.badlogic.gdx.utils.Json;
import com.blamecinders.aplicacao.EstadoPartida;
import com.blamecinders.combate.CategoriaInimigo;
import com.blamecinders.combate.Inimigo;
import com.blamecinders.combate.Personagem;
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
        EstadoPartida original = new EstadoPartida(Personagem.AQUILLA);
        original.getJogador().setVida(37);
        original.getJogador().setArmaEquipada(new Arma("Claymore", 9, "ARMA: CLAYMORE"));
        original.adicionarCinzas(14);

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
        assertEquals(14, carregada.getCinzas());
        assertEquals(Personagem.AQUILLA, carregada.getPersonagem());
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
    void recuperaBackupQuandoSavePrincipalEstaCorrompido() {
        Json json = new Json();
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        armazenamento.escreverBackup(
            json.toJson(ConversorDadosPartida.paraDados(new EstadoPartida(Personagem.TAIGA)))
        );
        armazenamento.escrever("{invalido");
        RepositorioPartida repositorio = new RepositorioPartida(armazenamento, json);

        EstadoPartida carregada = repositorio.carregar();

        assertNotNull(carregada);
        assertEquals(Personagem.TAIGA, carregada.getPersonagem());
    }

    @Test
    void consultaDeSaveCorrompidoNaoApagaOsDados() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        armazenamento.escrever("{invalido");
        RepositorioPartida repositorio = new RepositorioPartida(armazenamento, new Json());

        assertFalse(repositorio.existe());
        assertEquals("{invalido", armazenamento.ler());
    }

    @Test
    void preservaTrocaDePosicoesDaFurtividade() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        RepositorioPartida repositorio = new RepositorioPartida(armazenamento, new Json());
        EstadoPartida original = new EstadoPartida();
        int linhaDestino = original.getTabuleiro().getCarta(0, 1) == TipoCarta.INIMIGO
            ? 0
            : 1;
        int colunaDestino = linhaDestino == 0 ? 1 : 0;

        original.getTabuleiro().trocarJogadorComCarta(linhaDestino, colunaDestino);
        repositorio.salvar(original);
        EstadoPartida carregada = repositorio.carregar();

        assertNotNull(carregada);
        assertEquals(linhaDestino, carregada.getTabuleiro().getJogadorLinha());
        assertEquals(colunaDestino, carregada.getTabuleiro().getJogadorColuna());
        assertEquals(TipoCarta.INIMIGO, carregada.getTabuleiro().getCarta(0, 0));
        assertNull(carregada.getTabuleiro().getCartaInfo(linhaDestino, colunaDestino));
    }

    @Test
    void carregaVersaoAnteriorComIdentificadorAntigoDePersonagem() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        Json json = new Json();
        DadosPartida dados = ConversorDadosPartida.paraDados(
            new EstadoPartida(Personagem.VIEYRA)
        );
        dados.versao = ConversorDadosPartida.VERSAO_INICIAL;
        dados.personagemId = "PERSONAGEM_Vieyra";
        dados.cinzas = 7;
        armazenamento.escrever(json.toJson(dados));

        EstadoPartida carregada = new RepositorioPartida(armazenamento, json).carregar();

        assertNotNull(carregada);
        assertEquals(Personagem.VIEYRA, carregada.getPersonagem());
        assertEquals(7, carregada.getCinzas());
    }

    @Test
    void carregaSaveOriginalSemCamposDePersonagemECinzas() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        Json json = new Json();
        DadosPartida dados = ConversorDadosPartida.paraDados(new EstadoPartida());
        dados.versao = ConversorDadosPartida.VERSAO_INICIAL;
        dados.personagemId = null;
        dados.cinzas = 0;
        armazenamento.escrever(json.toJson(dados));

        EstadoPartida carregada = new RepositorioPartida(armazenamento, json).carregar();

        assertNotNull(carregada);
        assertEquals(Personagem.PADRAO, carregada.getPersonagem());
        assertEquals(0, carregada.getCinzas());
    }

    @Test
    void preservaCategoriaDoInimigoMesmoQuandoElaNaoCoincideComAVida() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        Json json = new Json();
        EstadoPartida original = new EstadoPartida();
        CartaInfo carta = primeiraCartaDeInimigo(original.getTabuleiro());
        carta.setInimigo(new Inimigo(
            "Guardião enfraquecido",
            8,
            "INIMIGO 1",
            CategoriaInimigo.MEDIO_DIFICIL,
            25
        ));

        RepositorioPartida repositorio = new RepositorioPartida(armazenamento, json);
        repositorio.salvar(original);
        EstadoPartida carregada = repositorio.carregar();

        assertEquals(
            CategoriaInimigo.MEDIO_DIFICIL,
            primeiraCartaDeInimigo(carregada.getTabuleiro()).getInimigo().getCategoria()
        );
    }

    @Test
    void carregaVersaoDoisSemCategoriaPersistida() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        Json json = new Json();
        DadosPartida dados = ConversorDadosPartida.paraDados(new EstadoPartida());
        dados.versao = 2;
        for (DadosPartida.CartaSalva carta : dados.cartas) {
            if (carta != null && carta.inimigo != null) carta.inimigo.categoria = null;
        }
        armazenamento.escrever(json.toJson(dados));

        EstadoPartida carregada = new RepositorioPartida(armazenamento, json).carregar();
        Inimigo inimigo = primeiraCartaDeInimigo(carregada.getTabuleiro()).getInimigo();

        assertEquals(CategoriaInimigo.porVida(inimigo.getVida()), inimigo.getCategoria());
    }

    @Test
    void rejeitaVersaoDeSaveFutura() {
        ArmazenamentoMemoria armazenamento = new ArmazenamentoMemoria();
        Json json = new Json();
        DadosPartida dados = ConversorDadosPartida.paraDados(new EstadoPartida());
        dados.versao = ConversorDadosPartida.VERSAO_ATUAL + 1;
        armazenamento.escrever(json.toJson(dados));

        RepositorioPartida repositorio = new RepositorioPartida(armazenamento, json);

        assertThrows(IllegalStateException.class, repositorio::carregar);
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

    private static CartaInfo primeiraCartaDeInimigo(Tabuleiro tabuleiro) {
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                CartaInfo carta = tabuleiro.getCartaInfo(linha, coluna);
                if (carta != null && carta.getTipo() == TipoCarta.INIMIGO) return carta;
            }
        }
        throw new AssertionError("O tabuleiro deveria possuir ao menos um inimigo.");
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
        private String backup = "";

        @Override
        public String ler() {
            return conteudo;
        }

        @Override
        public String lerBackup() {
            return backup;
        }

        @Override
        public void escrever(String conteudo) {
            this.conteudo = conteudo;
        }

        @Override
        public void remover() {
            conteudo = "";
            backup = "";
        }

        private void escreverBackup(String conteudo) {
            backup = conteudo;
        }
    }
}
