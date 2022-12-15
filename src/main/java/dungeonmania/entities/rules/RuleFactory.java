package dungeonmania.entities.rules;

public class RuleFactory {
    public Rule createRule(String rule) {
        switch (rule) {
        case "and":
            return new AndRule();
        case "or":
            return new OrRule();
        case "xor":
            return new XorRule();
        case "co_and":
            return new CoAndRule();
        default:
            return null;
        }
    }
}
