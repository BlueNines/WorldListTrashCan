import java.util.*;
import java.util.regex.Pattern;

public class EntityCleanMatcher {

    private final Set<String> whiteExact = new HashSet<>();
    private final List<Pattern> whiteRegex = new ArrayList<>();

    private final Set<String> blackExact = new HashSet<>();
    private final List<Pattern> blackRegex = new ArrayList<>();

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

            if (rule.contains("*")) {
                String regex = rule
                        .replace(".", "\\.")
                        .replace("*", ".*");

                Pattern pattern = Pattern.compile("^" + regex + "$");

                if (white) {
                    whiteRegex.add(pattern);
                } else {
                    blackRegex.add(pattern);
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

    /**
     * @return true = 应该被清理
     * false = 不应该被清理
     * null = 不在黑白名单中
     */
    public Boolean checkClean(String entityType, String entityName) {
        entityType = entityType.toLowerCase();
        entityName = entityName.toLowerCase();

        // 白名单优先
        if (matchWhite(entityType) || matchWhite(entityName)) {
            return false;
        }

        // 黑名单
        if (matchBlack(entityType) || matchBlack(entityName)) {
            return true;
        }

        // 默认不清理
        return null;
    }

    private boolean matchWhite(String value) {
        if (whiteExact.contains(value)) return true;

        for (Pattern p : whiteRegex) {
            if (p.matcher(value).matches()) return true;
        }
        return false;
    }

    private boolean matchBlack(String value) {
        if (blackExact.contains(value)) return true;

        for (Pattern p : blackRegex) {
            if (p.matcher(value).matches()) return true;
        }
        return false;
    }
}
