package net.royalur;

import net.royalur.lut.StateLUT;
import net.royalur.model.GameSettings;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
//        RGUStatistics.main(args);

        System.out.println(new StateLUT(GameSettings.MASTERS).countStates());
    }
}
