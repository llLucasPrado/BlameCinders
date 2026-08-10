package com.blamecinders.animacao;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.math.Interpolation;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.ui.carta.CartaExibida;
import com.blamecinders.util.ProvedorPosicaoCarta;
import com.blamecinders.ui.tabuleiro.CartaVisual;

import java.util.Objects;
import java.util.function.Consumer;

/** Anima o herói e o segmento da esteira afetado por cada movimento. */
public class AnimacaoTabuleiro {

    private final Consumer<Actor> adicionarAtor;
    private final CartaVisual[][] cartasVisuais;
    private final float cartaLargura;
    private final float cartaAltura;
    private final float espaco;
    private final ProvedorPosicaoCarta posicaoProvider;

    public AnimacaoTabuleiro(
        Stage stageTabuleiro,
        CartaVisual[][] cartasVisuais,
        float cartaLargura,
        float cartaAltura,
        float espaco,
        ProvedorPosicaoCarta posicaoProvider
    ) {
        this(
            Objects.requireNonNull(stageTabuleiro, "stageTabuleiro")::addActor,
            cartasVisuais,
            cartaLargura,
            cartaAltura,
            espaco,
            posicaoProvider
        );
    }

    public AnimacaoTabuleiro(
        Consumer<Actor> adicionarAtor,
        CartaVisual[][] cartasVisuais,
        float cartaLargura,
        float cartaAltura,
        float espaco,
        ProvedorPosicaoCarta posicaoProvider
    ) {
        this.adicionarAtor = Objects.requireNonNull(adicionarAtor, "adicionarAtor");
        this.cartasVisuais = cartasVisuais;
        this.cartaLargura = cartaLargura;
        this.cartaAltura = cartaAltura;
        this.espaco = espaco;
        this.posicaoProvider = posicaoProvider;
    }

    public void animarMovimentoJogadorComEsteira(
        int antigaLinha,
        int antigaColuna,
        int novaLinha,
        int novaColuna,
        Runnable aoFinalizar
    ) {
        CartaVisual jogador = cartasVisuais[antigaLinha][antigaColuna];

        if (jogador == null) {
            if (aoFinalizar != null) aoFinalizar.run();
            return;
        }

        final float levantarDuracao = 0.12f;
        final float esteiraDuracao = 0.30f;
        final float moverJogadorDuracao = 0.24f;

        final float origemX = getCartaX(antigaColuna);
        final float origemY = getCartaY(antigaLinha);

        final float destinoX = getCartaX(novaColuna);
        final float destinoY = getCartaY(novaLinha);

        jogador.clearActions();
        jogador.setOrigin(Align.center);
        jogador.setVisible(true);
        jogador.setPosition(origemX, origemY);
        jogador.setScale(1f, 1f);
        jogador.getColor().a = 1f;
        jogador.toFront();

        jogador.addAction(
            Actions.sequence(


                Actions.parallel(
                    Actions.moveTo(origemX, origemY + 18f, levantarDuracao, Interpolation.sineOut),
                    Actions.scaleTo(1.07f, 1.12f, levantarDuracao, Interpolation.sineOut)
                ),

                Actions.run(() -> {
                    animarDeslizamentoEsteira(
                        antigaLinha,
                        antigaColuna,
                        novaLinha,
                        novaColuna
                    );

                    jogador.toFront();
                }),

                Actions.delay(esteiraDuracao * 0.82f),

                Actions.parallel(
                    Actions.moveTo(destinoX, destinoY, moverJogadorDuracao, Interpolation.sine),
                    Actions.scaleTo(1f, 1f, moverJogadorDuracao, Interpolation.sine)
                ),

                Actions.run(() -> {
                    if (aoFinalizar != null) {
                        aoFinalizar.run();
                    }
                })
            )
        );
    }

