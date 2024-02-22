package net.royalur.lut;

import net.royalur.model.GameSettings;
import net.royalur.model.dice.Roll;
import net.royalur.notation.JsonNotation;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class LutCLI {

    public static final DecimalFormat MS_DURATION = new DecimalFormat("#,###");

    public static void main(String[] args) throws IOException {
        GameSettings<Roll> settings = GameSettings.FINKEL;//.withStartingPieceCount(2);
        FinkelGameStateEncoding encoding = new FinkelGameStateEncoding();
        JsonNotation<?, ?, Roll> jsonNotation = JsonNotation.createSimple();
        LutTrainer<Roll> trainer = new LutTrainer<>(settings, encoding, jsonNotation);

        File inputFile = new File("./models/finkel.rgu");
        File checkpointFile = new File("./finkel_ckpt.rgu");
        File outputFile = new File("./finkel.rgu");

        long readStart = System.nanoTime();
//        Lut<Roll> lut = trainer.populateNewLut();
        Lut<Roll> lut = Lut.read(jsonNotation, encoding, inputFile);
        lut.getMetadata().getAdditionalMetadata().clear();
        lut.getMetadata().addMetadata("author", "Padraig Lamont");
        double readDurationMs = (System.nanoTime() - readStart) / 1e6;
        System.out.println("Read or populate took " + MS_DURATION.format(readDurationMs) + " ms");

        lut = trainer.train(lut, checkpointFile, 0.001f);

        long start = System.nanoTime();
        lut.write(jsonNotation, outputFile);
        double durationMs = (System.nanoTime() - start) / 1e6;
        System.out.println("Write took " + MS_DURATION.format(durationMs) + " ms");
    }
}
