package net.royalur.model;

/**
 * The reason that a game was abandoned before it was finished.
 */
public enum AbandonReason {
    /**
     * A player left the game before it finished.
     */
    PLAYER_LEFT("player_left", "Player Left", true),

    /**
     * An external event caused the game to end before it finished.
     */
    EXTERNAL("external", "External", false);

    /**
     * A unique ID associated with this abandon reason.
     */
    private final String id;

    /*
     * An English name that can describe this abandon reason.
     */
    private final String name;

    /**
     * Whether this abandonment reason requires a player.
     */
    private final boolean requiresPlayer;

    /**
     * Instantiates an abandon reason.
     * @param id             A unique ID associated with this abandon reason.
     * @param name           An English name that can describe this abandon reason.
     * @param requiresPlayer Whether this abandonment reason requires a player.
     */
    AbandonReason(String id, String name, boolean requiresPlayer) {
        this.id = id;
        this.name = name;
        this.requiresPlayer = requiresPlayer;
    }

    /**
     * Gets a unique ID associated with this abandon reason.
     * @return A unique ID associated with this abandon reason.
     */
    public String getID() {
        return id;
    }

    /**
     * Gets an English name that can describe this abandon reason.
     * @return An English name that can describe this abandon reason.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets whether this abandonment reason requires a player.
     * @return Whether this abandonment reason requires a player.
     */
    public boolean requiresPlayer() {
        return requiresPlayer;
    }

    /**
     * Retrieves the abandon reason with {@param id}.
     * @param id The unique ID associated with an abandon reason.
     * @return The abandon reason associated with {@param id}.
     */
    public static AbandonReason getByID(String id) {
        for (AbandonReason mode : values()) {
            if (mode.getID().equals(id))
                return mode;
        }
        throw new IllegalArgumentException("Unknown abandon reason " + id);
    }
}