    private void animarDeslizamentoEsteira(int antigaLinha, int antigaColuna, int novaLinha, int novaColuna) {
        int dx = novaColuna - antigaColuna;
        int dy = novaLinha - antigaLinha;

        animarEntradaTemporariaEsteira(
            antigaLinha,
            antigaColuna,
            novaLinha,
            novaColuna
        );

        if (dx != 0) {
            animarLinhaEsteira(antigaLinha, antigaColuna, dx);
        } else if (dy != 0) {
            animarColunaEsteira(antigaColuna, antigaLinha, dy);
        }
    }

    private void animarLinhaEsteira(int linha, int colunaJogador, int dx) {
        float deslocamento = cartaLargura + espaco;
        float moveX = dx * deslocamento;
        int inicio = dx == 1 ? 0 : colunaJogador + 1;
        int fimExclusivo = dx == 1 ? colunaJogador : Tabuleiro.COLUNAS;

        for (int coluna = inicio; coluna < fimExclusivo; coluna++) {
            CartaVisual carta = cartasVisuais[linha][coluna];
            if (carta == null) continue;
            float atraso = calcularAtrasoSuave(coluna, colunaJogador);
            animarCartaDeslizandoNaEsteira(carta, moveX, 0f, atraso);
        }
    }

    // O eixo Y do grid cresce para baixo; no Scene2D, cresce para cima.
    private void animarColunaEsteira(int coluna, int linhaJogador, int dy) {
        float deslocamento = cartaAltura + espaco;
        float moveY = -dy * deslocamento;
        int inicio = dy == 1 ? 0 : linhaJogador + 1;
        int fimExclusivo = dy == 1 ? linhaJogador : Tabuleiro.LINHAS;

        for (int linha = inicio; linha < fimExclusivo; linha++) {
            CartaVisual carta = cartasVisuais[linha][coluna];
            if (carta == null) continue;
            float atraso = calcularAtrasoSuave(linha, linhaJogador);
            animarCartaDeslizandoNaEsteira(carta, 0f, moveY, atraso);
        }
    }

    private void animarCartaDeslizandoNaEsteira(CartaVisual carta, float moveX, float moveY, float delay) {
        if (carta == null) return;

        carta.clearActions();
        carta.setOrigin(Align.center);

        carta.setVisible(true);
        carta.getColor().a = 1f;
        carta.setScale(1f, 1f);

        final float inicioX = carta.getX();
        final float inicioY = carta.getY();

        carta.addAction(
            Actions.sequence(
                Actions.delay(delay),
                Actions.parallel(
                    Actions.moveTo(inicioX + moveX, inicioY + moveY, (float) 0.3, Interpolation.sine),
                    Actions.sequence(
                        Actions.scaleTo(1.025f, 0.985f, (float) 0.3 * 0.38f, Interpolation.sineOut),
                        Actions.scaleTo(1f, 1f, (float) 0.3 * 0.62f, Interpolation.sineIn)
                    )
                )
            )
        );
    }

    private float calcularAtrasoSuave(int indiceCarta, int indiceJogador) {
        return Math.abs(indiceCarta - indiceJogador) * 0.008f;
    }

