package com.blamecinders.fluxo;

import java.util.Objects;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.blamecinders.animacao.AnimacaoCarta;
import com.blamecinders.aplicacao.ControladorEncontro;
import com.blamecinders.aplicacao.DesfechoInimigo;
import com.blamecinders.aplicacao.ResultadoEncontroInimigo;
import com.blamecinders.combate.Inimigo;
import com.blamecinders.combate.Jogador;
import com.blamecinders.combate.ResultadoCombate;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.ui.GerenciadorPopups;
import com.blamecinders.ui.carta.CartaExibida;
import com.blamecinders.util.GerenciadorTexturas;

/** Monta a apresentação do combate e encaminha seu resultado ao jogo. */
public class FluxoCombate {

    private final Stage stageCartaZoom;
    private final Skin skin;
    private final AnimacaoCarta animacaoCarta;
    private final GerenciadorPopups popupManager;
    private final ControladorEncontro controladorEncontro;

    public FluxoCombate(
        Stage stageCartaZoom,
        Skin skin,
        AnimacaoCarta animacaoCarta,
        GerenciadorPopups popupManager,
        ControladorEncontro controladorEncontro
    ) {
        this.stageCartaZoom = stageCartaZoom;
        this.skin = skin;
        this.animacaoCarta = animacaoCarta;
        this.popupManager = popupManager;
        this.controladorEncontro = controladorEncontro;
    }

