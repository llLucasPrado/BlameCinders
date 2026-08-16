package com.blamecinders.ui.tabuleiro;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.animacao.AnimacaoTabuleiro;
import com.blamecinders.aplicacao.MovimentoTabuleiro;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.tabuleiro.Tabuleiro;
import com.blamecinders.tabuleiro.TipoCarta;
import com.blamecinders.telas.Tela;

/**
 * Dono da representação Scene2D do tabuleiro.
 * Mantém atores, layout, destaques e animações sincronizados com o grid lógico.
 */
public final class TelaTabuleiro implements Disposable, Tela {

    public static final float LARGURA_MUNDO = 1280f;
    public static final float ALTURA_MUNDO = 720f;
    public static final float ESPACO_CARTAS = 8f;

    private static final String VERSO = "VERSO";
    private static final String HEROI = "HERÓI-TESTE";

    private final Tabuleiro tabuleiro;
    private final Stage stage;
    private final CartaVisual[][] cartas;
    private final LayoutTabuleiro layout;
    private final AnimacaoTabuleiro animacao;
    private boolean destruida = false;

    public TelaTabuleiro(
        Tabuleiro tabuleiro,
        BitmapFont fonteCarta,
        InteracaoCartaVisual interacao
    ) {
        if (tabuleiro == null || fonteCarta == null || interacao == null) {
            throw new IllegalArgumentException("Tela do tabuleiro recebeu uma dependência nula.");
        }

        this.tabuleiro = tabuleiro;
        stage = new Stage(new FitViewport(LARGURA_MUNDO, ALTURA_MUNDO));
        cartas = new CartaVisual[Tabuleiro.LINHAS][Tabuleiro.COLUNAS];
        layout = new LayoutTabuleiro(
            Tabuleiro.LINHAS,
            Tabuleiro.COLUNAS,
            CartaVisual.LARGURA,
            CartaVisual.ALTURA,
            ESPACO_CARTAS,
            stage.getViewport().getWorldWidth(),
            stage.getViewport().getWorldHeight()
        );

        criarCartas(fonteCarta, interacao);
        animacao = new AnimacaoTabuleiro(
            stage,
            cartas,
            CartaVisual.LARGURA,
            CartaVisual.ALTURA,
            ESPACO_CARTAS,
            layout
        );
    }

    public Stage getStage() {
        return stage;
    }

    public CartaVisual getCarta(int linha, int coluna) {
        return cartas[linha][coluna];
    }

    public void resize(int largura, int altura) {
        stage.getViewport().update(largura, altura, true);
        layout.atualizarMundo(
            stage.getViewport().getWorldWidth(),
            stage.getViewport().getWorldHeight()
        );
    }

