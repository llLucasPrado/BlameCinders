package com.blamecinders.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.blamecinders.animacao.AnimacaoCarta;
import com.blamecinders.item.Arma;
import com.blamecinders.item.Comida;
import com.blamecinders.item.ItemBau;
import com.blamecinders.tabuleiro.CartaInfo;
import com.blamecinders.util.GerenciadorTexturas;

/** Cria e anima os popups e overlays da partida. */
public class GerenciadorPopups {

    private final Stage stageUI;
    private final Stage stageCartaZoom;
    private final Skin skin;

    public GerenciadorPopups(Stage stageUI, Stage stageCartaZoom, Skin skin) {
        this.stageUI = stageUI;
        this.stageCartaZoom = stageCartaZoom;
        this.skin = skin;
    }

    public void mostrarConfirmacaoCarta(Runnable confirmar, Runnable cancelar) {
        if (stageUI.getRoot().findActor("popupConfirmacao") != null) return;

        Window popup = new Window("", skin);
        popup.setName("popupConfirmacao");

        Label texto = new Label("Revelar esta carta?", skin);
        texto.setAlignment(Align.center);

        TextButton btnConfirmar = new TextButton("Confirmar", skin);
        TextButton btnCancelar = new TextButton("Cancelar", skin);

        btnConfirmar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animarFechamentoPopup(popup, confirmar);
            }
        });

        btnCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animarFechamentoPopup(popup, cancelar);
            }
        });

        popup.add(texto).colspan(2).pad(20);
        popup.row();
        popup.add(btnConfirmar).width(140).pad(10);
        popup.add(btnCancelar).width(140).pad(10);

        popup.pack();
        centralizar(stageUI, popup);
        stageUI.addActor(popup);
        animarAberturaPopup(popup);
        popup.toFront();
    }

    public void mostrarPopupMensagem(String mensagem, Runnable confirmar) {
        Window popup = new Window("", skin);

        Label texto = new Label(mensagem, skin);
        texto.setAlignment(Align.center);

        TextButton btn = new TextButton("Confirmar", skin);

        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animarFechamentoPopup(popup, confirmar);
            }
        });

        popup.add(texto).pad(20);
        popup.row();
        popup.add(btn).width(180).pad(15);

        popup.pack();
        centralizarZoom(popup);
        stageCartaZoom.addActor(popup);
        animarAberturaPopup(popup);
        popup.toFront();
    }

    public void mostrarGameOver() {
        stageCartaZoom.clear();

        Image overlay = criarOverlayBloqueador(0.85f);
        stageCartaZoom.addActor(overlay);

        Label titulo = new Label("GAME OVER", skin);
        titulo.setFontScale(2.2f);
        titulo.setAlignment(Align.center);
        titulo.setWidth(500);

        Label sub = new Label(
            "Você foi derrotado.\nO menu de runs será ligado depois.",
            skin
        );
        sub.setAlignment(Align.center);
        sub.setWidth(500);

        float centroX = stageCartaZoom.getViewport().getWorldWidth() / 2f;
        float centroY = stageCartaZoom.getViewport().getWorldHeight() / 2f;

        titulo.setPosition(
            centroX - titulo.getWidth() / 2f,
            centroY + 40
        );

        sub.setPosition(
            centroX - sub.getWidth() / 2f,
            centroY - 40
        );

        stageCartaZoom.addActor(titulo);
        stageCartaZoom.addActor(sub);

    }

    public Image criarOverlayBloqueador(float alpha) {
        Image overlay = new Image(new TextureRegionDrawable(
            GerenciadorTexturas.getSolid(Color.BLACK)
        ));
        overlay.setSize(
            stageCartaZoom.getViewport().getWorldWidth(),
            stageCartaZoom.getViewport().getWorldHeight()
        );
        overlay.setColor(0f, 0f, 0f, alpha);

        overlay.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
            }
        });

        return overlay;
    }

    private void centralizar(Stage stage, Window popup) {
        popup.setPosition(
            stage.getViewport().getWorldWidth() / 2f - popup.getWidth() / 2f,
            stage.getViewport().getWorldHeight() / 2f - popup.getHeight() / 2f
        );
    }

    private void centralizarZoom(Window popup) {
        popup.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - popup.getWidth() / 2f,
            stageCartaZoom.getViewport().getWorldHeight() / 2f - 300
        );
    }

    public void mostrarConfirmacaoVisualizarCarta(Runnable visualizar, Runnable cancelar) {
        if (stageUI.getRoot().findActor("popupVisualizarCarta") != null) return;

        Window popup = new Window("", skin);
        popup.setName("popupVisualizarCarta");

        Label texto = new Label("Visualizar esta carta?", skin);
        texto.setAlignment(Align.center);

        TextButton btnVisualizar = new TextButton("Visualizar", skin);
        TextButton btnCancelar = new TextButton("Cancelar", skin);

        btnVisualizar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animarFechamentoPopup(popup, visualizar);
            }
        });

        btnCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animarFechamentoPopup(popup, cancelar);
            }
        });

        popup.add(texto).colspan(2).pad(20);
        popup.row();
        popup.add(btnVisualizar).width(170).pad(10);
        popup.add(btnCancelar).width(170).pad(10);

        popup.pack();

        popup.setPosition(
            stageUI.getViewport().getWorldWidth() / 2f - popup.getWidth() / 2f,
            stageUI.getViewport().getWorldHeight() / 2f - popup.getHeight() / 2f
        );

        stageUI.addActor(popup);
        animarAberturaPopup(popup);
        popup.toFront();
    }

    public void mostrarPopupCartaReveladaComAcao(String mensagem, String textoAcao, Runnable acaoPrincipal, Runnable cancelar) {
        Window popup = new Window("", skin);
        popup.setName("popupCartaReveladaComAcao");

        Label texto = new Label(mensagem, skin);
        texto.setAlignment(Align.center);

        TextButton btnAcao = new TextButton(textoAcao, skin);
        TextButton btnCancelar = new TextButton("Cancelar", skin);

        btnAcao.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animarFechamentoPopup(popup, acaoPrincipal);
            }
        });

        btnCancelar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animarFechamentoPopup(popup, cancelar);
            }
        });

        popup.add(texto).colspan(2).pad(20);
        popup.row();
        popup.add(btnAcao).width(180).pad(10);
        popup.add(btnCancelar).width(180).pad(10);

        popup.pack();

        popup.setPosition(
            stageUI.getViewport().getWorldWidth() / 2f - popup.getWidth() / 2f,
            stageUI.getViewport().getWorldHeight() / 5f - popup.getHeight() / 2f
        );

        stageCartaZoom.addActor(popup);
        animarAberturaPopup(popup);
        popup.toFront();
    }

    public void mostrarDetalheArmaEquipada(
        String nomeArma,
        int durabilidade,
        String identificadorVisual,
        AnimacaoCarta animacaoCarta,
        Runnable aoFechar
    ) {
        Image overlay = criarOverlayBloqueador(0.75f);
        stageCartaZoom.addActor(overlay);

        Image cartaArma = new Image(new TextureRegionDrawable(
            new TextureRegion(GerenciadorTexturas.get(identificadorVisual))
        ));

        cartaArma.setSize(260, 360);
        cartaArma.setOrigin(Align.center);
        cartaArma.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - 130,
            stageCartaZoom.getViewport().getWorldHeight() / 2f - 140
        );

        Label labelInfo = new Label(
            nomeArma + "\nDurabilidade: " + durabilidade,
            skin
        );
        labelInfo.setAlignment(Align.center);
        labelInfo.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 1.93f - 120,
            120
        );

        TextButton btnFechar = new TextButton("Fechar", skin);
        btnFechar.setSize(160, 50);
        btnFechar.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - 80,
            50
        );

        stageCartaZoom.addActor(cartaArma);
        stageCartaZoom.addActor(labelInfo);
        stageCartaZoom.addActor(btnFechar);

        overlay.toFront();
        cartaArma.toFront();
        labelInfo.toFront();
        btnFechar.toFront();

        if (animacaoCarta != null) {
            animacaoCarta.aplicarIdleFlutuacao(cartaArma);
        }

        btnFechar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                // O stage pode conter a tela de combate por baixo deste popup.
                overlay.remove();
                cartaArma.remove();
                labelInfo.remove();
                btnFechar.remove();

                if (aoFechar != null) {
                    aoFechar.run();
                }
            }
        });
    }

    public void mostrarDecisaoItemBau(
        CartaInfo cartaInfo,
        boolean jogadorTemArma,
        Runnable aoUsar,
        Runnable aoDeixar
    ) {
        ItemBau item = cartaInfo != null ? cartaInfo.getItemDentro() : null;
        String texto;
        String btnPrincipal;
        String btnSecundario;

        if (item instanceof Arma) {
            Arma arma = (Arma) item;
            texto = arma.getNome() + "\nDurabilidade: " + arma.getDurabilidade();
            btnPrincipal = jogadorTemArma ? "Trocar arma" : "Equipar";
            btnSecundario = jogadorTemArma ? "Manter atual" : "Deixar";
        } else if (item instanceof Comida) {
            Comida comida = (Comida) item;
            texto = comida.getNome() + "\nCura: " + comida.getCura();
            btnPrincipal = "Consumir";
            btnSecundario = "Deixar";
        } else {
            texto = "Baú vazio.";
            btnPrincipal = "Fechar";
            btnSecundario = "Deixar";
        }

        Window popup = new Window("", skin);
        popup.setName("popupArmaBauRevelado");

        Label label = new Label(texto, skin);
        label.setAlignment(Align.center);

        TextButton btn1 = new TextButton(btnPrincipal, skin);
        TextButton btn2 = new TextButton(btnSecundario, skin);

        btn1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animarFechamentoPopup(popup, aoUsar);
            }
        });

        btn2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                animarFechamentoPopup(popup, aoDeixar);
            }
        });

        popup.add(label).colspan(2).pad(20);
        popup.row();
        popup.add(btn1).width(190).pad(10);
        popup.add(btn2).width(190).pad(10);

        popup.pack();

        popup.setPosition(
            stageCartaZoom.getViewport().getWorldWidth() / 2f - popup.getWidth() / 2f,
            stageCartaZoom.getViewport().getWorldHeight() / 2f - 300
        );

        stageCartaZoom.addActor(popup);
        animarAberturaPopup(popup);
        popup.toFront();
    }

    private void animarAberturaPopup(Actor actor) {
        if (actor == null) return;

        actor.clearActions();
        actor.setOrigin(Align.center);
        actor.setScale(0.01f, 1f);
        actor.getColor().a = 0f;

        actor.addAction(
            Actions.parallel(
                Actions.scaleTo(1f, 1f, 0.18f, Interpolation.fade),
                Actions.fadeIn(0.14f, Interpolation.fade)
            )
        );
    }

    private void animarFechamentoPopup(Actor actor, Runnable aoFinalizar) {
        if (actor == null) {
            if (aoFinalizar != null) aoFinalizar.run();
            return;
        }

        actor.clearActions();
        actor.setOrigin(Align.center);

        actor.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(1f, 0.01f, 0.16f, Interpolation.fade),
                    Actions.fadeOut(0.12f, Interpolation.fade)
                ),
                Actions.run(() -> {
                    actor.remove();

                    if (aoFinalizar != null) {
                        aoFinalizar.run();
                    }
                })
            )
        );
    }

    private void animarFechamentoPause(
    Actor popup,
    Actor overlay,
    Runnable aoFinalizar
    ) {

        popup.clearActions();
        overlay.clearActions();

        popup.setOrigin(Align.center);

        popup.addAction(
            Actions.parallel(
                Actions.scaleTo(
                    1f,
                    0.01f,
                    0.16f,
                    Interpolation.fade
                ),
                Actions.fadeOut(
                    0.12f,
                    Interpolation.fade
                )
            )
        );

        overlay.addAction(
            Actions.sequence(
                Actions.fadeOut(
                    0.12f,
                    Interpolation.fade
                ),
                Actions.run(() -> {

                    popup.remove();
                    overlay.remove();

                    if (aoFinalizar != null) {
                        Gdx.app.postRunnable(aoFinalizar);
                    }
                })
            )
        );
    }

    public void mostrarPause(
    Runnable continuar,
    Runnable opcoes,
    Runnable voltarMenu
    ) {

        if (stageUI.getRoot().findActor("popupPause") != null) {
            return;
        }

        Image overlay = criarOverlayBloqueador(0.75f);
        overlay.setName("overlayPause");

        stageUI.addActor(overlay);

        Window popup = new Window("", skin);
        popup.setName("popupPause");

        Label titulo = new Label("PAUSADO", skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(2.2f);

        TextButton btnContinuar =
            new TextButton("Continuar", skin);

        TextButton btnOpcoes =
            new TextButton("Opções", skin);

        TextButton btnVoltar =
            new TextButton("Voltar ao menu principal", skin);

        btnContinuar.addListener(new ClickListener() {

            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {

                animarFechamentoPause(
                    popup,
                    overlay,
                    continuar
                );
            }
        });

        btnOpcoes.addListener(new ClickListener() {

            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {

                opcoes.run();
            }
        });

        btnVoltar.addListener(new ClickListener() {

            @Override
            public void clicked(
                InputEvent event,
                float x,
                float y
            ) {

                animarFechamentoPause(
                    popup,
                    overlay,
                    voltarMenu
                );
            }
        });

        popup.add(titulo)
            .colspan(1)
            .width(420)
            .pad(20);

        popup.row();

        popup.add(btnContinuar)
            .width(220)
            .pad(8);

        popup.row();

        popup.add(btnOpcoes)
            .width(220)
            .pad(8);

        popup.row();

        popup.add(btnVoltar)
            .width(220)
            .pad(8);

        popup.pack();

        centralizar(stageUI, popup);

        stageUI.addActor(popup);

        animarAberturaPopup(overlay);
        animarAberturaPopup(popup);

        overlay.toFront();
        popup.toFront();
    }

    public void fecharPause(Runnable aoFinalizar) {

        Actor popup = stageUI.getRoot().findActor("popupPause");
        Actor overlay = stageUI.getRoot().findActor("overlayPause");

        if (popup == null) {
            if (aoFinalizar != null) {
                aoFinalizar.run();
            }
            return;
        }

        animarFechamentoPause(
            popup,
            overlay,
            aoFinalizar
        );
    }

}
