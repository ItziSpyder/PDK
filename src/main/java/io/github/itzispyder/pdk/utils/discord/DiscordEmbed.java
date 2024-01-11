package io.github.itzispyder.pdk.utils.discord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record DiscordEmbed(String title, String url, String description, String timestamp, int color, Author author, Image image, Thumbnail thumbnail, Footer footer, Field... fields) {

    public static final int DEFAULT_COLOR = 0x909090;

    public DiscordEmbed(String title, String url, String description, int color, Author author, Field... fields) {
        this(title, url, description, LocalDateTime.now().toString(), color, author, null, null, null, fields);
    }

    public DiscordEmbed(String title, String url, String description, int color, Field... fields) {
        this(title, url, description, color, null, fields);
    }

    public DiscordEmbed(String title, String description, int color, Field... fields) {
        this(title, null, description, color, null, fields);
    }

    public DiscordEmbed(String title, String description, Field... fields) {
        this(title, null, description, DEFAULT_COLOR, fields);
    }

    public record Author(String name, String url, String icon_url) {
        public Author(String name, String icon_url) {
            this(name, null, icon_url);
        }

        public Author(String name) {
            this(name, null, null);
        }
    }

    public record Image(String url) {

    }

    public record Thumbnail(String url) {

    }

    public record Footer(String text, String icon_url) {
        public Footer(String text) {
            this(text, null);
        }
    }

    public record Field(String name, String value, boolean inline) {
        public Field(String name, String value) {
            this(name, value, false);
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String title, url, description, timestamp;
        private int color;
        private Author author;
        private Image image;
        private Thumbnail thumbnail;
        private Footer footer;
        private List<Field> fields;

        public Builder() {
            color = DEFAULT_COLOR;
            fields = new ArrayList<>();
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder desc(String desc) {
            this.description = desc;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp.toString();
            return this;
        }

        public Builder color(int hex) {
            this.color = hex;
            return this;
        }

        public Builder author(Author author) {
            this.author = author;
            return this;
        }

        public Builder author(String name, String url, String icon_url) {
            return author(new Author(name, url, icon_url));
        }

        public Builder author(String name, String icon_url) {
            return author(new Author(name, null, icon_url));
        }

        public Builder author(String name) {
            return author(new Author(name, null, null));
        }

        public Builder image(Image image) {
            this.image = image;
            return this;
        }

        public Builder image(String url) {
            return image(new Image(url));
        }

        public Builder thumbnail(Thumbnail thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder thumbnail(String url) {
            return thumbnail(new Thumbnail(url));
        }

        public Builder footer(Footer footer) {
            this.footer = footer;
            return this;
        }

        public Builder footer(String text, String icon_url) {
            return footer(new Footer(text, icon_url));
        }

        public Builder footer(String text) {
            return footer(new Footer(text, null));
        }

        public Builder addField(Field field) {
            this.fields.add(field);
            return this;
        }

        public Builder addField(String name, String value, boolean inline) {
            return addField(new Field(name, value, inline));
        }

        public Builder addField(String name, String value) {
            return addField(new Field(name, value, false));
        }

        public DiscordEmbed build() {
            return new DiscordEmbed(title, url, description, timestamp, color, author, image, thumbnail, footer, fields.toArray(Field[]::new));
        }
    }
}
