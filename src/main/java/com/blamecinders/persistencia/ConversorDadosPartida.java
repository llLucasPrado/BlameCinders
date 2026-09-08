package com.blamecinders.persistencia;

import com.blamecinders.aplicacao.EstadoPartida;
import com.blamecinders.combate.Inimigo;
import com.blamecinders.combate.Jogador;
import com.blamecinders.item.Arma;
import com.blamecinders.item.Comida;
import com.blamecinders.item.ItemBau;
import com.blamecinders.persistencia.DadosPartida.CartaSalva;
import com.blamecinders.persistencia.DadosPartida.InimigoSalvo;
import com.blamecinders.persistencia.DadosPartida.ItemSalvo;
import com.blamecinders.persistencia.DadosPartida.TipoItemSalvo;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.tabuleiro.EstadoCarta;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.tabuleiro.TipoCarta;

import java.util.Objects;

final class ConversorDadosPartida {

    static final int VERSAO_ATUAL = 1;

    private ConversorDadosPartida() {
    }

    static DadosPartida paraDados(EstadoPartida partida) {
        Objects.requireNonNull(partida, "partida");
        DadosPartida dados = new DadosPartida();
        Tabuleiro tabuleiro = partida.getTabuleiro();
        Jogador jogador = partida.getJogador();

        dados.versao = VERSAO_ATUAL;
        dados.jogadorLinha = tabuleiro.getJogadorLinha();
        dados.jogadorColuna = tabuleiro.getJogadorColuna();
        dados.chamasColetadas = tabuleiro.getChamasColetadas();
        dados.vidaAtual = jogador.getVida();
        dados.vidaMaxima = jogador.getVidaMaxima();
        dados.armaEquipada = paraItem(jogador.getArmaEquipada());
        dados.cartas = new CartaSalva[Tabuleiro.LINHAS * Tabuleiro.COLUNAS];

        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                dados.cartas[indice(linha, coluna)] = paraCarta(
                    tabuleiro.getCartaInfo(linha, coluna)
                );
            }
        }
        return dados;
    }

    static EstadoPartida paraPartida(DadosPartida dados) {
        validarDados(dados);
        CartaInfo[][] grid = new CartaInfo[Tabuleiro.LINHAS][Tabuleiro.COLUNAS];
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                grid[linha][coluna] = paraCarta(dados.cartas[indice(linha, coluna)]);
            }
        }

        if (grid[dados.jogadorLinha][dados.jogadorColuna] != null) {
            throw new IllegalArgumentException("O save contém uma carta sob o herói.");
        }
        validarInvariantesTabuleiro(grid, dados.jogadorLinha, dados.jogadorColuna);

        Tabuleiro tabuleiro = Tabuleiro.restaurar(
            grid,
            dados.jogadorLinha,
            dados.jogadorColuna,
            dados.chamasColetadas
        );
        Jogador jogador = Jogador.restaurar(dados.vidaMaxima, dados.vidaAtual);
        ItemBau arma = paraItem(dados.armaEquipada);
        if (arma != null && !(arma instanceof Arma)) {
            throw new IllegalArgumentException("O item equipado no save não é uma arma.");
        }
        jogador.setArmaEquipada((Arma) arma);
        return new EstadoPartida(tabuleiro, jogador);
    }

    private static CartaSalva paraCarta(CartaInfo carta) {
        if (carta == null) return null;
        CartaSalva salva = new CartaSalva();
        salva.tipo = carta.getTipo();
        salva.estado = carta.getEstado();
        salva.inimigo = paraInimigo(carta.getInimigo());
        salva.item = paraItem(carta.getItemDentro());
        salva.furtividadeTentada = carta.isFurtividadeTentada();
        salva.bauAberto = carta.isBauAberto();
        return salva;
    }

    private static CartaInfo paraCarta(CartaSalva salva) {
        if (salva == null) return null;
        if (salva.tipo == null || salva.tipo == TipoCarta.VAZIO || salva.estado == null) {
            throw new IllegalArgumentException("Carta inválida no save.");
        }

        CartaInfo carta = new CartaInfo(salva.tipo);
        carta.setEstado(salva.estado);
        carta.setInimigo(paraInimigo(salva.inimigo));
        carta.setItemDentro(paraItem(salva.item));
        if (salva.furtividadeTentada) carta.registrarTentativaFurtividade();
        if (salva.bauAberto) carta.registrarAberturaBau();

        if (salva.tipo == TipoCarta.INIMIGO && carta.getInimigo() == null) {
            throw new IllegalArgumentException("Carta de inimigo sem inimigo no save.");
        }
        if (salva.tipo != TipoCarta.INIMIGO && carta.getInimigo() != null) {
            throw new IllegalArgumentException("Inimigo associado ao tipo de carta incorreto.");
        }
        if (salva.tipo != TipoCarta.BAU && carta.getItemDentro() != null) {
            throw new IllegalArgumentException("Item associado ao tipo de carta incorreto.");
        }
        return carta;
    }

    private static InimigoSalvo paraInimigo(Inimigo inimigo) {
        if (inimigo == null) return null;
        InimigoSalvo salvo = new InimigoSalvo();
        salvo.nome = inimigo.getNome();
        salvo.identificadorVisual = inimigo.getIdentificadorVisual();
        salvo.vida = inimigo.getVida();
        salvo.dificuldadeFurtividade = inimigo.getDificuldadeFurtividade();
        return salvo;
    }

    private static Inimigo paraInimigo(InimigoSalvo salvo) {
        if (salvo == null) return null;
        return new Inimigo(
            salvaObrigatoria(salvo.nome, "nome do inimigo"),
            salvo.vida,
            salvaObrigatoria(salvo.identificadorVisual, "identificador do inimigo"),
            salvo.dificuldadeFurtividade
        );
    }

    private static ItemSalvo paraItem(ItemBau item) {
        if (item == null) return null;
        ItemSalvo salvo = new ItemSalvo();
        salvo.nome = item.getNome();
        salvo.identificadorVisual = item.getIdentificadorVisual();
        if (item instanceof Arma) {
            salvo.tipo = TipoItemSalvo.ARMA;
            salvo.valor = ((Arma) item).getDurabilidade();
        } else if (item instanceof Comida) {
            salvo.tipo = TipoItemSalvo.COMIDA;
            salvo.valor = ((Comida) item).getCura();
        } else {
            throw new IllegalArgumentException("Tipo de item não suportado no save.");
        }
        return salvo;
    }

    private static ItemBau paraItem(ItemSalvo salvo) {
        if (salvo == null) return null;
        String nome = salvaObrigatoria(salvo.nome, "nome do item");
        String identificador = salvaObrigatoria(
            salvo.identificadorVisual,
            "identificador do item"
        );
        if (salvo.tipo == TipoItemSalvo.ARMA) {
            return new Arma(nome, salvo.valor, identificador);
        }
        if (salvo.tipo == TipoItemSalvo.COMIDA) {
            return new Comida(nome, salvo.valor, identificador);
        }
        throw new IllegalArgumentException("Tipo de item inválido no save.");
    }

    private static void validarDados(DadosPartida dados) {
        if (dados == null || dados.versao != VERSAO_ATUAL) {
            throw new IllegalArgumentException("Versão de save incompatível.");
        }
        if (dados.cartas == null
            || dados.cartas.length != Tabuleiro.LINHAS * Tabuleiro.COLUNAS) {
            throw new IllegalArgumentException("Dimensões do tabuleiro inválidas no save.");
        }
        if (dados.jogadorLinha < 0 || dados.jogadorLinha >= Tabuleiro.LINHAS
            || dados.jogadorColuna < 0 || dados.jogadorColuna >= Tabuleiro.COLUNAS) {
            throw new IllegalArgumentException("Posição do herói inválida no save.");
        }
        if (dados.vidaMaxima <= 0 || dados.vidaAtual <= 0 || dados.vidaAtual > dados.vidaMaxima) {
            throw new IllegalArgumentException("Vida do herói inválida no save.");
        }
    }

    private static void validarInvariantesTabuleiro(
        CartaInfo[][] grid,
        int jogadorLinha,
        int jogadorColuna
    ) {
        int chamas = 0;
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                CartaInfo carta = grid[linha][coluna];
                boolean posicaoHeroi = linha == jogadorLinha && coluna == jogadorColuna;
                if (posicaoHeroi) continue;
                if (carta == null) {
                    throw new IllegalArgumentException("O save contém uma célula vazia fora do herói.");
                }
                if (carta.getTipo() == TipoCarta.CHAMA) chamas++;
            }
        }
        if (chamas != 1) {
            throw new IllegalArgumentException("O save deve conter exatamente uma chama ativa.");
        }
    }

    private static String salvaObrigatoria(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("Campo ausente no save: " + campo + ".");
        }
        return valor;
    }

    private static int indice(int linha, int coluna) {
        return linha * Tabuleiro.COLUNAS + coluna;
    }
}
