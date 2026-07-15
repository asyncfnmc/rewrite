package dev.sinister.rewrite.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RewriteConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("rewrite.json");
    private static final Map<String, String> ENTRIES = new LinkedHashMap<>();

    private RewriteConfig() {}

    public static Map<String, String> entries() { return ENTRIES; }

    public static void load() {
        ENTRIES.clear();
        if (!Files.exists(FILE)) return;
        try (Reader reader = Files.newBufferedReader(FILE)) {
            Map<String, String> loaded = GSON.fromJson(reader, new TypeToken<LinkedHashMap<String, String>>() {}.getType());
            if (loaded != null) loaded.forEach((key, value) -> ENTRIES.put(clean(key), clean(value)));
        } catch (Exception ignored) { }
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) { GSON.toJson(ENTRIES, writer); }
        } catch (Exception exception) {
            throw new RuntimeException("Could not save Rewrite configuration", exception);
        }
    }

    public static String clean(String command) {
        String value = command == null ? "" : command.trim();
        while (value.startsWith("/")) value = value.substring(1).trim();
        return value;
    }

    public static String expand(String command) {
        String current = clean(command);
        for (int depth = 0; depth < 10; depth++) {
            int space = current.indexOf(' ');
            String head = space < 0 ? current : current.substring(0, space);
            String tail = space < 0 ? "" : current.substring(space);
            String replacement = ENTRIES.get(head);
            if (replacement == null || replacement.isBlank()) return current;
            String next = replacement + tail;
            if (next.equals(current)) return current;
            current = next;
        }
        return current;
    }
}
