package net.royalur;

import net.royalur.lut.LutTrainer;
import net.royalur.model.GameSettings;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//        RGUStatistics.main(args);

        LutTrainer.main(args);

//        System.out.println(new LutTrainer(GameSettings.MASTERS).countStates());
    }
}
