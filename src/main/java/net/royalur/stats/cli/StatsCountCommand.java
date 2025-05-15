package net.royalur.stats.cli;

import net.royalur.cli.CLI;
import net.royalur.cli.CLICommand;
import net.royalur.cli.CLIConstants;
import net.royalur.cli.CLIHandler;
import net.royalur.model.GameSettings;
import net.royalur.rules.simple.fast.FastSimpleFlags;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

public class StatsCountCommand extends CLICommand {

    public static final String NAME = "count";
    public static final String DESC = "Count the number of states under common rulesets";

    public StatsCountCommand(CLICommand parent) {
        super(parent, NAME, DESC);
    }

    @Override
    public @Nullable CLIHandler handle(CLI cli) {
        return StatsCountCommand::countStates;
    }

    public static void countStates() {
        for (GameSettings settings : CLIConstants.COMMONLY_PLAYED) {
            FastSimpleFlags flags = new FastSimpleFlags(settings);

            AtomicLong totalCount = new AtomicLong(0);
            AtomicLong nonWinCount = new AtomicLong(0);
            AtomicLong onePlayerCount = new AtomicLong(0);

            flags.loopLightGameStates((game) -> {
                totalCount.addAndGet(2);
                onePlayerCount.incrementAndGet();
                if (!game.isFinished) {
                    nonWinCount.addAndGet(2);
                }
            });

            System.out.println(settings.getName() + ":");
            System.out.println("* Total states = " + totalCount.get());
            System.out.println("* States excluding win states = " + nonWinCount.get());
            System.out.println("* States per player = " + onePlayerCount.get());
            System.out.println();
        }
    }
}
