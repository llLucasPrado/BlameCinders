package com.blamecinders.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.blamecinders.util.GerenciadorTexturas;

/** Recursos e estilos compartilhados pela apresentação do jogo. */
public final class TemaJogo implements Disposable {

    public static final String FONTE_DARK_FANTASY = "Cinzel Decorative Bold";
    public static final String ARQUIVO_FONTE = "Fonts/CinzelDecorative-Bold.ttf";

    private final Skin skin;
    private final BitmapFont fonteCarta;

    private TemaJogo(Skin skin, BitmapFont fonteCarta) {
        this.skin = skin;
        this.fonteCarta = fonteCarta;
    }

    public static TemaJogo criar() {
        BitmapFont fonteInterface = criarFonte(20);
        BitmapFont fonteCarta = criarFonte(10);
        return new TemaJogo(criarSkin(fonteInterface), fonteCarta);
    }

    public Skin getSkin() {
        return skin;
    }

    public BitmapFont getFonteInterface() {
        return skin.getFont("default-font");
    }

    public BitmapFont getFonteCarta() {
        return fonteCarta;
    }

    @Override
    public void dispose() {
        skin.dispose();
        fonteCarta.dispose();
    }

    private static Skin criarSkin(BitmapFont fonte) {
        Skin skin = new Skin();
        skin.add("default-font", fonte);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = fonte;
        labelStyle.fontColor = Color.WHITE;
        skin.add("default", labelStyle);

        Window.WindowStyle windowStyle = new Window.WindowStyle();
        windowStyle.titleFont = fonte;
        windowStyle.titleFontColor = Color.WHITE;
        windowStyle.background = criarDrawableCor(new Color(0f, 0f, 0f, 0.85f));
        skin.add("default", windowStyle);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = fonte;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.GRAY;
        buttonStyle.up = criarDrawableCor(new Color(0.18f, 0.18f, 0.18f, 1f));
        buttonStyle.down = criarDrawableCor(new Color(0.10f, 0.10f, 0.10f, 1f));
        skin.add("default", buttonStyle);

        return skin;
    }

    private static BitmapFont criarFonte(int tamanho) {
        if (!Gdx.files.internal(ARQUIVO_FONTE).exists()) {
            BitmapFont fallback = new BitmapFont();
            fallback.getData().setScale(tamanho / 15f);
            return fallback;
        }

        FreeTypeFontGenerator gerador =
            new FreeTypeFontGenerator(Gdx.files.internal(ARQUIVO_FONTE));
        FreeTypeFontGenerator.FreeTypeFontParameter parametros =
            new FreeTypeFontGenerator.FreeTypeFontParameter();
        parametros.size = tamanho;
        parametros.color = Color.WHITE;

        BitmapFont fonte = gerador.generateFont(parametros);
        gerador.dispose();
        return fonte;
    }

    private static Drawable criarDrawableCor(Color cor) {
        return new TextureRegionDrawable(new TextureRegion(GerenciadorTexturas.getSolid(cor)));
    }
}
