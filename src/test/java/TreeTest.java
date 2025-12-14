
import dev.spoocy.jdaextensions.commands.annotations.Command;
import dev.spoocy.jdaextensions.commands.annotations.Cooldown;
import dev.spoocy.jdaextensions.commands.annotations.Permissions;
import dev.spoocy.jdaextensions.commands.arguments.Arguments;
import dev.spoocy.jdaextensions.commands.arguments.ProvidedArgument;
import dev.spoocy.jdaextensions.commands.cooldown.CooldownScope;
import dev.spoocy.jdaextensions.commands.event.CommandContext;
import dev.spoocy.jdaextensions.commands.structure.DiscordCommand;
import dev.spoocy.jdaextensions.commands.tree.CommandTree;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

@Command(
        name = "tree",
        description = "Tree Command Test",
        context = {InteractionContextType.GUILD, InteractionContextType.BOT_DM}
)
public class TreeTest {

    @Command.Default
    @Cooldown(value = 5, unit = TimeUnit.MINUTES, scope = CooldownScope.USER)
    @Permissions({Permission.MESSAGE_SEND, Permission.MESSAGE_EMBED_LINKS})
    public static void execute(
            @NotNull CommandContext context,

            @Arguments.Text(
                    name = "Text",
                    description = "Text Argument",
                    maxLength = 10,
                    minLength = 1
            ) String text,

            @Arguments.Number(
                    name = "Number",
                    description = "Number Argument",
                    minValue = 0,
                    maxValue = 100
            ) double number
    ) {



    }


    public static void main(String[] args) {

        DiscordCommand cmd = new CommandTree("root", "Test")
                .withContext(InteractionContextType.GUILD, InteractionContextType.BOT_DM)
                .withDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .arg(Arguments.string("mainArg", "Main Argument", false, false))
                .executes(context -> {

                })

                .then(CommandTree.command("alone", "Alone Command")
                        .arg(Arguments.number("aloneArg", "Alone Argument", true, false))
                        .executes(context -> {
                            ProvidedArgument a = context.getArgument("aloneArg");
                            a.getAsAttachment();
                        })
                )

                .then(CommandTree.group("group1", "First Group")
                        .then(CommandTree.command("sub1", "First Subcommand")
                                .arg(Arguments.integer("arg1", "An Integer Argument", true, false))
                                .executes(context -> {

                                })
                        )
                )

                .then(CommandTree.group("group2", "Second Group")
                        .then(CommandTree.command("sub2", "Second Subcommand")
                                .arg(Arguments.bool("arg2", "A Boolean Argument", false, false))
                                .executes(context -> {

                                })
                        )
                )

                .build()

                ;


    }


}
