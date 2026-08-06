package com.blamecinders.suporte;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/** Cria uma fonte sem textura/OpenGL para testes que apenas avançam Actions. */
public final class FonteTesteScene2D {

    private FonteTesteScene2D() {
    }

    public static BitmapFont criar() {
        return new BitmapFont(new BitmapFont.BitmapFontData(), new TextureRegion(), true);
    }
}
