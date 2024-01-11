package io.github.itzispyder.pdk.utils.discord;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public record DiscordWebhook(String username, String avatar_url, String content, boolean tts, DiscordEmbed... embeds) {

    public DiscordWebhook(String username, String avatar_url, String content, DiscordEmbed... embeds) {
        this(username, avatar_url, content, false, embeds);
    }

    public DiscordWebhook(String username, String content, DiscordEmbed... embeds) {
        this(username, null, content, false, embeds);
    }

    public void send(String spec) throws IOException {
        URL url = new URL(spec);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("User-Agent", "ImproperIssues-Java-DiscordWebhook-Sender");
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        OutputStream stream = conn.getOutputStream();
        stream.write(new Gson().toJson(this).getBytes());
        stream.flush();
        stream.close();

        conn.getInputStream().close();
        conn.disconnect();
    }

    public boolean trySend(String spec) {
        try {
            send(spec);
            return true;
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
            System.err.println("For webhook: " + new Gson().toJson(this));
            return false;
        }
    }

    public static Builder create() {
        return new Builder();
    }

    public static class Builder {
        private String username, avatar_url, content;
        private boolean tts;
        private List<DiscordEmbed> embeds;

        public Builder() {
            embeds = new ArrayList<>();
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar_url = avatar;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder textToSpeech() {
            this.tts = true;
            return this;
        }

        public Builder addEmbed(DiscordEmbed embed) {
            this.embeds.add(embed);
            return this;
        }

        public Builder addEmbed(String title, String url, String description, int color, DiscordEmbed.Field... fields) {
            return addEmbed(new DiscordEmbed(title, url, description, color, fields));
        }

        public Builder addEmbed(String title, String description, int color, DiscordEmbed.Field... fields) {
            return addEmbed(new DiscordEmbed(title, null, description, color, fields));
        }

        public Builder addEmbed(String title, String description, DiscordEmbed.Field... fields) {
            return addEmbed(new DiscordEmbed(title, null, description, DiscordEmbed.DEFAULT_COLOR, fields));
        }

        public Builder addEmbed(DiscordEmbed.Image image) {
            return addEmbed(DiscordEmbed.create().image(image).build());
        }

        public Builder addEmbed(String title, String desc, String imageUrl) {
            return addEmbed(DiscordEmbed.create().title(title).desc(desc).image(imageUrl).build());
        }

        public DiscordWebhook build() {
            return new DiscordWebhook(username, avatar_url, content, tts, embeds.toArray(DiscordEmbed[]::new));
        }

        public void send(String spec) {
            build().trySend(spec);
        }
    }
}
