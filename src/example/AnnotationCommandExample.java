import dev.spoocy.jdaextensions.commands.annotations.Choice;
import dev.spoocy.jdaextensions.commands.annotations.Command;
import dev.spoocy.jdaextensions.commands.annotations.Permissions;
import dev.spoocy.jdaextensions.commands.arguments.Arguments;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.modals.Modal;
import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Command(
        name = "example",
        description = "An example command using annotations",
        context = {InteractionContextType.GUILD, InteractionContextType.PRIVATE_CHANNEL}
)
@Permissions.Default(Permission.MESSAGE_SEND)                                       // Command will only be shown to user with MESSAGE_SEND permission
public class AnnotationCommandExample {

    @Command.Default(ephemeral = false)                                             // Command: /example
    public static void executeRootCommand(@NotNull CommandContext context,

                                          @Arguments.User(
                                                  name = "user",
                                                  description = "some user",
                                                  required = false                  // Argument may be null because it's not required
                                          ) User user

    ) {

        context.reply("User: " + (user == null ? "None" : user.getAsTag())).queue();


    }


    @Command.Sub(name = "channel", description = "Some Subcommand", ephemeral = true)   // Command: /example channel
    @Choice.Text(argument = "some_text", name = "Text Choice One", value = "text_one")  // Choice for argument "some_text"
    @Choice.Text(argument = "some_text", name = "Text Choice Wwo", value = "text_two")
    @Permissions.Owner                                                                  // Only the bot owner(s), specified in the Bot Config, can use this command
    public static void executeSubCommand(@NotNull CommandContext context,

                                         @Arguments.Channel(
                                                 name = "channel",
                                                 description = "Some example text argument",
                                                 channelTypes = {ChannelType.TEXT},
                                                 required = true
                                         ) GuildChannelUnion channel,

                                         @Arguments.Text(
                                                 name = "some_text",
                                                 description = "Some example text argument",
                                                 minLength = 1,
                                                 maxLength = 100,
                                                 required = true
                                         ) String text

    ) {

        context.reply("Channel: " + (channel == null ? "None" : channel.getName()) +
                "\nText: " + (text == null ? "None" : text)).queue();
        channel.asTextChannel().sendMessage(text).queue();
    }

    @Command.Sub(name = "bug", description = "Report a bug", ephemeral = true)      // Command: /example channel
    @Command.DisableAcknowledge                                                     // This is required because we can only acknowledge an interaction once and
                                                                                    // sending a modal also acknowledges it
    public static void executeWithModel(@NotNull CommandContext context) {

        if (!context.isInteraction()) {
            context.reply("This command can only be used in interactions!").queue();
            return;
        }

        // Create the modal inputs

        TextInput subject = TextInput.create("subject", TextInputStyle.SHORT)
                .setPlaceholder("Subject of this ticket")
                .setMinLength(10)
                .setMaxLength(100) // or setRequiredRange(10, 100)
                .build();

        TextInput body = TextInput.create("body", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Your concerns go here")
                .setMinLength(30)
                .setMaxLength(1000)
                .build();

        Modal modal = Modal.create("modmail", "Modmail")
                .addComponents(Label.of("Subject", subject), Label.of("Body", body))
                .build();

        // Show the modal to the user and acknowledge the interaction
        context.replyModal(modal).queue();
    }

}
