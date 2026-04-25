package org.worldlisttrashcan.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class EntityCleanMatcher {

    private static class Rule {
        final String pattern;
        final Pattern regex;
        final boolean forceClean;
        final boolean isRegex;

        Rule(String pattern, Pattern regex, boolean forceClean, boolean isRegex) {
            this.pattern = pattern;
            this.regex = regex;
            this.forceClean = forceClean;
            this.isRegex = isRegex;
        }
    }

    private final Set<String> whiteExact = new HashSet<>();
    private final List<Pattern> whiteRegex = new ArrayList<>();

    private final Set<String> blackExact = new HashSet<>();
    private final List<Rule> blackRules = new ArrayList<>();

    public EntityCleanMatcher(
            List<String> whiteList,
            List<String> blackList
    ) {
        loadRules(whiteList, true);
        loadRules(blackList, false);
    }

    private void loadRules(List<String> rules, boolean white) {
        for (String raw : rules) {
            String rule = raw.toLowerCase();
            boolean forceClean = true;

            if (rule.contains(":")) {
                String[] parts = rule.split(":");
                if (parts.length == 2) {
                    rule = parts[0].trim();
                    forceClean = Boolean.parseBoolean(parts[1].trim());
                }
            }

            if (rule.contains("*")) {
                String regex = rule
                        .replace(".", "\\.")
                        .replace("*", ".*");

                Pattern pattern = Pattern.compile("^" + regex + "$");

                if (white) {
                    whiteRegex.add(pattern);
                } else {
                    blackRules.add(new Rule(rule, pattern, forceClean, true));
                }
            } else {
                if (white) {
                    whiteExact.add(rule);
                } else {
                    blackExact.add(rule);
                }
            }
        }
    }

    public Boolean checkClean(String entityType, String entityName) {
        entityType = entityType.toLowerCase();
        entityName = entityName.toLowerCase();

        boolean hasCustomName = entityName != null && !entityName.isEmpty() && !entityName.equals(entityType);

        if (matchWhite(entityType) || matchWhite(entityName)) {
            return false;
        }

        if (matchBlack(entityType, hasCustomName) || matchBlack(entityName, hasCustomName)) {
            return true;
        }

        return null;
    }

    private boolean matchWhite(String value) {
        if (whiteExact.contains(value)) return true;

        for (Pattern p : whiteRegex) {
            if (p.matcher(value).matches()) return true;
        }
        return false;
    }

    private boolean matchBlack(String value, boolean hasCustomName) {
        if (blackExact.contains(value)) {
            return true;
        }

        for (Rule rule : blackRules) {
            if (rule.regex.matcher(value).matches()) {
                if (rule.forceClean) {
                    return true;
                } else {
                    return !hasCustomName;
                }
            }
        }
        return false;
    }
}