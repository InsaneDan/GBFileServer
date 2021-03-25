package ru.isakov.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;


public class WatchFoldersService {

    private static final Logger logger = LoggerFactory.getLogger(WatchFoldersService.class);

    private static Map<WatchKey, Path> keyPathMap = new HashMap<>();

    public static void main (String[] args) throws Exception {

        String watchDir = "d:/testDir";
        logger.info("Отслеживаемая папка: {}", watchDir);
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            registerDir(Paths.get(watchDir), watchService); // зарегистрировать папку
            startListening(watchService); // начать прослушивание изменений
        }
    }

    private static void registerDir (Path path, WatchService watchService) throws IOException {
        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            logger.trace("Файл: " + path);
            return;
        }
        logger.trace("Директория: " + path);
        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.OVERFLOW);
        keyPathMap.put(key, path);
        for (File f : path.toFile().listFiles()) {
            registerDir(f.toPath(), watchService); // рекурсивно добавляем файлы и подпапки
        }
    }

    private static void startListening (WatchService watchService) throws Exception {
        while (true) {
            WatchKey queuedKey = watchService.take();
            for (WatchEvent<?> watchEvent : queuedKey.pollEvents()) {
                logger.info("Новое событие => тип: {}, count={}, context={}}, Context type={}",
                    watchEvent.kind(), watchEvent.count(), watchEvent.context(), ((Path) watchEvent.context()).getClass());

                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    Path path = (Path) watchEvent.context(); // путь из контекста
                    Path parentPath = keyPathMap.get(queuedKey); // родительский путь из очереди событий
                    path = parentPath.resolve(path); // полный путь

                    registerDir(path, watchService); // регистрируем полный путь (файла или папки)

                    // передать команду
                }
            }

            if (!queuedKey.reset()) {
                keyPathMap.remove(queuedKey);
            }

            if (keyPathMap.isEmpty()) {
                break;
            }
        }
    }
}