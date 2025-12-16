import java.util.ArrayList;
import java.util.List;

public class MatcherTest {

    public static void main(String[] args) {

        List< String> whitelist = new ArrayList<>();
        whitelist.add("villager");
        whitelist.add("*golem");
        whitelist.add("ender_dragon");
        List< String> blacklist = new ArrayList<>();
        blacklist.add("pig");
        blacklist.add("*zombie*");
        blacklist.add("*slime");

        EntityCleanMatcher matcher = new EntityCleanMatcher(
                // 白名单
                whitelist,
                // 黑名单
                blacklist
        );

        test(matcher, "pig", "Pig", true);
        test(matcher, "zombie", "Zombie", true);
        test(matcher, "zombie_villager", "Zombie Villager", true);
        test(matcher, "slime", "Slime", true);
        test(matcher, "magma_slime", "Magma Cube", true);

        test(matcher, "villager", "Villager", false);
        test(matcher, "iron_golem", "Iron Golem", false);
        test(matcher, "snow_golem", "Snow Golem", false);
        test(matcher, "ender_dragon", "Dragon", false);

        test(matcher, "cow", "Cow", false);
        test(matcher, "sheep", "Sheep", false);
    }

    private static void test(EntityCleanMatcher matcher,
                             String type,
                             String name,
                             Boolean expected) {

        Boolean result = matcher.checkClean(type, name);

        System.out.printf(
                "[TEST] type=%-16s name=%-20s => shouldClean=%-5s | expected=%-5s %s%n",
                type,
                name,
                result,
                expected,
                result == expected ? "✔ PASS" : "✘ FAIL"
        );
    }
}
