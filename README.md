JDA Extensions
=================

Helpers and extensions for JDA (Java Discord API).
<br>
This repository provides utilities to make building Discord bots with JDA easier.

It contains a small set of helpers:
- Helper classes to configure and run JDA-based bots.
- Command Manager for slash and prefix commands.
- CommandTree and annotation-based command registration.
- EventWaiter utility to wait for specific events (like message replies or reactions).

## Download

```xml
<repository>
    <id>spoocyDev-releases</id>
    <name>Coding Stube Repository</name>
    <url>https://repo.coding-stube.de/releases</url>
</repository>
```

```xml
<dependency>
    <groupId>dev.spoocy</groupId>
    <artifactId>jda-extensions</artifactId>
    <version>VERSION</version>
</dependency>
```

Be sure to replace the **VERSION** key below with the version shown above!

## Versions
The following library versions are created with the respective JDA versions:

| jda-extended | JDA   |
|--------------|-------|
| 1.0.0        | 6.1.0 |

---

## EventWaiter

The library includes an `EventWaiter` utility to wait for a single event that matches a predicate (a common pattern when you ask a user a question and expect a reply). Below are two typical patterns: waiting for a message and waiting for a reaction.

Register the waiter as a listener so it receives events:

```java
EventWaiter waiter = new EventWaiter();
shardManagerBuilder.addEventListeners(waiter);
```

Waiting for a message reply (with timeout):

```java
waiter.waitFor(MessageReceivedEvent.class)
    .runIf(event -> event.getAuthor().equals(user) && event.getChannel().equals(channel))
    .run(event -> channel.sendMessage("Got your reply: " + event.getMessage().getContentRaw()).queue())
    .timeoutAfter(30, TimeUnit.SECONDS)
    .runOnTimeout(() -> channel.sendMessage("Timed out waiting for a reply.").queue())
    .build();
```

You can find a basic implementation here: [BotExample.java](src/main/example/BotExample.java).

## BotBuilder

The library provides a `BotBuilder` and  `BotConfig` that make it easier to configure and run a JDA bot.

Your main bot class should extend `DiscordBot`:
```java
public class BotExample extends DiscordBot {
    
    public BotExample(@NotNull BotConfig config, @NotNull BotBuilder builder) {
        super(config, builder);
        ...
    }
    
    ...
    
}
```

You will also need a json config file for your bot token, online status, and other settings;
```java
Document config = Document.readFile(JsonConfig.class, new File("config.json"));
BotConfig botConfig = new BotConfig(config);
```
If you need other custom configuration options you may create your own config class that extends `BotConfig`. <br>
You can find an example here: [ExtendedBotConfig.java](src/main/example/ExtendedBotConfig.java).

Then create a `BotBuilder` to register commands and listeners:
```java
BotBuilder builder = new BotBuilder()
        .addActivity(() -> Activity.playing("Testing..."))
        .setAllIntents()
        .addCommand(new CommandTree("ping", "Replies with Pong!")
                .executes(context -> context.reply("Pong!"))
                .build())
        .addListener(new ListenerExample())
        .addCommand(AnnotationCommandExample.class);
```

Finally, create your bot instance:
```java
new BotExample(botConfig, builder);
```

You can find a basic implementation here: [BotExample.java](src/main/example/BotExample.java).

## CommandTree

The `CommandTree` builder provides a fluent API to register slash/prefix commands programmatically. In the examples you'll find:

You can find a basic implementation here: [BotExample.java](src/main/example/BotExample.java).

## Annotation-based commands

You can find a basic implementation here: [AnnotationCommandExample.java](src/main/example/AnnotationCommandExample.java).

## Java Version
This library requires Java 11 or newer.