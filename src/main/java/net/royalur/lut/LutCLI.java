package net.royalur.lut;

import net.royalur.model.GameSettings;
import net.royalur.notation.JsonNotation;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class LutCLI {

    public static final DecimalFormat MS_DURATION = new DecimalFormat("#,###");

    public static void main(String[] args) throws IOException {
        GameSettings settings = GameSettings.ASEB ;
        SimpleGameStateEncoding encoding = new SimpleGameStateEncoding(settings);
        JsonNotation jsonNotation = JsonNotation.createSimple();
        LutTrainer trainer = new LutTrainer(settings, encoding, jsonNotation);

        File inputFile = new File("./models/aseb.rgu");
        File checkpointFile = new File("./aseb_ckpt.rgu");
        File outputFile = new File("./aseb.rgu");

        long readStart = System.nanoTime();
        Lut lut = trainer.populateNewLut();
//        Lut lut = Lut.read(jsonNotation, encoding, inputFile);
        double readDurationMs = (System.nanoTime() - readStart) / 1e6;
        System.out.println("Read or populate took " + MS_DURATION.format(readDurationMs) + " ms");

        lut = trainer.train(lut, checkpointFile, 0.001f);

        long start = System.nanoTime();
        lut.write(jsonNotation, outputFile);
        double durationMs = (System.nanoTime() - start) / 1e6;
        System.out.println("Write took " + MS_DURATION.format(durationMs) + " ms");
    }
}
