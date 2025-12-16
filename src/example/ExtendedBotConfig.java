import dev.spoocy.jdaextensions.core.BotConfig;
import dev.spoocy.utils.config.Document;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class ExtendedBotConfig extends BotConfig {

    public ExtendedBotConfig(@NotNull Document config) {
        super(config);
    }

    public ExtendedBotConfig(@NotNull File jsonFile) {
        super(jsonFile);
    }

    @Override
    protected void loadDefaults() {
        super.loadDefaults();
        setDefault("someOption", 123);
        setDefault("anotherOption", "defaultValue");
    }

    public int someOption() {
        return this.getDocument().getInt("someOption");
    }

    public String anotherOption() {
        return this.getDocument().getString("anotherOption");
    }

}
