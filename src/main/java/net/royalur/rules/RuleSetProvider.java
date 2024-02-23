package net.royalur.rules;

import net.royalur.model.GameMetadata;
import net.royalur.model.GameSettings;

/**
 * Creates rule sets to match game settings.
 */
public interface RuleSetProvider {

    /**
     * Creates a rule set to match the given settings and game metadata.
     * @param settings The settings of the game.
     * @param metadata The metadata associated with the game.
     * @return A rule set matching the given settings and game metadata.
     */
    RuleSet create(
            GameSettings settings,
            GameMetadata metadata
    );
}
