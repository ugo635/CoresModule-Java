package com.me.coresmodule.features.bot;

import com.me.coresmodule.utils.FilesHandler;
import com.me.coresmodule.utils.Helper;
import com.me.coresmodule.utils.ScreenshotUtils;
import com.me.coresmodule.utils.TextHelper;
import com.me.coresmodule.utils.chat.Chat;
import com.me.coresmodule.utils.events.Register;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageActivity;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.FileUpload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.FileHandler;

public class Bot extends ListenerAdapter {
    public static JDA jda;
    public static Thread thread;
    public static List<CommandData> commands;
    public static Bot instance = null;
    public static HashMap<String, Boolean> trackedMessages;
    public String uuid;

    static {
        commands = new ArrayList<>();

        commands.add(Commands.slash("disconnect", "Disconnect from the bot."));
        commands.add(Commands.slash("takeScreenshot", "Take a screenshot."));

        commands.add(
                Commands.slash("say", "Makes the player send the message passed as argument.")
                        .addOption(OptionType.STRING, "message", "The message to send.", true)
        );

        commands.add(
                Commands.slash("command", "Makes the player execute the command passed as argument.")
                        .addOption(OptionType.STRING, "command", "The command to send.", true)
        );

        commands.add(
                Commands.slash("startMessageTracking", "Sends all messages sent in the chat in this channel or specified one in the argument.")
                    .addOption(OptionType.CHANNEL, "channel", "The channel to send the messages in.", false)
                    .addOption(OptionType.BOOLEAN, "pretty-display", "display the messages as an image with formatting.", true)
        );

        commands.add(
                Commands.slash("stopMessageTracking", "Stops the messages tracking.")
        );
    }

    public Bot() throws IOException {
        this.uuid = UUID.randomUUID().toString();
        instance = this;
        String token = FilesHandler.getContent("bot/token.txt").trim();
        if (token.isEmpty()) return;

        thread = new Thread(() -> {
            try {
                jda = JDABuilder.createDefault(token)
                        .addEventListeners(this)
                        .build()
                        .awaitReady();

                jda.updateCommands()
                        .addCommands(commands)
                        .queue();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

        thread.start();

    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent command) {

        MessageChannelUnion channel = command.getInteraction().getChannel();
        //command.getInteraction().getGuild().getTextChannelsByName("", true);

        switch (command.getName()) {
            case "disconnect" -> this.disconnect(command);
            case "takeScreenshot" -> this.takeScreenshot(command);
            case "say" -> say( command.getOption("message").getAsString());
            case "command" -> command( command.getOption("command").getAsString());
            case "startMessageTracking" -> startMessageTracking(
                command,
                command.getOption("channel", channel.asTextChannel(), option -> option.getAsChannel().asTextChannel()),
                command.getOption("pretty-display", false, OptionMapping::getAsBoolean)
            );
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Bot is ready!");

        try {
            event.getJDA().updateCommands().queue(commands -> {
                System.out.println("Synced " + commands.size() + " commands:");

                for (Command cmd : commands) {
                    System.out.println("    -" + cmd.getName());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect(SlashCommandInteractionEvent interaction) {
        interaction.reply("Disconnecting...").queue();
        thread.interrupt();
        instance = null;
    }

    public void takeScreenshot(SlashCommandInteractionEvent interaction) {
        long timeBefore = System.currentTimeMillis();
        BufferedImage screenshot = ScreenshotUtils.takeScreenshotWithReturn();
        double time = (System.currentTimeMillis() - timeBefore) * 1000;

        if (screenshot == null) {
            interaction.reply("Failled to take screenshot").queue();
            return;
        }

        // Creates the InputStream
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(screenshot,"png", outputStream);
            InputStream image = new ByteArrayInputStream(outputStream.toByteArray());

            FileUpload file = FileUpload.fromStreamSupplier("screenshot.png", () -> image);
            interaction
                    .reply("Screenshot taken in %.2f seconds!".formatted(time))
                    .addFiles(file)
                    .queue();
        } catch (IOException e) {
            print(e.getMessage());
        }

    }

    public void say(String message) {
        Chat.say(message);
    }

    public void command(String command) {
        Chat.command(command);
    }

    public void startMessageTracking(SlashCommandInteractionEvent interaction, TextChannel channel, boolean pretty) {
        interaction.reply("Started tracking messages").queue();
        trackedMessages.replace(uuid, true);

        if (!pretty) {
            // TODO: Add this.
        } else {
            Register.onChatMessage(message -> {
                if (isTrackingMessages()) channel.sendMessage(TextHelper.getUnFormattedString(message)).queue();
            });
        }
    }

    public void stopMessageTracking(SlashCommandInteractionEvent interaction) {
        interaction.reply("Stopped tracking messages").queue();
        trackedMessages.replace(uuid, true);
    }

    public static void register() {
        try {
            FilesHandler.createNewFolder("bot");
            FilesHandler.createFile("bot/bot.log");
            FilesHandler.createFile("bot/token.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File targetDir = new File("config/coresmodule/bot");
        if (!targetDir.exists()) targetDir.mkdirs();


        Register.command("setBotToken", args -> {
            try {
                FilesHandler.writeToFile("bot/token.txt", args[0]);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Chat.chat("§6[Cm] §aBot token set successfully!");
        });

        Register.command("connectToBot", args -> {
            try {
                instance = new Bot();
                Chat.chat("§a[Cm] Connected to bot successfully!");
            } catch (IOException e) {
                Chat.chat("§c[Cm] Failed to connect to bot!");
            }
        });

        Register.command("disconnectFromBot", args -> {
            if (instance == null) {
                Chat.chat("§c[Cm] Not connected to any bot!");
                return;
            }

            thread.interrupt();
            instance = null;
            Chat.chat("§a[Cm] Disconnected from bot.");
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (thread == null) return;
            if (thread.isAlive()) {
                Helper.print("Game closing, stopping bot...");
                thread.interrupt();
            }
        }));
    }

    public static void print(String message) {
        try {
            FilesHandler.appendToFile("bot/bot.log", message);
        } catch (IOException e) {
            Helper.printErr(Arrays.toString(e.getStackTrace()));
        }
    }

    public boolean isTrackingMessages() {
        return trackedMessages.containsKey(uuid) && trackedMessages.get(uuid);
    }
}
