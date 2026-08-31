package com.blamecinders.aplicacao;

import java.util.Objects;

import com.blamecinders.combate.Jogador;
import com.blamecinders.combate.ResultadoCombate;
import com.blamecinders.combate.ResultadoFurtividade;
import com.blamecinders.combate.SistemaCombate;
import com.blamecinders.combate.SistemaFurtividade;
import com.blamecinders.item.Arma;
import com.blamecinders.item.Comida;
import com.blamecinders.item.ItemBau;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.tabuleiro.TipoCarta;

/** Resolve regras de encontros sem conhecer atores, estágios ou animações. */
public final class ControladorEncontro {

    private final EstadoPartida partida;
    private final SistemaCombate sistemaCombate;
    private final SistemaFurtividade sistemaFurtividade;

    public ControladorEncontro(EstadoPartida partida) {
        this(partida, new SistemaCombate(), new SistemaFurtividade());
    }

    public ControladorEncontro(
        EstadoPartida partida,
        SistemaCombate sistemaCombate,
        SistemaFurtividade sistemaFurtividade
    ) {
        this.partida = Objects.requireNonNull(partida, "partida");
        this.sistemaCombate = Objects.requireNonNull(sistemaCombate, "sistemaCombate");
        this.sistemaFurtividade = Objects.requireNonNull(sistemaFurtividade, "sistemaFurtividade");
    }

    public ResultadoColetaChama coletarChama(int linha, int coluna) {
        validarDestino(linha, coluna, TipoCarta.CHAMA);
        Tabuleiro tabuleiro = partida.getTabuleiro();
        if (!tabuleiro.coletarChama(linha, coluna)) {
            throw new IllegalStateException("A chama não pôde ser coletada.");
        }

        return new ResultadoColetaChama(
            tabuleiro.getChamasColetadas(),
            partida.verificarObjetivoConcluido()
        );
    }

    public ResultadoColetaBau coletarBau(int linha, int coluna) {
        validarDestino(linha, coluna, TipoCarta.BAU);
        Tabuleiro tabuleiro = partida.getTabuleiro();
        CartaInfo carta = tabuleiro.getCartaInfo(linha, coluna);
        ItemBau item = carta.getItemDentro();
        int vidaCurada = 0;
        String mensagem;

        if (item instanceof Arma) {
            Arma arma = (Arma) item;
            partida.getJogador().setArmaEquipada(arma);
            mensagem = "Você equipou: " + arma.getNome();
        } else if (item instanceof Comida) {
            vidaCurada = ((Comida) item).consumir(partida.getJogador());
            mensagem = vidaCurada > 0
                ? "Você recuperou " + vidaCurada + " de vida."
                : "Sua vida já estava cheia.";
        } else {
            mensagem = "Baú vazio.";
        }

        tabuleiro.consumirCarta(linha, coluna);
        return new ResultadoColetaBau(item, vidaCurada, mensagem);
    }

    public ResultadoEncontroInimigo tentarFurtividade(CartaInfo carta) {
        validarInimigo(carta);
        if (carta.isFurtividadeTentada()) {
            throw new IllegalStateException("A furtividade já foi tentada nesta carta.");
        }
        carta.registrarTentativaFurtividade();
        ResultadoFurtividade resultado = sistemaFurtividade.tentar(carta.getInimigo());

        int dano = resultado.isSucesso()
            ? carta.getInimigo().getVida() / 2
            : carta.getInimigo().getVida();
        Jogador jogador = partida.getJogador();
        jogador.setVida(jogador.getVida() - dano);

        if (!jogador.estaVivo()) {
            return ResultadoEncontroInimigo.derrotaPorFurtividade(resultado, dano);
        }
        return ResultadoEncontroInimigo.furtividade(resultado, dano);
    }

    public ResultadoEncontroInimigo lutar(CartaInfo carta) {
        validarInimigo(carta);
        ResultadoCombate resultado = sistemaCombate.resolverCombate(
            partida.getJogador(),
            carta.getInimigo()
        );
        return ResultadoEncontroInimigo.combate(resultado);
    }

    public void concluirInimigo(
        int linha,
        int coluna,
        ResultadoEncontroInimigo resultado
    ) {
        Objects.requireNonNull(resultado, "resultado");
        switch (resultado.getDesfecho()) {
            case FURTIVIDADE_SUCESSO:
            case COMBATE_VENCIDO:
                validarDestino(linha, coluna, TipoCarta.INIMIGO);
                partida.getTabuleiro().consumirCarta(linha, coluna);
                break;
            case JOGADOR_DERROTADO:
                partida.registrarDerrota();
                break;
            case RECUO:
            case FURTIVIDADE_FALHOU:
                break;
            default:
                throw new IllegalStateException("Desfecho de inimigo desconhecido.");
        }
    }

    private void validarDestino(int linha, int coluna, TipoCarta esperado) {
        Tabuleiro tabuleiro = partida.getTabuleiro();
        if (!tabuleiro.podeMover(linha, coluna) || tabuleiro.getCarta(linha, coluna) != esperado) {
            throw new IllegalArgumentException("A carta não é um destino válido para " + esperado + ".");
        }
    }

    private void validarInimigo(CartaInfo carta) {
        if (carta == null || carta.getTipo() != TipoCarta.INIMIGO || carta.getInimigo() == null) {
            throw new IllegalArgumentException("Carta de inimigo inválida.");
        }
    }
}
