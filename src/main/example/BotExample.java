import dev.spoocy.jdaextensions.commands.manager.impl.DefaultCommandManager;
import dev.spoocy.jdaextensions.commands.tree.CommandTree;
import dev.spoocy.jdaextensions.core.BotBuilder;
import dev.spoocy.jdaextensions.core.BotConfig;
import dev.spoocy.jdaextensions.core.DiscordBot;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class BotExample extends DiscordBot<BotConfig> {

    public static void main(String[] args) {

        // create an instance of a custom BotConfig implementation that reads data from a json file
        ExtendedBotConfig botConfig = new ExtendedBotConfig(new File("config.json"));

        BotBuilder builder = new BotBuilder()
                .addActivity(() -> Activity.playing("Testing..."))
                .setAllIntents()
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
                                                        .executes(context -> {
                                                            context.reply("You executed the first subcommand!");
                                                        })
                                                )

                                                // Command: /test second
                                                .then(CommandTree.command("second", "Second subcommand")
                                                        .executes(context -> {

                                                            context.reply("What do you think about this bot? Please reply within 30 seconds.");

                                                            getInstance().getEventWaiter().waitFor(MessageReceivedEvent.class)
                                                                    .runIf(event -> event.getAuthor().getIdLong() == context.getUser().getIdLong())
                                                                    .timeoutAfter(30, TimeUnit.SECONDS)
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
                                .register(AnnotationCommandExample.class)     // annotation-based command are also supported
                                .build()

                )
                .addListener(new ListenerExample())             // supports both ListenerAdapter and annotation-based listeners
                ;

        // create the bot instance
        new BotExample(botConfig, builder);

    }

    private static BotExample INSTANCE;

    public static BotExample getInstance() {
        return INSTANCE;
    }

    public BotExample(@NotNull BotConfig config, @NotNull BotBuilder builder) {
        super(config, builder);
        INSTANCE = this;
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
