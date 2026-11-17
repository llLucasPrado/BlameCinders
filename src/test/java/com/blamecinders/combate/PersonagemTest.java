package com.blamecinders.combate;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonagemTest {

    @Test
    void mantemAquillaComoPadraoCompativelComPartidasAntigas() {
        assertSame(Personagem.AQUILLA, Personagem.PADRAO);
        assertEquals(50, Personagem.PADRAO.getVidaInicial());
    }

    @Test
    void expoeSomenteArquetiposNaTelaDeSelecao() {
        assertEquals("Arqueira", Personagem.VIEYRA.getArquetipo());
        assertEquals("Assassino", Personagem.TAIGA.getArquetipo());
        assertEquals("Anão", Personagem.NAZARET.getArquetipo());
        assertEquals("Cavaleiro", Personagem.AQUILLA.getArquetipo());
    }

    @Test
    void distribuiVidaConformeRiscoDeCadaArquetipo() {
        assertEquals(32, Personagem.TAIGA.getVidaInicial());
        assertEquals(38, Personagem.VIEYRA.getVidaInicial());
        assertEquals(50, Personagem.AQUILLA.getVidaInicial());
        assertEquals(60, Personagem.NAZARET.getVidaInicial());
    }

    @Test
    void defineHierarquiaDeFurtividadePorArquetipo() {
        assertTrue(
            Personagem.TAIGA.getChanceBaseFurtividade()
                > Personagem.VIEYRA.getChanceBaseFurtividade()
        );
        assertTrue(
            Personagem.VIEYRA.getChanceBaseFurtividade()
                > Personagem.AQUILLA.getChanceBaseFurtividade()
        );
        assertTrue(Personagem.TAIGA.podeUsarFurtividade());
        assertTrue(Personagem.VIEYRA.podeUsarFurtividade());
        assertTrue(Personagem.AQUILLA.podeUsarFurtividade());
        assertFalse(Personagem.NAZARET.podeUsarFurtividade());
        assertEquals(0, Personagem.NAZARET.getChanceBaseFurtividade());
    }

    @Test
    void usaFrasesDeEstiloSemExporVidaOuFurtividade() {
        for (Personagem personagem : Personagem.values()) {
            String descricao = personagem.getDescricao().toLowerCase(Locale.ROOT);
            assertFalse(descricao.contains("vida"));
            assertFalse(descricao.contains("%"));
            assertFalse(descricao.matches(".*\\d.*"));
        }
    }

    @Test
    void usaIdentificadorVisualEstavelSemPrefixoDuplicado() {
        assertEquals("PERSONAGEM_VIEYRA", Personagem.VIEYRA.getIdentificadorVisual());
        assertEquals("PERSONAGEM_TAIGA", Personagem.TAIGA.getIdentificadorVisual());
        assertEquals("PERSONAGEM_NAZARET", Personagem.NAZARET.getIdentificadorVisual());
        assertEquals("PERSONAGEM_AQUILLA", Personagem.AQUILLA.getIdentificadorVisual());
    }

    @Test
    void migraIdentificadoresAntigosEUsaPadraoParaAusentes() {
        assertSame(Personagem.VIEYRA, Personagem.porId("PERSONAGEM_Vieyra"));
        assertSame(Personagem.TAIGA, Personagem.porId("PERSONAGEM_PERSONAGEM_Taiga"));
        assertSame(Personagem.NAZARET, Personagem.porId("nazaret"));
        assertSame(Personagem.PADRAO, Personagem.porId(null));
        assertSame(Personagem.PADRAO, Personagem.porId("desconhecido"));
    }

    @Test
    void possuiRetratoComNomePortatilParaCadaPersonagem() {
        for (Personagem personagem : Personagem.values()) {
            String recurso = "Cartas/Frente/Jogador/"
                + personagem.name().toLowerCase(Locale.ROOT) + ".png";
            assertNotNull(
                PersonagemTest.class.getClassLoader().getResource(recurso),
                recurso
            );
        }
    }
}
