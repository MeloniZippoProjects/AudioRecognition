package org.melonizippo.audiorecognition;

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.Wave;

import java.util.Arrays;

public class Fingerprint
{
    private byte[] bytes;

    public Fingerprint(String filepath)
    {
        bytes = (new Wave(filepath)).getFingerprint();
    }

    public Fingerprint(byte[] bytes)
    {
        bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public double GetSimilarity(Fingerprint other)
    {
        FingerprintSimilarityComputer computer = new FingerprintSimilarityComputer(this.bytes, other.bytes);
        FingerprintSimilarity similarity = computer.getFingerprintsSimilarity();
        return similarity.getSimilarity();
    }
}
