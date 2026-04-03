package dev.spoocy;

import dev.spoocy.jdaextensions.commands.arguments.Arguments;
import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import dev.spoocy.jdaextensions.commands.manager.impl.DefaultCommandManager;
import dev.spoocy.jdaextensions.commands.tree.CommandTree;
import dev.spoocy.jdaextensions.core.BotSettings;
import dev.spoocy.jdaextensions.core.SingleShardDiscordBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BotExample extends SingleShardDiscordBot {

    public static void main(String[] args) {

        BotSettings settings = BotSettings.builder()
                .setActivity(i -> Activity.playing("Testing..."))
                .setIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .setAutoLogin(false) // disable auto-login, have to manually call login() after creating the bot instance
                .setCommandManager(
                        DefaultCommandManager.builder()
                                // Add commands using the CommandTree builder
                                .register(
                                        // Command: /ping
                                        new CommandTree("ping", "Replies with Pong!")
                                                .executes(context -> {
                                                    context.reply("Pong!");
                                                })
                                                .build()
                                )
                                .register(
                                        new CommandTree("test", "Second command")

                                                // Command: /test first
                                                .then(CommandTree.command("first", "First subcommand")
                                                        .arg(
                                                                Arguments.string("input", "Some input", false, false)
                                                                        .choice("Option 1", "option1")
                                                                        .choice("Option 2", "option2")
                                                        )
                                                        .executes(context -> {

                                                            ProvidedArgument inputArg = context.getArgument("input");
                                                            String input = inputArg != null ? inputArg.getAsString() : "No input provided";

                                                            context.reply("You executed the first subcommand with input: " + input);
                                                        })
                                                )

                                                // Command: /test second
                                                .then(CommandTree.command("second", "Second subcommand")
                                                        .executes(context -> {

                                                            context.reply("What do you think about this bot? Please reply within 30 seconds.");

                                                            getInstance().getEventWaiter().waitFor(MessageReceivedEvent.class)
                                                                    .runIf(event -> event.getAuthor().getIdLong() == context.getUser().getIdLong())
                                                                    .timeoutAfter(Duration.ofSeconds(30))
                                                                    .runOnTimeout(() -> {
                                                                        context.reply("You did not reply in time!");
                                                                    })
                                                                    .run(event -> {
                                                                        context.reply("You replied with: " + event.getMessage().getContentDisplay());
                                                                    })
                                                                    .build();


                                                        })
                                                )
                                                .build()
                                )
                                .registerCommand(AnnotationCommandExample.class)     // annotation-based command are also supported
                                .build()

                ).build()
                ;

        // create the bot instance
        new BotExample(settings);

    }

    private static BotExample INSTANCE;

    public static BotExample getInstance() {
        return INSTANCE;
    }

    public BotExample(@NotNull BotSettings settings) {
        super(settings);
        INSTANCE = this;
        this.login();   // manually log in the bot since auto-login is disabled in the settings
    }

    @Override
    protected void configure(@NotNull JDABuilder builder) {
        // modify the JDABuilder before building the JDA instance
        // (e.g. add additional event listeners, set member cache policy, etc.)

        builder.addEventListeners(new ListenerExample()); // supports both ListenerAdapter and annotation-based listeners
    }

    @Override
    protected void onStart() {
        // executed once the bot starts
    }

    @Override
    protected void onReady() {
        // executed once all shards are ready
    }

    @Override
    protected void onShutdown() {
        // executed once the bot is shutting down
    }

    @SubscribeEvent
    public void onReady(@NotNull ReadyEvent event) {
        // Bot instance will listen for events by default
    }

}
