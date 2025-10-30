package com.me.coresmodule.features.bot;

import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.ScreenshotUtils;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import net.minecraft.client.MinecraftClient;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.nio.file.*;

public class Bot {

    private static Process botProcess = null;

    private static Thread thread = new Thread(() -> {
        while (true) {
            try {
                String content = FilesHandler.getContent("bot/sharedForMod.json");
                content = content.trim();
                if (!content.isEmpty() && !content.equals("{}")) {
                    JSONObject json = new JSONObject(content);
                    String request = json.getString("request");

                    switch (request) {
                        case "screenshot" -> {
                            MinecraftClient.getInstance().execute(() -> {
                                ScreenshotUtils.takeScreenshotAsync(path -> {
                                    if (path.isEmpty()) return;
                                    HashMap<String, String> response = new HashMap<>();
                                    response.put("timestamp", String.valueOf(System.currentTimeMillis()));
                                    response.put("answer", path);
                                    try {
                                        FilesHandler.writeToFile("bot/sharedForBot.json", new JSONObject(response).toString(4));
                                        clearRequest();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            });
                        }
                        case "say" -> {
                            Chat.say(json.getString("content"));
                            HashMap<String, String> response = new HashMap<>();
                            response.put("timestamp", String.valueOf(System.currentTimeMillis()));
                            response.put("answer", "done");
                            try {
                                FilesHandler.writeToFile("bot/sharedForBot.json", new JSONObject(response).toString(4));
                                clearRequest();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        case "command" -> {
                            Chat.command(json.getString("content"));
                            HashMap<String, String> response = new HashMap<>();
                            response.put("timestamp", String.valueOf(System.currentTimeMillis()));
                            response.put("answer", "done");
                            try {
                                FilesHandler.writeToFile("bot/sharedForBot.json", new JSONObject(response).toString(4));
                                clearRequest();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

    public static void register() {
        try {
            FilesHandler.createNewFolder("bot");
            FilesHandler.createFile("bot/bot.log");
            FilesHandler.createFile("bot/stop.txt");
            FilesHandler.createFile("bot/token.txt");
            FilesHandler.createFile("bot/sharedForMod.json");
            FilesHandler.createFile("bot/sharedForBot.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            String[] resources = {"javaConnection.py", "JsonCreator.py"};

            File targetDir = new File("config/coresmodule/bot");
            if (!targetDir.exists()) targetDir.mkdirs();

            for (String resourceName : resources) {
                InputStream is = Bot.class.getResourceAsStream("/" + resourceName);
                if (is == null) {
                    System.out.println(resourceName + " not found in resources!");
                    continue;
                }

                File targetFile = new File(targetDir, resourceName);
                Files.copy(is, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                is.close();

                System.out.println(resourceName + " copied to " + targetFile.getAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        Register.command("setBotToken", args -> {
            try {
                FilesHandler.writeToFile("bot/token.txt", args[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Chat.chat("§6[Cm] §aBot token set successfully!");
        });

        Register.command("connectToBot", args -> {
            if (botProcess != null && botProcess.isAlive()) {
                Chat.chat("§6[Cm] §cBot is already running.");
                return;
            }
            try {
                FilesHandler.writeToFile("bot/stop.txt", "false");

                String pythonExe = "python";
                String scriptPath = "config/coresmodule/bot/javaConnection.py";

                ProcessBuilder builder = new ProcessBuilder(pythonExe, scriptPath);
                builder.redirectErrorStream(true);
                builder.redirectOutput(new File("config/coresmodule/bot/bot.log"));
                Process process = builder.start();
                thread.start();

                botProcess = process;

                Chat.chat("§6[Cm] §aConnected to bot successfully!");

            } catch (IOException e) {
                e.printStackTrace();
                Chat.chat("§6[Cm] §cError connecting bot. Check python path: " + e.getMessage());
            }
        });

        Register.command("disconnectFromBot", args -> {
            if (botProcess == null || !botProcess.isAlive()) {
                Chat.chat("§6[Cm] §cBot is not running.");
                return;
            }
            try {
                FilesHandler.writeToFile("bot/stop.txt", "true");
                Chat.chat("§6[Cm] §eSent shutdown signal to Python. Waiting for termination...");

                new Thread(() -> {
                    try {
                        if (botProcess.waitFor(5, TimeUnit.SECONDS)) {
                            thread.interrupt();
                            Chat.chat("§6[Cm] §aBot shut down gracefully.");
                        } else {
                            botProcess.destroyForcibly();
                            thread.interrupt();
                            Chat.chat("§6[Cm] §eBot was forcefully terminated.");
                        }

                        botProcess = null;

                    } catch (Exception e) {
                        e.printStackTrace();
                        Chat.chat("§6[Cm] §cError while disconnecting from bot: " + e.getMessage());
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
                Chat.chat("§6[Cm] §cError while disconnecting from bot: " + e.getMessage());
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Game closing, stopping bot...");
            try {
                FilesHandler.writeToFile("bot/stop.txt", "true");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    public static void clearRequest() throws IOException {
        FilesHandler.writeToFile("bot/sharedForMod.json","{}");
    }

}