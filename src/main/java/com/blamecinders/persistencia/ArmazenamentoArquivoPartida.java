package com.blamecinders.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

final class ArmazenamentoArquivoPartida implements ArmazenamentoPartida {

    static final String DIRETORIO_JOGO = "BlameCinders";
    static final String DIRETORIO_SAVES = "saves";
    static final String NOME_ARQUIVO = "slot-001.json";
    static final String NOME_BACKUP = "slot-001.backup.json";

    private final Path arquivo;
    private final Path arquivoTemporario;
    private final Path backup;
    private final ArmazenamentoPartida armazenamentoLegado;

    ArmazenamentoArquivoPartida(ArmazenamentoPartida armazenamentoLegado) {
        this(caminhoPadrao(), armazenamentoLegado);
    }

    ArmazenamentoArquivoPartida(Path arquivo, ArmazenamentoPartida armazenamentoLegado) {
        this.arquivo = Objects.requireNonNull(arquivo, "arquivo").toAbsolutePath();
        this.arquivoTemporario = this.arquivo.resolveSibling(NOME_ARQUIVO + ".tmp");
        this.backup = this.arquivo.resolveSibling(NOME_BACKUP);
        this.armazenamentoLegado = Objects.requireNonNull(
            armazenamentoLegado,
            "armazenamentoLegado"
        );
    }

    @Override
    public String ler() {
        if (Files.isRegularFile(arquivo)) return lerArquivo(arquivo);

        String conteudoLegado = armazenamentoLegado.ler();
        if (conteudoLegado == null || conteudoLegado.trim().isEmpty()) return "";

        escrever(conteudoLegado);
        armazenamentoLegado.remover();
        return conteudoLegado;
    }

    @Override
    public String lerBackup() {
        return Files.isRegularFile(backup) ? lerArquivo(backup) : "";
    }

    @Override
    public void restaurarBackup() {
        if (!Files.isRegularFile(backup)) return;
        try {
            Files.copy(backup, arquivoTemporario, StandardCopyOption.REPLACE_EXISTING);
            moverTemporarioParaSave();
        } catch (IOException erro) {
            apagarSilenciosamente(arquivoTemporario);
            throw new IllegalStateException("Não foi possível restaurar o backup do save.", erro);
        }
    }

    @Override
    public void escrever(String conteudo) {
        Objects.requireNonNull(conteudo, "conteudo");
        try {
            Files.createDirectories(arquivo.getParent());
            Files.write(arquivoTemporario, conteudo.getBytes(StandardCharsets.UTF_8));

            if (Files.isRegularFile(arquivo)) {
                Files.copy(arquivo, backup, StandardCopyOption.REPLACE_EXISTING);
            }

            moverTemporarioParaSave();
        } catch (IOException erro) {
            apagarSilenciosamente(arquivoTemporario);
            throw new IllegalStateException("Não foi possível gravar o arquivo de save.", erro);
        }
    }

    @Override
    public void remover() {
        try {
            Files.deleteIfExists(arquivo);
            Files.deleteIfExists(arquivoTemporario);
            Files.deleteIfExists(backup);
            armazenamentoLegado.remover();
        } catch (IOException erro) {
            throw new IllegalStateException("Não foi possível remover o save.", erro);
        }
    }

    Path getArquivo() {
        return arquivo;
    }

    private void moverTemporarioParaSave() throws IOException {
        try {
            Files.move(
                arquivoTemporario,
                arquivo,
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (AtomicMoveNotSupportedException erro) {
            Files.move(
                arquivoTemporario,
                arquivo,
                StandardCopyOption.REPLACE_EXISTING
            );
        }
    }

    private static String lerArquivo(Path caminho) {
        try {
            return new String(Files.readAllBytes(caminho), StandardCharsets.UTF_8);
        } catch (IOException erro) {
            throw new IllegalStateException("Não foi possível ler o arquivo de save.", erro);
        }
    }

    private static void apagarSilenciosamente(Path caminho) {
        try {
            Files.deleteIfExists(caminho);
        } catch (IOException ignorado) {
            // A falha original de escrita é mais relevante para quem chamou o método.
        }
    }

    private static Path caminhoPadrao() {
        return Paths.get(
            System.getProperty("user.home"),
            DIRETORIO_JOGO,
            DIRETORIO_SAVES,
            NOME_ARQUIVO
        );
    }
}
