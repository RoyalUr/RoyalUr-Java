package net.royalur.rules;

/**
 * A set of rules that govern the play of
 * a variant of the Royal Game of Ur.
 */
public abstract class RuleSet {

    /**
     * The name of this rule set.
     */
    public final String name;

    /**
     * @param name The name of this rule set.
     */
    protected RuleSet(String name) {
        this.name = name;
    }
}
