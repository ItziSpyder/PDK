package io.github.itzispyder.pdk.commands.completions;

import java.util.ArrayList;
import java.util.List;

public class CompletionNode {

    final List<String> values;
    final List<CompletionNode> nextOptions;

    CompletionNode(List<String> values, List<CompletionNode> nextOptions) {
        this.values = values;
        this.nextOptions = nextOptions;
    }

    CompletionNode(String values) {
        this(List.of(values), new ArrayList<>());
    }

    public static boolean strictContains(CompletionNode parent, String subject) {
        for (String value : parent.values) {
            if (value.equals(subject)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(CompletionNode parent, String subject) {
        for (String value : parent.values) {
            if (value.contains(subject)) {
                return true;
            }
        }
        return false;
    }

    public CompletionNode next(String name) {
        for (CompletionNode option : nextOptions) {
            if (strictContains(option, name)) {
                return option;
            }
        }

        for (CompletionNode option : nextOptions) {
            if (contains(option, name)) {
                return option;
            }
        }

        return null;
    }

    public List<String> getOptions() {
        List<String> a = new ArrayList<>();
        for (CompletionNode o : nextOptions) {
            a.addAll(o.values);
        }
        return a;
    }
}