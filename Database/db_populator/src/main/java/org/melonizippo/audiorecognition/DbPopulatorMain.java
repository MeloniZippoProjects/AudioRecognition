package org.melonizippo.audiorecognition;

import com.musicg.wave.Wave;
import org.apache.commons.codec.binary.Hex;

import java.nio.file.Path;
import java.nio.file.Paths;

public class DbPopulatorMain {

    public static void main(String[] args)
    {
        Path wavFilePath = Paths.get(args[0]);
        Wave wave = new Wave(wavFilePath.toString());
        byte[] fingerprint = wave.getFingerprint();

        String encoded = new String(Hex.encodeHex(fingerprint));

        System.out.println(encoded);
    }

}
