package com.blamecinders.ui;

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
import com.badlogic.gdx.utils.Align;
import com.blamecinders.audio.GerenciadorAudio;

import java.util.Objects;
import java.util.function.BooleanSupplier;

final class PopupPause {

    private static final String NOME_POPUP = "popupPause";
    private static final String NOME_OVERLAY = "overlayPause";
    private static final float ESPERA_SALVAR_E_SAIR = 1.1f;

    private final Stage stage;
    private final Skin skin;
    private final GerenciadorAudio audio;

    PopupPause(Stage stage, Skin skin, GerenciadorAudio audio) {
        this.stage = Objects.requireNonNull(stage, "stage");
        this.skin = Objects.requireNonNull(skin, "skin");
        this.audio = Objects.requireNonNull(audio, "audio");
    }

    void mostrar(BooleanSupplier salvar, Runnable continuar, Runnable voltarMenu) {
        Objects.requireNonNull(salvar, "salvar");
        Objects.requireNonNull(continuar, "continuar");
        Objects.requireNonNull(voltarMenu, "voltarMenu");
        if (stage.getRoot().findActor(NOME_POPUP) != null) return;

        Image overlay = ElementosPopup.criarOverlay(stage, 0.75f);
        overlay.setName(NOME_OVERLAY);
        stage.addActor(overlay);

        Window popup = new Window("", skin);
        popup.setName(NOME_POPUP);
        montarMenuPrincipal(popup, overlay, salvar, continuar, voltarMenu, "");

        stage.addActor(popup);
        AnimadorPopup.abrir(overlay);
        AnimadorPopup.abrir(popup);
        overlay.toFront();
        popup.toFront();
    }

    void fechar(Runnable aoFinalizar) {
        Actor popup = stage.getRoot().findActor(NOME_POPUP);
        Actor overlay = stage.getRoot().findActor(NOME_OVERLAY);
        AnimadorPopup.fecharComOverlay(popup, overlay, aoFinalizar);
    }

    private void montarMenuPrincipal(
        Window popup,
        Image overlay,
        BooleanSupplier salvar,
        Runnable continuar,
        Runnable voltarMenu,
        String mensagem
    ) {
        popup.clearChildren();

        Label titulo = criarTitulo("PAUSADO", 2.2f);
        Label aviso = criarAviso(mensagem);
        TextButton btnContinuar = new TextButton("Continuar", skin);
        TextButton btnSalvarSair = new TextButton("Salvar / Sair", skin);
        TextButton btnMusica = new TextButton(textoMusica(), skin);
        TextButton btnEfeitos = new TextButton(textoEfeitos(), skin);

        btnContinuar.addListener(aoClicar(() ->
            AnimadorPopup.fecharComOverlay(popup, overlay, continuar)
        ));
        btnSalvarSair.addListener(aoClicar(() ->
            montarMenuSalvar(popup, overlay, salvar, continuar, voltarMenu)
        ));
        btnMusica.addListener(aoClicar(() -> {
            audio.setMusicaAtiva(!audio.isMusicaAtiva());
            btnMusica.setText(textoMusica());
        }));
        btnEfeitos.addListener(aoClicar(() -> {
            audio.setEfeitosAtivos(!audio.isEfeitosAtivos());
            btnEfeitos.setText(textoEfeitos());
        }));

        popup.add(titulo).width(420).pad(20);
        popup.row();
        popup.add(btnContinuar).width(220).pad(8);
        popup.row();
        popup.add(btnSalvarSair).width(220).pad(8);
        popup.row();
        popup.add(btnMusica).width(220).pad(8);
        popup.row();
        popup.add(btnEfeitos).width(220).pad(8);
        popup.row();
        popup.add(aviso).width(420).pad(8);
        ajustarPopup(popup);
    }

    private void montarMenuSalvar(
        Window popup,
        Image overlay,
        BooleanSupplier salvar,
        Runnable continuar,
        Runnable voltarMenu
    ) {
        popup.clearChildren();

        Label titulo = criarTitulo("SALVAR / SAIR", 1.8f);
        Label aviso = criarAviso("");
        TextButton btnSalvar = new TextButton("Salvar", skin);
        TextButton btnSalvarSair = new TextButton("Salvar e sair", skin);

        btnSalvar.addListener(aoClicar(() -> {
            if (salvar.getAsBoolean()) {
                montarMenuPrincipal(
                    popup,
                    overlay,
                    salvar,
                    continuar,
                    voltarMenu,
                    "Jogo salvo."
                );
            } else {
                aviso.setText("Não é possível salvar durante uma ação.");
            }
        }));
        btnSalvarSair.addListener(aoClicar(() -> {
            if (salvar.getAsBoolean()) {
                btnSalvar.setDisabled(true);
                btnSalvarSair.setDisabled(true);
                aviso.setText("Jogo salvo.");
                popup.addAction(Actions.sequence(
                    Actions.delay(ESPERA_SALVAR_E_SAIR),
                    Actions.run(() ->
                        AnimadorPopup.fecharComOverlay(popup, overlay, voltarMenu)
                    )
                ));
            } else {
                aviso.setText("Não é possível salvar durante uma ação.");
            }
        }));

        popup.add(titulo).width(420).pad(20);
        popup.row();
        popup.add(btnSalvar).width(220).pad(8);
        popup.row();
        popup.add(btnSalvarSair).width(220).pad(8);
        popup.row();
        popup.add(aviso).width(420).pad(8);
        ajustarPopup(popup);
    }

    private Label criarTitulo(String texto, float escala) {
        Label titulo = new Label(texto, skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(escala);
        return titulo;
    }

    private Label criarAviso(String texto) {
        Label aviso = new Label(texto, skin);
        aviso.setAlignment(Align.center);
        return aviso;
    }

    private void ajustarPopup(Window popup) {
        popup.pack();
        ElementosPopup.centralizar(stage, popup);
        popup.toFront();
    }

    private static ClickListener aoClicar(Runnable acao) {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                acao.run();
            }
        };
    }

    private String textoMusica() {
        return "Música: " + (audio.isMusicaAtiva() ? "ligada" : "desligada");
    }

    private String textoEfeitos() {
        return "Efeitos: " + (audio.isEfeitosAtivos() ? "ligados" : "desligados");
    }
}
