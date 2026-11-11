package com.jgmagalop.aidocanalyzer.documents.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${app.storage.location}") String location) {
        this.root = Path.of(location).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear la carpeta de almacenamiento: " + root, e);
        }
    }

    /** Guarda el archivo con el nombre indicado y devuelve la ruta relativa. */
    public String store(MultipartFile file, String filename) {
        Path target = resolveSafely(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el archivo " + filename, e);
        }
    }

    /** Devuelve el archivo para leerlo (lo usarán los lectores de la Fase 3). */
    public Resource load(String storagePath) {
        return new FileSystemResource(resolveSafely(storagePath));
    }

    /** Borra el archivo si existe. */
    public void delete(String storagePath) {
        try {
            Files.deleteIfExists(resolveSafely(storagePath));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo borrar el archivo " + storagePath, e);
        }
    }

    /** Evita que un nombre como "../../application.yml" salga de la carpeta. */
    private Path resolveSafely(String filename) {
        Path target = root.resolve(filename).normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("Ruta de archivo no permitida: " + filename);
        }
        return target;
    }
}