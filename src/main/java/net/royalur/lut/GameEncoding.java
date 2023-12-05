package net.royalur.lut;

import net.royalur.rules.simple.fast.FastSimpleGame;

import javax.annotation.Nonnull;

public abstract class GameEncoding {

    public abstract int encodeGame(@Nonnull FastSimpleGame game);
}
