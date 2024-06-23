package io.github.itzispyder.pdk.commands.completions;

import java.util.*;
import java.util.function.Function;

public class CompletionBuilder {

    private final CompletionNode root;
    private final List<CompletionBuilder> options;
    private boolean isBranch;
    private String regex;

    CompletionBuilder(List<String> names) {
        this.root = new CompletionNode(names, new ArrayList<>(), null);
        this.options = new ArrayList<>();
        this.isBranch = false;
    }

    public CompletionBuilder(String names) {
        this.root = new CompletionNode(names);
        this.options = new ArrayList<>();
        this.isBranch = false;
    }

    public CompletionBuilder(String regex, String details) {
        this.root = new CompletionNode(Collections.singletonList(details), new ArrayList<>(), regex);
        this.options = new ArrayList<>();
        this.isBranch = false;
        this.regex = regex;
    }

    public CompletionBuilder then(CompletionBuilder arg) {
        options.add(arg);
        root.nextOptions.add(arg.root);
        return this;
    }

    public CompletionBuilder arg(List<String> name) {
        CompletionBuilder b = new CompletionBuilder(name);
        b.isBranch = true;
        return b;
    }

    public CompletionBuilder argRegex(String regex, String details) {
        CompletionBuilder b = new CompletionBuilder(regex, details);
        b.isBranch = true;
        return b;
    }

    public CompletionBuilder arg(String... names) {
        return arg(Arrays.asList(names));
    }

    public CompletionBuilder arg(String name) {
        return arg(Collections.singletonList(name));
    }

    public <T> CompletionBuilder arg(Collection<T> input, Function<T, String> toString) {
        return arg(input.stream().map(toString).toList());
    }

    public CompletionBuilder next(String name) {
        for (CompletionBuilder o : options) {
            if (CompletionNode.strictContains(o.root, name)) {
                return o;
            }
        }
        return null;
    }

    public CompletionNode getRootNode() {
        return root;
    }

    public CompletionNode build() {
        if (this.isBranch) {
            throw new IllegalArgumentException("build() cannot be called on branches!");
        }
        return root;
    }

    public boolean isBranch() {
        return isBranch;
    }

    public boolean isRegex() {
        return regex != null;
    }
}