    public void animarCartaMovimentoInvalido(int linha, int coluna) {
        CartaVisual carta = cartasVisuais[linha][coluna];
        if (carta == null) return;

        carta.setBloqueandoAnimacaoClique(true);
        carta.toFront();

        final float xOriginal = carta.getX();
        final float yOriginal = carta.getY();
        final float scaleXOriginal = carta.getScaleX();
        final float scaleYOriginal = carta.getScaleY();
        final Color corOriginal = new Color(carta.getColor());

        carta.clearActions();

        carta.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.color(new Color(1f, 0.15f, 0.15f, corOriginal.a), 0.08f),
                    Actions.sequence(
                        Actions.moveTo(xOriginal - 12f, yOriginal, 0.025f),
                        Actions.moveTo(xOriginal + 12f, yOriginal, 0.04f),
                        Actions.moveTo(xOriginal - 10f, yOriginal, 0.035f),
                        Actions.moveTo(xOriginal + 8f, yOriginal, 0.03f),
                        Actions.moveTo(xOriginal - 4f, yOriginal, 0.02f),
                        Actions.moveTo(xOriginal, yOriginal, 0.02f)
                    )
                ),
                Actions.color(corOriginal, 0.14f),
                Actions.run(() -> {
                    carta.setPosition(xOriginal, yOriginal);
                    carta.setScale(scaleXOriginal, scaleYOriginal);
                    carta.setColor(corOriginal);
                    carta.setBloqueandoAnimacaoClique(false);
                })
            )
        );
    }

    private float getCartaX(int coluna) {
        return posicaoProvider.getCartaX(coluna);
    }

    private float getCartaY(int linha) {
        return posicaoProvider.getCartaY(linha);
    }

    private void animarEntradaTemporariaEsteira(int antigaLinha, int antigaColuna, int novaLinha, int novaColuna) {
        int dx = novaColuna - antigaColuna;
        int dy = novaLinha - antigaLinha;

        int linhaEntrada;
        int colunaEntrada;

        float origemX;
        float origemY;
        float destinoX;
        float destinoY;

        float passoX = cartaLargura + espaco;
        float passoY = cartaAltura + espaco;

        if (dx == 1) {
            linhaEntrada = antigaLinha;
            colunaEntrada = 0;

            destinoX = getCartaX(colunaEntrada);
            destinoY = getCartaY(linhaEntrada);

            origemX = destinoX - passoX;
            origemY = destinoY;

        } else if (dx == -1) {
            linhaEntrada = antigaLinha;
            colunaEntrada = Tabuleiro.COLUNAS - 1;

            destinoX = getCartaX(colunaEntrada);
            destinoY = getCartaY(linhaEntrada);

            origemX = destinoX + passoX;
            origemY = destinoY;

        } else if (dy == 1) {
            linhaEntrada = 0;
            colunaEntrada = antigaColuna;

            destinoX = getCartaX(colunaEntrada);
            destinoY = getCartaY(linhaEntrada);

            origemX = destinoX;
            origemY = destinoY + passoY;

        } else if (dy == -1) {
            linhaEntrada = Tabuleiro.LINHAS - 1;
            colunaEntrada = antigaColuna;

            destinoX = getCartaX(colunaEntrada);
            destinoY = getCartaY(linhaEntrada);

            origemX = destinoX;
            origemY = destinoY - passoY;

        } else {
            return;
        }

        CartaVisual cartaReferencia = cartasVisuais[linhaEntrada][colunaEntrada];
        if (cartaReferencia == null) return;

        // A carta de entrada ainda é desconhecida e deve usar o verso neutro.
        CartaExibida cartaTemp = new CartaExibida(
            cartaReferencia.getFundoVerso(),
            "VERSO",
            cartaReferencia.getFonte()
        );

        cartaTemp.setSize(cartaReferencia.getWidth(), cartaReferencia.getHeight());
        cartaTemp.setOrigin(Align.center);
        cartaTemp.setPosition(origemX, origemY);
        cartaTemp.setScale(0.96f, 0.96f);

        cartaTemp.setColor(1f, 1f, 1f, 0f);

        adicionarAtor.accept(cartaTemp);

        cartaTemp.toFront();

        // Impede a carta temporária de passar sobre o herói.
        cartaTemp.toBack();

        cartaTemp.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveTo(destinoX, destinoY, (float) 0.3, Interpolation.sine),
                    Actions.alpha(1f, (float) 0.3 * 0.75f, Interpolation.fade),
                    Actions.scaleTo(1f, 1f, (float) 0.3, Interpolation.sine)
                ),

                // Evita um quadro vazio antes da sincronização da carta real.
                Actions.delay(0.22f),

                Actions.removeActor()
            )
        );
    }

}
