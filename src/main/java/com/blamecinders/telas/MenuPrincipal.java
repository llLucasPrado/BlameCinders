package com.blamecinders.telas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.blamecinders.audio.GerenciadorAudio;
import com.blamecinders.ui.CartaoMenu;
import com.blamecinders.ui.VisualMenu;

import java.util.Objects;
import java.util.function.BooleanSupplier;

public final class MenuPrincipal implements Tela {

    private static final float DURACAO_FADE = 0.9f;

    private final AcaoTela acaoNovoJogo;
    private final AcaoTela acaoContinuar;
    private final AcaoTela acaoOpcoes;
    private final AcaoTela acaoCreditos;
    private final BooleanSupplier partidaSalvaDisponivel;
    private final GerenciadorAudio audio;
    private final Skin skin;

    private Stage stage;
    private ShapeRenderer fadeRenderer;
    private CartaoMenu[] opcoes;
    private Label aviso;
    private int opcaoSelecionada;
    private float tempoFade;
    private boolean acaoEmExecucao;

    public MenuPrincipal(
        AcaoTela acaoNovoJogo,
        AcaoTela acaoContinuar,
        AcaoTela acaoOpcoes,
        AcaoTela acaoCreditos,
        BooleanSupplier partidaSalvaDisponivel,
        GerenciadorAudio audio,
        Skin skin
    ) {
        this.acaoNovoJogo = Objects.requireNonNull(acaoNovoJogo, "acaoNovoJogo");
        this.acaoContinuar = Objects.requireNonNull(acaoContinuar, "acaoContinuar");
        this.acaoOpcoes = Objects.requireNonNull(acaoOpcoes, "acaoOpcoes");
        this.acaoCreditos = Objects.requireNonNull(acaoCreditos, "acaoCreditos");
        this.partidaSalvaDisponivel = Objects.requireNonNull(
            partidaSalvaDisponivel,
            "partidaSalvaDisponivel"
        );
        this.audio = Objects.requireNonNull(audio, "audio");
        this.skin = Objects.requireNonNull(skin, "skin");
    }

    @Override
    public void mostrar() {
        stage = new Stage(new FitViewport(VisualMenu.LARGURA, VisualMenu.ALTURA));
        VisualMenu.adicionarFundo(stage, VisualMenu.FUNDO_CEMITERIO, 0.34f);

        Group painelTitulo = VisualMenu.criarPainel(330f, 590f, 620f, 86f);
        Label titulo = new Label("B L A M E   C I N D E R S", skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(1.55f);
        titulo.setFillParent(true);
        painelTitulo.addActor(titulo);
        stage.addActor(painelTitulo);

        opcoes = new CartaoMenu[5];
        opcoes[0] = criarCartao(
            "NOVO JOGO", "UI/Menu/novo-jogo.jpeg", 220f, 170f, 280f, 365f, 0
        );
        opcoes[1] = criarCartao(
            "CONTINUAR", "UI/Menu/continuar.jpeg", 520f, 455f, 540f, 80f, 1
        );
        opcoes[2] = criarCartao(
            "OPÇÕES", "UI/Menu/opcoes.jpeg", 520f, 360f, 540f, 80f, 2
        );
        opcoes[3] = criarCartao(
            "CRÉDITOS", "UI/Fundos/brasas.png", 520f, 265f, 540f, 80f, 3
        );
        opcoes[4] = criarCartao(
            "SAIR", "UI/Menu/sair.jpeg", 520f, 170f, 540f, 80f, 4
        );

        for (CartaoMenu opcao : opcoes) stage.addActor(opcao);
        if (!partidaSalvaDisponivel.getAsBoolean()) opcoes[1].setDisponivel(false);

        aviso = new Label("", skin);
        aviso.setColor(Color.LIGHT_GRAY);
        aviso.setAlignment(Align.center);
        aviso.setBounds(220f, 96f, 840f, 44f);
        stage.addActor(aviso);

        atualizarSelecao(false);
        VisualMenu.animarEntrada(painelTitulo, 0f);
        for (int i = 0; i < opcoes.length; i++) {
            VisualMenu.animarEntrada(opcoes[i], 0.08f + i * 0.05f);
        }
        fadeRenderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(stage);
        audio.iniciarMusicaMenu();
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        if (tempoFade < DURACAO_FADE) {
            tempoFade = Math.min(tempoFade + delta, DURACAO_FADE);
            desenharFade(1f - tempoFade / DURACAO_FADE);
        }
        if (acaoEmExecucao) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)
            || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selecionar((opcaoSelecionada + 1) % opcoes.length, true);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)
            || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selecionar((opcaoSelecionada - 1 + opcoes.length) % opcoes.length, true);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            executarOpcao();
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
        if (stage != null) {
            if (Gdx.input.getInputProcessor() == stage) Gdx.input.setInputProcessor(null);
            stage.dispose();
            stage = null;
        }
        if (fadeRenderer != null) {
            fadeRenderer.dispose();
            fadeRenderer = null;
        }
    }

    private CartaoMenu criarCartao(
        String texto,
        String imagem,
        float x,
        float y,
        float largura,
        float altura,
        int indice
    ) {
        CartaoMenu cartao = new CartaoMenu(
            skin,
            texto,
            imagem,
            largura,
            altura,
            () -> selecionar(indice, true),
            () -> {
                selecionar(indice, false);
                executarOpcao();
            }
        );
        cartao.setPosition(x, y);
        return cartao;
    }

    private void selecionar(int indice, boolean tocarSom) {
        if (acaoEmExecucao || opcaoSelecionada == indice) return;
        opcaoSelecionada = indice;
        aviso.setText("");
        atualizarSelecao(true);
        if (tocarSom) audio.tocarTrocaOpcao();
    }

    private void atualizarSelecao(boolean animar) {
        for (int i = 0; i < opcoes.length; i++) {
            opcoes[i].setSelecionado(i == opcaoSelecionada, animar);
        }
    }

    private void executarOpcao() {
        if (acaoEmExecucao) return;
        audio.tocarSelecionarOpcao();
        switch (opcaoSelecionada) {
            case 0:
                acaoEmExecucao = true;
                acaoNovoJogo.executar();
                break;
            case 1:
                if (!partidaSalvaDisponivel.getAsBoolean()) {
                    aviso.setText("NÃO HÁ PARTIDA SALVA DISPONÍVEL");
                    return;
                }
                acaoEmExecucao = true;
                acaoContinuar.executar();
                break;
            case 2:
                acaoEmExecucao = true;
                acaoOpcoes.executar();
                break;
            case 3:
                acaoEmExecucao = true;
                acaoCreditos.executar();
                break;
            case 4:
                acaoEmExecucao = true;
                Gdx.app.exit();
                break;
            default:
                throw new IllegalStateException("Opção de menu desconhecida.");
        }
    }

    private void desenharFade(float alpha) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        fadeRenderer.setProjectionMatrix(stage.getCamera().combined);
        fadeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        fadeRenderer.setColor(0f, 0f, 0f, alpha);
        fadeRenderer.rect(0f, 0f, VisualMenu.LARGURA, VisualMenu.ALTURA);
        fadeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