    public void mostrarTelaCombate(
        CartaInfo cartaInfo,
        Jogador jogadorCombate,
        Consumer<ResultadoEncontroInimigo> onResultado,
        Consumer<String> onMensagem
    ) {
        Objects.requireNonNull(onResultado, "onResultado");
        Objects.requireNonNull(onMensagem, "onMensagem");
        if (cartaInfo == null || cartaInfo.getInimigo() == null) {
            onMensagem.accept("Erro: inimigo não encontrado.");
            onResultado.accept(ResultadoEncontroInimigo.recuo());
            return;
        }

        Inimigo inimigo = cartaInfo.getInimigo();

        stageCartaZoom.clear();

        Image overlay = popupManager.criarOverlayBloqueador(0.75f);
        stageCartaZoom.addActor(overlay);

        CartaExibida cartaInimigo = new CartaExibida(
            GerenciadorTexturas.get(inimigo.getIdentificadorVisual()),
            inimigo.getIdentificadorVisual(),
            skin.getFont("default-font")
        );
        cartaInimigo.setSize(280, 380);
        cartaInimigo.setPosition(300, 300);
        stageCartaZoom.addActor(cartaInimigo);

        CartaExibida cartaJogador = new CartaExibida(
            GerenciadorTexturas.get("HERÓI-TESTE"),
            "HERÓI-TESTE",
            skin.getFont("default-font")
        );
        cartaJogador.setSize(280, 380);
        cartaJogador.setPosition(700, 300);
        stageCartaZoom.addActor(cartaJogador);

        animacaoCarta.aplicarIdleFlutuacao(cartaInimigo);
        animacaoCarta.aplicarIdleFlutuacao(cartaJogador);

        Image miniArmaCombate = null;

        // Protege a resolução contra eventos de clique repetidos do Scene2D.
        final boolean[] combateEmAndamento = { false };

        if (jogadorCombate.getArmaEquipada() != null) {
            miniArmaCombate = new Image(new TextureRegionDrawable(
                new TextureRegion(GerenciadorTexturas.get(jogadorCombate.getArmaEquipada().getIdentificadorVisual()))
            ));

            miniArmaCombate.setSize(70, 95);
            miniArmaCombate.setOrigin(Align.center);
            miniArmaCombate.setPosition(
                cartaJogador.getX() + cartaJogador.getWidth() + 5,
                cartaJogador.getY() + 5
            );

            stageCartaZoom.addActor(miniArmaCombate);

            Image miniArmaClicavel = miniArmaCombate;

            miniArmaClicavel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {

                    if (combateEmAndamento[0]) {
                        event.stop();
                        return;
                    }

                    popupManager.mostrarDetalheArmaEquipada(
                        jogadorCombate.getArmaEquipada().getNome(),
                        jogadorCombate.getArmaEquipada().getDurabilidade(),
                        jogadorCombate.getArmaEquipada().getIdentificadorVisual(),
                        animacaoCarta,
                        null
                    );
                }
            });
        }

        Label labelVidaInimigo = new Label(
            inimigo.getNome() + " - Vida: " + inimigo.getVida(),
            skin
        );
        labelVidaInimigo.setPosition(300, 215);
        stageCartaZoom.addActor(labelVidaInimigo);

        Integer durabilidadeInicial = null;
        if (jogadorCombate.getArmaEquipada() != null) {
            durabilidadeInicial = jogadorCombate.getArmaEquipada().getDurabilidade();
        }

        Label labelVidaJogador = new Label(
            montarTextoStatusJogadorCombate(
                jogadorCombate,
                jogadorCombate.getVida(),
                durabilidadeInicial
            ),
            skin
        );
        labelVidaJogador.setPosition(700, 215);
        stageCartaZoom.addActor(labelVidaJogador);

        TextButton btnVoltar = new TextButton("Voltar", skin);
        btnVoltar.setSize(140, 50);
        btnVoltar.setPosition(40, stageCartaZoom.getViewport().getWorldHeight() - 90);

        TextButton btnLutar = new TextButton("Lutar", skin);
        btnLutar.setSize(180, 60);
        btnLutar.setPosition(460, 90);

        TextButton btnFurtividade = new TextButton("Furtividade", skin);
        btnFurtividade.setSize(180, 60);
        btnFurtividade.setPosition(660, 90);
        if (cartaInfo.isFurtividadeTentada()) {
            btnFurtividade.setDisabled(true);
            btnFurtividade.setTouchable(Touchable.disabled);
        }

        btnVoltar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onResultado.accept(ResultadoEncontroInimigo.recuo());
            }
        });

        btnFurtividade.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (combateEmAndamento[0] || cartaInfo.isFurtividadeTentada()) {
                    event.stop();
                    return;
                }
                ResultadoEncontroInimigo resultado = controladorEncontro.tentarFurtividade(cartaInfo);
                btnFurtividade.setDisabled(true);
                btnFurtividade.setTouchable(Touchable.disabled);

                boolean sucesso = resultado.getDesfecho() == DesfechoInimigo.FURTIVIDADE_SUCESSO;

                combateEmAndamento[0] = true;
                btnLutar.setDisabled(true);
                btnVoltar.setDisabled(true);
                btnLutar.setTouchable(Touchable.disabled);
                btnVoltar.setTouchable(Touchable.disabled);

                Label labelProcesso = new Label("", skin);
                labelProcesso.setPosition(
                    cartaJogador.getX() + cartaJogador.getWidth() / 2f - labelProcesso.getPrefWidth() / 2f,
                    cartaJogador.getY() + cartaJogador.getHeight() + 12f
                );
                stageCartaZoom.addActor(labelProcesso);

                animacaoCarta.animarFurtividade(cartaJogador, cartaInimigo, stageCartaZoom, labelProcesso, sucesso, () -> {
                    labelProcesso.remove();
                    labelVidaJogador.setText(
                        montarTextoStatusJogadorCombate(
                            jogadorCombate,
                            jogadorCombate.getVida(),
                            jogadorCombate.getArmaEquipada() != null
                                ? jogadorCombate.getArmaEquipada().getDurabilidade()
                                : null
                        )
                    );
                    popupManager.mostrarPopupMensagem(resultado.getMensagem(), () -> {
                        onResultado.accept(resultado);
                    });
                });
            }
        });

        final Image miniArmaFinal = miniArmaCombate;

        btnLutar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                if (combateEmAndamento[0]) {
                    event.stop();
                    return;
                }

                combateEmAndamento[0] = true;

                // setDisabled não bloqueia todos os eventos; Touchable também é necessário.
                btnLutar.setDisabled(true);
                btnFurtividade.setDisabled(true);
                btnVoltar.setDisabled(true);

                btnLutar.setTouchable(Touchable.disabled);
                btnFurtividade.setTouchable(Touchable.disabled);
                btnVoltar.setTouchable(Touchable.disabled);
                if (miniArmaFinal != null) {
                    miniArmaFinal.setTouchable(Touchable.disabled);
                }

                ResultadoEncontroInimigo resultadoEncontro = controladorEncontro.lutar(cartaInfo);
                ResultadoCombate resultado = resultadoEncontro.getCombate();

                final int vidaInicialInimigo = inimigo.getVida();
                final int vidaInicialJogador = resultado.getVidaInicialJogador();
                final int vidaFinalJogador = Math.max(0, resultado.getVidaFinalJogador());

                final int durabilidadeInicialArma = resultado.getDurabilidadeInicialArma();
                final int durabilidadeFinalArma = Math.max(0, resultado.getDurabilidadeFinalArma());

                // Em derrota, o dano no inimigo é limitado ao quanto o herói resistiu.
                int danoAbsorvidoPelaArma = durabilidadeInicialArma - durabilidadeFinalArma;
                int danoRecebidoPeloJogador = vidaInicialJogador - vidaFinalJogador;
                int danoTotalSustentado = danoAbsorvidoPelaArma + danoRecebidoPeloJogador;

                final int vidaFinalInimigo = resultado.isJogadorVenceu()
                    ? 0
                    : Math.max(0, vidaInicialInimigo - danoTotalSustentado);

                final int[] vidaInimigoExibida = { vidaInicialInimigo };
                final int[] vidaJogadorExibida = { vidaInicialJogador };
                final int[] durabilidadeExibida = { durabilidadeInicialArma };

                animacaoCarta.animarImpactoJogador(cartaJogador);

                // Um único timer mantém os três contadores visuais sincronizados.
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {

                        boolean alterouAlgo = false;

                        if (vidaInimigoExibida[0] > vidaFinalInimigo) {
                            vidaInimigoExibida[0]--;
                            alterouAlgo = true;
                        }

                        if (durabilidadeExibida[0] > durabilidadeFinalArma) {
                            durabilidadeExibida[0]--;
                            alterouAlgo = true;
                        }
                        else if (vidaJogadorExibida[0] > vidaFinalJogador) {
                            vidaJogadorExibida[0]--;
                            alterouAlgo = true;
                        }

                        labelVidaInimigo.setText(
                            inimigo.getNome() + " - Vida: " + vidaInimigoExibida[0]
                        );

                        labelVidaJogador.setText(
                            montarTextoStatusJogadorCombate(
                                jogadorCombate,
                                vidaJogadorExibida[0],
                                durabilidadeExibida[0] > 0 ? durabilidadeExibida[0] : null
                            )
                        );

                        if (!alterouAlgo) {
                            cancel();

                            if (resultado.isArmaQuebrou()) {
                                animacaoCarta.animarQuebraArma(miniArmaFinal, stageCartaZoom, null);
                                onMensagem.accept("Sua arma quebrou!");
                            }

                            if (resultado.isJogadorVenceu()) {
                                animacaoCarta.resetarTransformacoes(cartaJogador);
                                cartaInimigo.clearActions();

                                animacaoCarta.animarDerrotaInimigo(cartaInimigo, stageCartaZoom, () -> {
                                    onResultado.accept(resultadoEncontro);
                                });

                            } else {
                                onResultado.accept(resultadoEncontro);
                            }
                        }
                    }
                }, 0.20f, 0.08f);
            }
        });

        stageCartaZoom.addActor(btnVoltar);
        stageCartaZoom.addActor(btnLutar);
        stageCartaZoom.addActor(btnFurtividade);
    }

    private String montarTextoStatusJogadorCombate(Jogador jogadorCombate, int vidaExibida, Integer durabilidadeExibida) {
        String texto = "Jogador - Vida: " + vidaExibida;

        if (jogadorCombate.getArmaEquipada() != null && durabilidadeExibida != null) {
            texto += "\nArma: " + jogadorCombate.getArmaEquipada().getNome()
                + " (" + durabilidadeExibida + ")";
        } else if (durabilidadeExibida != null && durabilidadeExibida > 0) {
            texto += "\nArma: equipada (" + durabilidadeExibida + ")";
        } else {
            texto += "\nArma: Sem arma";
        }

        return texto;
    }

}
