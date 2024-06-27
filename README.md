# PDK
The Best Plugin Development Kit
<br>
---
### Adding to Gradle

#### 1) Duplicates strategy
```gradle
jar {
    from {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        configurations.runtimeClasspath.collect {
            it.isDirectory() ? it : zipTree(it)
        }
    }
}
```

#### 2) Add implementations
```yml
- download latest release of PDK
- create a folder named libs in your project and drag your downloaded jar in there
- type the following down in your build.gradle
```
```gradle
dependencies {
    implementation files("libs/PDK-1.3.5.jar")
}
```

#### 3) Set encoding to UTF-8
```gradle
compileJava.options.encoding = 'UTF-8'
tasks.withType(JavaCompile).configureEach {
    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible()) {
        options.encoding = 'UTF-8'
        options.release.set(targetJavaVersion)
    }
}
```

#### 4) Initialize PDK `PDK.init(this)`
```java
public final class CustomPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        PDK.init(this);

        // commands
        new TestCommand().register();

        // listeners
        new TestListener().register();

        // Items
        new TestCustomItem().register();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
```

---

### GUI Builders
Create complex GUI's with a few simple calls!
```java
public class TestGUI {

    public static final CustomGui GUI = CustomGui.create()
            .title("Super Epik Title")
            .size(27)
            .onClose(e -> e.getPlayer().sendMessage("e"))
            .defineMain(e -> {
                e.setCancelled(true);
            })
            .define(11, new ItemStack(Material.BARRIER), e -> {
                ServerUtils.dispatchf(CustomPlugin.class,"kill %s", e.getWhoClicked().getUniqueId());
            })
            .define(13, new ItemStack(Material.GREEN_WOOL), e -> {
                e.getWhoClicked().sendMessage("EeeeeEEE");
                e.getWhoClicked().closeInventory();
            })
            .define(15, new ItemStack(Material.DIAMOND), e -> {
                e.getWhoClicked().getInventory().addItem(e.getCurrentItem().clone());
                e.getWhoClicked().closeInventory();
            })
            .build();
    
    public static void openFor(Player player) {
        player.openInventory(GUI.getInventory());
    }
}
```

### Item Builders
ItemMetas are too annoying for something as simple as adding lore?
```java
public class TestItem {
    
    public static final ItemStack ITEM = ItemBuilder.create()
            .name("Custom Title")
            .lore("lore 1")
            .lore("lore 2")
            .enchant(Enchantment.MENDING, 5)
            .count(64)
            .build();
}
```

### Discord Webhooks
Sick of parsing JSON? No worries!
```java
public class DiscordWebhookSender {

    public static void sendWebhook(String webhookUrl) {
        DiscordWebhook.create() // none of the below are required, required ones already have their default values from the builder
                .username(/* custom username of the webhook */)
                .textToSpeech() // enables text to speech
                .avatar(/* custom avatar url */)
                .content(/* the message to send */)
                .addEmbed(new DiscordEmbed.Image(/* send an embed with only an image */))
                .addEmbed(DiscordEmbed.create()
                        .url(/* embed title's click event url */)
                        .desc/* embed description */()
                        .title(/* embed title */)
                        .author(/* embed's author stamp (top left) */)
                        .image(/* embed's image */)
                        .footer(/* embed's footer */)
                        .color(/* embed's side color */)
                        .thumbnail(/* embed's thumbnail */)
                        .timestamp(/* embed's timestamp, suggested "LocalDateTime.now()" */)
                        .addField(/* add embed field */)
                        .build())
                .send(webhookUrl); // provide a webhook url
    }
}
```

### And More!
- Custom command builder
- Custom command completion tree
- Custom item with event action
- Custom console printing tree
- Block display raytracer (block display entities)
- Custom display raytracer (particles... etc)









