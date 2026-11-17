package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.ui.VisualMenu;

import java.util.Objects;

public final class TelaPrologo implements Tela {

    private static final String TEXTO =
        "Antes de qualquer reino existir, havia apenas o fogo.\n\n"
        + "Não um fogo comum, mas três chamas primordiais. Elas não apenas "
        + "aqueciam ou iluminavam: definiam o que era existir.\n\n"
        + "Com o passar das eras, cada chama foi levada para um lugar onde "
        + "nenhum homem ousaria permanecer. Desde então, muitos partiram em "
        + "busca de vida eterna, poder e glória.\n\n"
        + "Mas as chamas não são apenas poder. Elas são fragmentos de uma chave. "
        + "E toda chave existe para abrir alguma coisa.";

    private final AcaoTela continuar;
    private final AcaoTela voltar;
    private final GerenciadorAudio audio;
    private final Skin skin;
    private Stage stage;
    private boolean encerrando;

    public TelaPrologo(
        AcaoTela continuar,
        AcaoTela voltar,
        GerenciadorAudio audio,
        Skin skin
    ) {
        this.continuar = Objects.requireNonNull(continuar, "continuar");
        this.voltar = Objects.requireNonNull(voltar, "voltar");
        this.audio = Objects.requireNonNull(audio, "audio");
        this.skin = Objects.requireNonNull(skin, "skin");
    }

    @Override
    public void mostrar() {
        stage = new Stage(new FitViewport(VisualMenu.LARGURA, VisualMenu.ALTURA));
        VisualMenu.adicionarFundo(stage, VisualMenu.FUNDO_CAVALEIRO, 0.46f);

        Group tituloPainel = VisualMenu.criarPainel(300f, 590f, 680f, 86f);
        Label titulo = new Label("DESCOBRINDO A CHAMA", skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(1.65f);
        titulo.setFillParent(true);
        tituloPainel.addActor(titulo);
        stage.addActor(tituloPainel);

        Group textoPainel = VisualMenu.criarPainel(230f, 145f, 820f, 410f);
        Label texto = new Label(TEXTO, skin);
        texto.setWrap(true);
        texto.setAlignment(Align.topLeft);
        texto.setColor(new Color(0.90f, 0.91f, 0.93f, 1f));
        texto.setBounds(38f, 34f, 744f, 342f);
        textoPainel.addActor(texto);
        stage.addActor(textoPainel);

        Label avancar = criarAcao("CONTINUAR", 850f, 72f, this::avancar);
        Label voltarLabel = criarAcao("VOLTAR", 230f, 72f, this::voltar);
        stage.addActor(avancar);
        stage.addActor(voltarLabel);

        VisualMenu.animarEntrada(tituloPainel, 0f);
        VisualMenu.animarEntrada(textoPainel, 0.12f);
        VisualMenu.animarEntrada(avancar, 0.22f);
        VisualMenu.animarEntrada(voltarLabel, 0.22f);
        Gdx.input.setInputProcessor(stage);
        audio.iniciarMusicaMenu();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        if (encerrando) return;
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) avancar();
        else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) voltar();
    }

    @Override
    public void redimensionar(int largura, int altura) {
        if (stage != null) stage.getViewport().update(largura, altura, true);
    }

    @Override
    public void esconder() {
        if (stage != null) stage.getRoot().setVisible(false);
    }

    @Override
    public void destruir() {
        if (stage == null) return;
        if (Gdx.input.getInputProcessor() == stage) Gdx.input.setInputProcessor(null);
        stage.dispose();
        stage = null;
    }

    private Label criarAcao(String texto, float x, float y, Runnable acao) {
        Label label = new Label(texto, skin);
        label.setAlignment(Align.center);
        label.setBounds(x, y, 200f, 44f);
        label.setColor(Color.LIGHT_GRAY);
        label.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float px, float py, int pointer, Actor fromActor) {
                label.setColor(Color.WHITE);
            }

            @Override
            public void exit(InputEvent event, float px, float py, int pointer, Actor toActor) {
                label.setColor(Color.LIGHT_GRAY);
            }

            @Override
            public void clicked(InputEvent event, float px, float py) {
                acao.run();
            }
        });
        return label;
    }

    private void avancar() {
        if (encerrando) return;
        encerrando = true;
        audio.tocarSelecionarOpcao();
        continuar.executar();
    }

    private void voltar() {
        if (encerrando) return;
        encerrando = true;
        audio.tocarSelecionarOpcao();
        voltar.executar();
    }
}
