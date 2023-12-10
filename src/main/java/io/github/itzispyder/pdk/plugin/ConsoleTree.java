package io.github.itzispyder.pdk.plugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsoleTree {

    /*
    EXAMPLE CONSOLE TREE TEXT:
    -------------------------------------------------------------
     String text = ConsoleTree.createTree()
                .title("title")
                .subtitle("subtitle")
                .addProperty("property1", "value")
                .addProperty("property2", ConsoleTree.branch()
                        .addProperty("branch-property1", "value")
                        .addProperty("branch-property2", "value")
                        .addProperty("branch-property3", "value")
                        .addProperty("branch-property4", ConsoleTree.branch()
                                .addProperty("subbranch-property1", "value")
                                .addProperty("subbranch-property2", "value")
                                .build())
                        .build())
                .addProperty("property3", "value")
                .print();
     */

    public static final int INDENT_STRENGTH = 5;
    private String title, subtitle;
    private final LinkedHashMap<String, Branch> properties;

    public ConsoleTree() {
        this.title = "Hello World";
        this.subtitle = "Branches";
        this.properties = new LinkedHashMap<>();
    }

    public static ConsoleTree createTree() {
        return new ConsoleTree();
    }

    public ConsoleTree title(String title) {
        this.title = title;
        return this;
    }

    public ConsoleTree subtitle(String subtitle) {
        this.subtitle = subtitle;
        return this;
    }

    public ConsoleTree addProperty(String key, String value) {
        properties.put(key, Branch.of(value));
        return this;
    }

    public ConsoleTree addProperty(String key, Branch value) {
        properties.put(key, value);
        return this;
    }

    public static Branch.BranchBuilder branch() {
        return new Branch.BranchBuilder();
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        AtomicInteger i = new AtomicInteger(0);
        int amount = i.get() * INDENT_STRENGTH;

        sb.newLine().append(title);
        sb.newLine().append(subtitle);
        sb.newLine().append("------------------------");

        for (Map.Entry<String, Branch> entry : properties.entrySet()) {
            String k = entry.getKey();
            Branch b = entry.getValue();

            if (b == null || k == null || k.isEmpty()) continue;

            if (b.hasProperties()) {
                sb.newLine().indent(amount);
                sb.append(k + ": ");
                i.getAndIncrement();
                b.print(i, sb);
            }
            else {
                sb.newLine().indent(amount);
                sb.append(k + ": " + b.getTitle());
            }
        }

        return sb.newLine().build();
    }



    public static class Branch {
        private String name;
        private final LinkedHashMap<String, Branch> properties;

        public Branch(String title, LinkedHashMap<String, Branch> properties) {
            this.name = title;
            this.properties = properties;
        }

        public Branch() {
            this("untitled-branch", new LinkedHashMap<>());
        }

        private static Branch of(String title) {
            return new Branch(title, new LinkedHashMap<>());
        }

        public boolean hasProperties() {
            return !properties.isEmpty();
        }

        public Map<String, Branch> getProperties() {
            return properties;
        }

        public Branch getProperty(String key) {
            return properties.get(key);
        }

        public boolean hasProperty(String key){
            return properties.containsKey(key);
        }

        public String getTitle() {
            return name;
        }

        public void setTitle(String name) {
            this.name = name;
        }

        public static BranchBuilder create() {
            return new BranchBuilder();
        }

        public StringBuilder print(AtomicInteger indent, StringBuilder printer) {
            int amount = indent.get() * INDENT_STRENGTH;

            for (Map.Entry<String, Branch> entry : properties.entrySet()) {
                String k = entry.getKey();
                Branch b = entry.getValue();

                if (b == null || k == null || k.isEmpty()) continue;

                if (b.hasProperties()) {
                    printer.newLine().indent(amount);
                    printer.append(k + ": ");
                    indent.getAndIncrement();
                    b.print(indent, printer);
                }
                else {
                    printer.newLine().indent(amount);
                    printer.append(k + ": " + b.getTitle());
                }
            }

            return printer;
        }



        public static class BranchBuilder {
            private String name;
            private final LinkedHashMap<String, Branch> properties;

            public BranchBuilder() {
                this.name = "untitled-branch";
                this.properties = new LinkedHashMap<>();
            }

            public BranchBuilder setTitle(String name) {
                this.name = name;
                return this;
            }

            public BranchBuilder addProperty(String key, Branch value) {
                properties.put(key, value);
                return this;
            }

            public BranchBuilder addProperty(String key, String value) {
                properties.put(key, Branch.of(value));
                return this;
            }

            public Branch build() {
                Branch b = new Branch(name, properties);
                return b;
            }
        }
    }



    private static class StringBuilder {
        private String s;

        public StringBuilder() {
            this.s = "";
        }

        public StringBuilder append(String s) {
            this.s += s;
            return this;
        }

        public StringBuilder append(StringBuilder b) {
            this.s += b.s;
            return this;
        }

        public StringBuilder newLine() {
            return append("\n");
        }

        public StringBuilder indent(int amount) {
            this.s += " ".repeat(amount);
            return this;
        }

        public String build() {
            return s;
        }
    }
}
