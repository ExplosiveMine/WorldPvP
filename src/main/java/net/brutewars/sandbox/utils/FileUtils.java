package net.brutewars.sandbox.utils;

import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;

public final class FileUtils {
    @SneakyThrows
    public static void deleteDirectory(File file) {
        if (!file.exists())
            return;

        Files.walkFileTree(file.toPath(), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @SneakyThrows
    public static void copyDirectory(File source, File destination, Predicate<String> filter) {
        if (source.isDirectory()) {
            destination.mkdirs();

            String[] list =  source.list();
            if (list == null)
                return;

            for (String s : list) {
                if (filter.test(s))
                    copyDirectory(new File(source, s), new File(destination, s), filter);
            }
        } else {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
        }
    }

}