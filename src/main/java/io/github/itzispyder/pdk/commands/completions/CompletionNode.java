package io.github.itzispyder.pdk.commands.completions;

import java.util.ArrayList;
import java.util.List;

public class CompletionNode {

    final List<String> values;
    final List<CompletionNode> nextOptions;
    final String regex;

    CompletionNode(List<String> values, List<CompletionNode> nextOptions, String regex) {
        this.values = values;
        this.nextOptions = nextOptions;
        this.regex = regex;
    }

    CompletionNode(String values) {
        this(List.of(values), new ArrayList<>(), null);
    }

    public static boolean strictContains(CompletionNode parent, String subject) {
        for (String value : parent.values)
            if (value.equals(subject))
                return true;
        return false;
    }

    public static boolean contains(CompletionNode parent, String subject) {
        for (String value : parent.values)
            if (value.contains(subject))
                return true;
        return false;
    }

    public static boolean containsRegex(CompletionNode parent, String subject) {
        if (parent.regex == null)
            return false;
        return subject.matches(parent.regex);
    }

    public boolean optionsRegexMatchesArg(String argument) {
        for (CompletionNode option : nextOptions)
            if (containsRegex(option, argument))
                return true;
        return false;
    }

    public CompletionNode next(String argument) {
        for (CompletionNode option : nextOptions)
            if (containsRegex(option, argument))
                return option;

        for (CompletionNode option : nextOptions)
            if (strictContains(option, argument))
                return option;

        for (CompletionNode option : nextOptions)
            if (contains(option, argument))
                return option;

        return null;
    }

    public List<String> getOptions() {
        List<String> a = new ArrayList<>();
        for (CompletionNode o : nextOptions) {
            a.addAll(o.values);
        }
        return a;
    }

    public boolean isRegex() {
        return regex != null;
    }

    public boolean isOptionsRegex() {
        return nextOptions.stream().anyMatch(CompletionNode::isRegex);
    }

    public List<CompletionNode> getNextOptions() {
        return nextOptions;
    }

    public List<String> getValues() {
        return values;
    }
}