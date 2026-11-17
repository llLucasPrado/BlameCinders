package com.blamecinders.persistencia;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArmazenamentoArquivoPartidaTest {

    @TempDir
    Path diretorioTemporario;

    @Test
    void gravaEmArquivoEGuardaVersaoAnteriorComoBackup() throws Exception {
        Path arquivo = caminhoSave();
        ArmazenamentoArquivoPartida armazenamento = novoArmazenamento(arquivo);

        armazenamento.escrever("primeiro");
        armazenamento.escrever("segundo");

        assertEquals("segundo", armazenamento.ler());
        assertEquals("primeiro", armazenamento.lerBackup());
        assertEquals(
            "segundo",
            new String(Files.readAllBytes(arquivo), StandardCharsets.UTF_8)
        );
        assertFalse(Files.exists(arquivo.resolveSibling("slot-001.json.tmp")));
    }

    @Test
    void migraSaveLegadoQuandoArquivoAindaNaoExiste() {
        Path arquivo = caminhoSave();
        ArmazenamentoMemoria legado = new ArmazenamentoMemoria("save antigo");
        ArmazenamentoArquivoPartida armazenamento = new ArmazenamentoArquivoPartida(
            arquivo,
            legado
        );

        assertEquals("save antigo", armazenamento.ler());
        assertTrue(Files.isRegularFile(arquivo));
        assertEquals("", legado.ler());
    }

    @Test
    void removeSaveBackupETemporario() throws Exception {
        Path arquivo = caminhoSave();
        ArmazenamentoArquivoPartida armazenamento = novoArmazenamento(arquivo);
        armazenamento.escrever("primeiro");
        armazenamento.escrever("segundo");
        Path temporario = arquivo.resolveSibling("slot-001.json.tmp");
        Files.write(temporario, "incompleto".getBytes(StandardCharsets.UTF_8));

        armazenamento.remover();

        assertFalse(Files.exists(arquivo));
        assertFalse(Files.exists(arquivo.resolveSibling("slot-001.backup.json")));
        assertFalse(Files.exists(temporario));
    }

    @Test
    void restauraBackupSemSubstituirACopiaBoa() throws Exception {
        Path arquivo = caminhoSave();
        ArmazenamentoArquivoPartida armazenamento = novoArmazenamento(arquivo);
        armazenamento.escrever("save válido");
        armazenamento.escrever("save mais novo");
        Files.write(arquivo, "corrompido".getBytes(StandardCharsets.UTF_8));

        armazenamento.restaurarBackup();

        assertEquals("save válido", armazenamento.ler());
        assertEquals("save válido", armazenamento.lerBackup());
    }

    private ArmazenamentoArquivoPartida novoArmazenamento(Path arquivo) {
        return new ArmazenamentoArquivoPartida(arquivo, new ArmazenamentoMemoria(""));
    }

    private Path caminhoSave() {
        return diretorioTemporario.resolve("saves").resolve("slot-001.json");
    }

    private static final class ArmazenamentoMemoria implements ArmazenamentoPartida {
        private String conteudo;

        private ArmazenamentoMemoria(String conteudo) {
            this.conteudo = conteudo;
        }

        @Override
        public String ler() {
            return conteudo;
        }

        @Override
        public void escrever(String conteudo) {
            this.conteudo = conteudo;
        }

        @Override
        public void remover() {
            conteudo = "";
        }
    }
}
