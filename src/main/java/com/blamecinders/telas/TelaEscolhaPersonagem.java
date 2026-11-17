package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.combate.Personagem;
import com.blamecinders.ui.CartaoMenu;
import com.blamecinders.ui.VisualMenu;
import com.blamecinders.util.GerenciadorTexturas;

import java.util.Objects;
import java.util.function.Consumer;

public final class TelaEscolhaPersonagem implements Tela {

    private final Consumer<Personagem> aoEscolher;
    private final AcaoTela aoVoltar;
    private final GerenciadorAudio audio;
    private final Skin skin;

    private Stage stage;
    private CartaoMenu[] opcoes;
    private Label descricao;
    private int selecionado;
    private boolean acaoEmExecucao;

    public TelaEscolhaPersonagem(
        Consumer<Personagem> aoEscolher,
        AcaoTela aoVoltar,
        GerenciadorAudio audio,
        Skin skin
    ) {
        this.aoEscolher = Objects.requireNonNull(aoEscolher, "aoEscolher");
        this.aoVoltar = Objects.requireNonNull(aoVoltar, "aoVoltar");
        this.audio = Objects.requireNonNull(audio, "audio");
        this.skin = Objects.requireNonNull(skin, "skin");
    }

    @Override
    public void mostrar() {
        stage = new Stage(new FitViewport(VisualMenu.LARGURA, VisualMenu.ALTURA));
        VisualMenu.adicionarFundo(stage, VisualMenu.FUNDO_CEMITERIO, 0.46f);

        Group tituloPainel = VisualMenu.criarPainel(340f, 610f, 600f, 70f);
        Label titulo = new Label("ESCOLHA SEU ARQUÉTIPO", skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(1.55f);
        titulo.setFillParent(true);
        tituloPainel.addActor(titulo);
        stage.addActor(tituloPainel);

        Personagem[] personagens = Personagem.values();
        opcoes = new CartaoMenu[personagens.length];
        for (int i = 0; i < personagens.length; i++) {
            final int indice = i;
            CartaoMenu cartao = new CartaoMenu(
                skin,
                "",
                GerenciadorTexturas.get(personagens[i].getIdentificadorVisual()),
                240f,
                340f,
                () -> selecionar(indice),
                () -> {
                    selecionar(indice);
                    confirmar();
                }
            );
            cartao.setPosition(100f + i * 270f, 225f);
            opcoes[i] = cartao;
            stage.addActor(cartao);
            VisualMenu.animarEntrada(cartao, 0.07f + i * 0.06f);
        }

        Group descricaoPainel = VisualMenu.criarPainel(200f, 82f, 880f, 104f);
        descricao = new Label("", skin);
        descricao.setAlignment(Align.center);
        descricao.setWrap(true);
        descricao.setBounds(28f, 12f, 824f, 80f);
        descricaoPainel.addActor(descricao);
        stage.addActor(descricaoPainel);

        Label dica = new Label("ENTER CONFIRMA    •    ESC VOLTA", skin);
        dica.setAlignment(Align.center);
        dica.setColor(Color.LIGHT_GRAY);
        dica.setBounds(390f, 26f, 500f, 32f);
        stage.addActor(dica);

        atualizarSelecao(false);
        VisualMenu.animarEntrada(tituloPainel, 0f);
        VisualMenu.animarEntrada(descricaoPainel, 0.25f);
        Gdx.input.setInputProcessor(stage);
        audio.iniciarMusicaMenu();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        if (acaoEmExecucao) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)
            || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selecionar((selecionado + 1) % opcoes.length);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)
            || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selecionar((selecionado - 1 + opcoes.length) % opcoes.length);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            confirmar();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            acaoEmExecucao = true;
            audio.tocarSelecionarOpcao();
            aoVoltar.executar();
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

    private void selecionar(int indice) {
        if (acaoEmExecucao || selecionado == indice) return;
        selecionado = indice;
        atualizarSelecao(true);
        audio.tocarTrocaOpcao();
    }

    private void atualizarSelecao(boolean animar) {
        for (int i = 0; i < opcoes.length; i++) {
            opcoes[i].setSelecionado(i == selecionado, animar);
        }

        Personagem atual = Personagem.values()[selecionado];
        descricao.setText(atual.getDescricao().toUpperCase());
    }

    private void confirmar() {
        if (acaoEmExecucao) return;
        acaoEmExecucao = true;
        audio.tocarSelecionarOpcao();
        aoEscolher.accept(Personagem.values()[selecionado]);
    }
}
