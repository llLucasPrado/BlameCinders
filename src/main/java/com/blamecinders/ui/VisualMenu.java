package com.blamecinders.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.blamecinders.util.GerenciadorTexturas;

import java.util.Objects;

public final class VisualMenu {

    public static final float LARGURA = 1280f;
    public static final float ALTURA = 720f;
    public static final String FUNDO_CEMITERIO = "UI/Fundos/cemiterio.png";
    public static final String FUNDO_CAVALEIRO = "UI/Fundos/cavaleiro-luar.png";
    public static final String FUNDO_BRASAS = "UI/Fundos/brasas.png";
    public static final String FUNDO_DRAGAO = "UI/Fundos/dragao.png";

    private VisualMenu() {
    }

    public static void adicionarFundo(Stage stage, String caminho, float escurecimento) {
        Objects.requireNonNull(stage, "stage");
        Texture textura = GerenciadorTexturas.getArquivo(caminho);
        Image fundo = new Image(new TextureRegionDrawable(new TextureRegion(textura)));
        fundo.setScaling(Scaling.fill);
        fundo.setFillParent(true);
        stage.addActor(fundo);

        Image overlay = criarRetangulo(
            0f,
            0f,
            LARGURA,
            ALTURA,
            new Color(0.015f, 0.025f, 0.04f, limitar(escurecimento))
        );
        stage.addActor(overlay);
    }

    public static Group criarPainel(float x, float y, float largura, float altura) {
        Group painel = new Group();
        painel.setBounds(x, y, largura, altura);

        Image fundo = criarRetangulo(
            0f,
            0f,
            largura,
            altura,
            new Color(0.07f, 0.09f, 0.12f, 0.78f)
        );
        painel.addActor(fundo);
        adicionarBorda(painel, largura, altura, new Color(0.82f, 0.86f, 0.92f, 0.22f));
        return painel;
    }

    public static Label criarTitulo(Skin skin, String texto, float y, float escala) {
        Label titulo = new Label(texto, skin);
        titulo.setAlignment(Align.center);
        titulo.setFontScale(escala);
        titulo.setSize(LARGURA, 90f);
        titulo.setPosition(0f, y);
        titulo.setColor(new Color(0.94f, 0.95f, 0.97f, 1f));
        return titulo;
    }

    public static Image criarRetangulo(
        float x,
        float y,
        float largura,
        float altura,
        Color cor
    ) {
        Image imagem = new Image(new TextureRegionDrawable(
            new TextureRegion(GerenciadorTexturas.getSolid(Color.WHITE))
        ));
        imagem.setBounds(x, y, largura, altura);
        imagem.setColor(cor);
        return imagem;
    }

    public static void adicionarBorda(
        Group grupo,
        float largura,
        float altura,
        Color cor
    ) {
        grupo.addActor(criarRetangulo(0f, 0f, largura, 1f, cor));
        grupo.addActor(criarRetangulo(0f, altura - 1f, largura, 1f, cor));
        grupo.addActor(criarRetangulo(0f, 0f, 1f, altura, cor));
        grupo.addActor(criarRetangulo(largura - 1f, 0f, 1f, altura, cor));
    }

    public static void animarEntrada(Actor actor, float atraso) {
        actor.getColor().a = 0f;
        actor.setY(actor.getY() - 10f);
        actor.addAction(Actions.sequence(
            Actions.delay(atraso),
            Actions.parallel(
                Actions.fadeIn(0.55f, Interpolation.fade),
                Actions.moveBy(0f, 10f, 0.55f, Interpolation.smooth)
            )
        ));
    }

    private static float limitar(float valor) {
        return Math.max(0f, Math.min(1f, valor));
    }
}
