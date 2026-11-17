package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.ui.CartaoMenu;
import com.blamecinders.ui.PopupAudio;
import com.blamecinders.ui.VisualMenu;

import java.util.Objects;

public final class TelaOpcoes implements Tela {

    private final AcaoTela voltar;
    private final GerenciadorAudio audio;
    private final Skin skin;

    private Stage stage;
    private PopupAudio popupAudio;
    private CartaoMenu[] opcoes;
    private int opcaoSelecionada;
    private boolean encerrando;

    public TelaOpcoes(AcaoTela voltar, GerenciadorAudio audio, Skin skin) {
        this.voltar = Objects.requireNonNull(voltar, "voltar");
        this.audio = Objects.requireNonNull(audio, "audio");
        this.skin = Objects.requireNonNull(skin, "skin");
    }

    @Override
    public void mostrar() {
        stage = new Stage(new FitViewport(VisualMenu.LARGURA, VisualMenu.ALTURA));
        VisualMenu.adicionarFundo(stage, VisualMenu.FUNDO_CEMITERIO, 0.48f);
        popupAudio = new PopupAudio(stage, skin, audio);

        Group tituloPainel = VisualMenu.criarPainel(390f, 560f, 500f, 86f);
        Label titulo = new Label("OPÇÕES", skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(1.9f);
        titulo.setFillParent(true);
        tituloPainel.addActor(titulo);
        stage.addActor(tituloPainel);

        opcoes = new CartaoMenu[2];
        opcoes[0] = criarOpcao("ÁUDIO", 390f, 350f, 0);
        opcoes[1] = criarOpcao("VOLTAR", 390f, 235f, 1);
        for (CartaoMenu opcao : opcoes) stage.addActor(opcao);

        atualizarSelecao(false);
        VisualMenu.animarEntrada(tituloPainel, 0f);
        VisualMenu.animarEntrada(opcoes[0], 0.1f);
        VisualMenu.animarEntrada(opcoes[1], 0.18f);
        Gdx.input.setInputProcessor(stage);
        audio.iniciarMusicaMenu();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        if (encerrando) return;

        if (popupAudio.estaAberto()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                audio.tocarSelecionarOpcao();
                popupAudio.fechar(null);
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selecionar((opcaoSelecionada + 1) % opcoes.length);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selecionar((opcaoSelecionada - 1 + opcoes.length) % opcoes.length);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executarOpcao();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            sair();
        }
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

    private CartaoMenu criarOpcao(String texto, float x, float y, int indice) {
        CartaoMenu cartao = new CartaoMenu(
            skin,
            texto,
            "UI/Menu/opcoes.jpeg",
            500f,
            86f,
            () -> selecionar(indice),
            () -> {
                selecionar(indice);
                executarOpcao();
            }
        );
        cartao.setPosition(x, y);
        return cartao;
    }

    private void selecionar(int indice) {
        if (encerrando || opcaoSelecionada == indice) return;
        opcaoSelecionada = indice;
        atualizarSelecao(true);
        audio.tocarTrocaOpcao();
    }

    private void atualizarSelecao(boolean animar) {
        for (int i = 0; i < opcoes.length; i++) {
            opcoes[i].setSelecionado(i == opcaoSelecionada, animar);
        }
    }

    private void executarOpcao() {
        audio.tocarSelecionarOpcao();
        if (opcaoSelecionada == 0) popupAudio.mostrar();
        else sair();
    }

    private void sair() {
        if (encerrando) return;
        encerrando = true;
        voltar.executar();
    }
}