    public void act(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

            @Override
    public void mostrar() {
        stage.getRoot().setVisible(true);
    }

    @Override
    public void render(float delta) {
        act(delta);
        draw();
    }

    @Override
    public void redimensionar(int largura, int altura) {
        resize(largura, altura);
    }

    @Override
    public void esconder() {
        stage.getRoot().setVisible(false);
    }

    @Override
    public void destruir() {
        dispose();
    }

    @Override
    public void dispose() {
        if (destruida) {
            return;
        }

        destruida = true;
        stage.dispose();
    }

    public void animarMovimentoInvalido(int linha, int coluna) {
        animacao.animarCartaMovimentoInvalido(linha, coluna);
    }

    public void animarMovimento(MovimentoTabuleiro movimento, Runnable aoFinalizar) {
        animacao.animarMovimentoJogadorComEsteira(
            movimento.getLinhaOrigem(),
            movimento.getColunaOrigem(),
            movimento.getLinhaDestino(),
            movimento.getColunaDestino(),
            aoFinalizar
        );
    }

    public void remapearAposEsteira(MovimentoTabuleiro movimento) {
        RemapeadorGradeEsteira.remapear(cartas, movimento);
    }

    public void sincronizar() {
        int jogadorLinha = tabuleiro.getJogadorLinha();
        int jogadorColuna = tabuleiro.getJogadorColuna();

        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                CartaVisual carta = cartas[linha][coluna];
                if (carta == null) continue;

                prepararCarta(carta, linha, coluna);
                if (linha == jogadorLinha && coluna == jogadorColuna) {
                    carta.setFrente(HEROI);
                    carta.setRevelada(true);
                    carta.toFront();
                } else {
                    aplicarEstadoLogico(carta, linha, coluna);
                }
            }
        }
    }

    public void restaurarCarta(int linha, int coluna, CartaVisual carta) {
        if (carta == null) return;
        adicionarAoStageSeNecessario(carta);
        cartas[linha][coluna] = carta;
        prepararCarta(carta, linha, coluna);

        if (linha == tabuleiro.getJogadorLinha() && coluna == tabuleiro.getJogadorColuna()) {
            carta.setFrente(HEROI);
            carta.setRevelada(true);
            carta.toFront();
        } else {
            aplicarEstadoLogico(carta, linha, coluna);
        }
    }

    public void recolocarComoPlaceholder(int linha, int coluna, CartaVisual carta) {
        if (carta == null) return;
        adicionarAoStageSeNecessario(carta);
        cartas[linha][coluna] = carta;

        carta.clearActions();
        carta.setPosicaoGrid(linha, coluna);
        carta.setPosition(layout.getCartaX(coluna), layout.getCartaY(linha));
        carta.setOrigin(Align.center);
        carta.setScale(1f, 1f);
        carta.setRotation(0f);
        carta.setRevelada(false);
        carta.setVisible(false);
        carta.getColor().a = 0f;
    }

    public void atualizarDestaques() {
        int jogadorLinha = tabuleiro.getJogadorLinha();
        int jogadorColuna = tabuleiro.getJogadorColuna();

        for (int linha = 0; linha < cartas.length; linha++) {
            for (int coluna = 0; coluna < cartas[linha].length; coluna++) {
                CartaVisual carta = cartas[linha][coluna];
                if (carta == null) continue;

                boolean jogador = linha == jogadorLinha && coluna == jogadorColuna;
                boolean adjacente =
                    Math.abs(linha - jogadorLinha) + Math.abs(coluna - jogadorColuna) == 1;
                carta.clearActions();

                if (jogador) {
                    carta.addAction(Actions.color(Color.WHITE, 0.18f, Interpolation.fade));
                } else if (adjacente) {
                    carta.addAction(criarPulsoAdjacente());
                } else {
                    carta.addAction(Actions.color(
                        new Color(0.8f, 0.85f, 1f, 0.65f),
                        0.25f,
                        Interpolation.fade
                    ));
                }
            }
        }
    }

    private void criarCartas(BitmapFont fonteCarta, InteracaoCartaVisual interacao) {
        for (int linha = 0; linha < Tabuleiro.LINHAS; linha++) {
            for (int coluna = 0; coluna < Tabuleiro.COLUNAS; coluna++) {
                CartaVisual carta = new CartaVisual(
                    getIdentificador(linha, coluna),
                    VERSO,
                    layout.getCartaX(coluna),
                    layout.getCartaY(linha),
                    linha,
                    coluna,
                    interacao,
                    fonteCarta
                );
                if (linha == tabuleiro.getJogadorLinha() && coluna == tabuleiro.getJogadorColuna()) {
                    carta.setRevelada(true);
                }
                cartas[linha][coluna] = carta;
                stage.addActor(carta);
            }
        }
    }

    private void prepararCarta(CartaVisual carta, int linha, int coluna) {
        carta.clearActions();
        carta.setPosicaoGrid(linha, coluna);
        carta.setSize(CartaVisual.LARGURA, CartaVisual.ALTURA);
        carta.setOrigin(Align.center);
        carta.setVisible(true);
        carta.getColor().a = 1f;
        carta.setScale(1f, 1f);
        carta.setRotation(0f);
        carta.setPosition(layout.getCartaX(coluna), layout.getCartaY(linha));
    }

    private void aplicarEstadoLogico(CartaVisual carta, int linha, int coluna) {
        carta.setFrente(getIdentificador(linha, coluna));
        boolean vazia = tabuleiro.getCarta(linha, coluna) == TipoCarta.VAZIO;
        carta.setRevelada(!vazia && tabuleiro.cartaEstaRevelada(linha, coluna));
    }

    private void adicionarAoStageSeNecessario(CartaVisual carta) {
        if (carta.getStage() == null) {
            stage.addActor(carta);
        }
    }

    public String getIdentificador(int linha, int coluna) {
        if (linha == tabuleiro.getJogadorLinha() && coluna == tabuleiro.getJogadorColuna()) {
            return HEROI;
        }

        CartaInfo info = tabuleiro.getCartaInfo(linha, coluna);
        if (info == null) return VERSO;

        switch (info.getTipo()) {
            case INIMIGO:
                return info.getInimigo() == null
                    ? "INIMIGO"
                    : info.getInimigo().getIdentificadorVisual();
            case BAU:
                return "BAÚ";
            case CHAMA:
                return "CHAMA";
            case PAREDE:
                return "PAREDE";
            case VAZIO:
                return VERSO;
            default:
                throw new IllegalStateException("Tipo de carta sem representação visual.");
        }
    }

    private static com.badlogic.gdx.scenes.scene2d.Action criarPulsoAdjacente() {
        return Actions.sequence(
            Actions.color(
                new Color(1f, 1f, 1f, 0.90f),
                0.28f,
                Interpolation.fade
            ),
            Actions.forever(
                Actions.sequence(
                    Actions.color(
                        new Color(0.86f, 0.92f, 1f, 1f),
                        0.85f,
                        Interpolation.sine
                    ),
                    Actions.color(
                        new Color(1f, 1f, 1f, 0.90f),
                        0.85f,
                        Interpolation.sine
                    )
                )
            )
        );
    }
}
