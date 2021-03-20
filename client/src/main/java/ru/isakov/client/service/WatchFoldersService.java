package ru.isakov.client.service;

import java.io.File;
import java.io.IOException;

import java.nio.file.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class WatchFoldersService {

    private static Map<WatchKey, Path> keyPathMap = new HashMap<>();

    public static void main (String[] args) throws Exception {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            registerDir(Paths.get("d:/testDir"), watchService);
            startListening(watchService);
        }
    }

    private static void registerDir (Path path, WatchService watchService) throws
                        IOException {


        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }

        System.out.println("registering: " + path);


        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_MODIFY,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.OVERFLOW);
        keyPathMap.put(key, path);


        for (File f : path.toFile().listFiles()) {
            registerDir(f.toPath(), watchService);
        }
    }

    private static void startListening (WatchService watchService) throws Exception {
        while (true) {
            WatchKey queuedKey = watchService.take();
            for (WatchEvent<?> watchEvent : queuedKey.pollEvents()) {
                System.out.printf("Event... kind=%s, count=%d, context=%s Context type=%s%n",
                                    watchEvent.kind(),
                                    watchEvent.count(), watchEvent.context(), ((Path) watchEvent
                                                        .context()).getClass());

                //do something useful here

                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    //this is not a complete path
                    Path path = (Path) watchEvent.context();
                    //need to get parent path
                    Path parentPath = keyPathMap.get(queuedKey);
                    //get complete path
                    path = parentPath.resolve(path);

                    registerDir(path, watchService);
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