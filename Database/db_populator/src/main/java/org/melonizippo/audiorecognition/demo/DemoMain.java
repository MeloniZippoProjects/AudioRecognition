package org.melonizippo.audiorecognition.demo;

import com.musicg.fingerprint.FingerprintSimilarity;
import com.musicg.fingerprint.FingerprintSimilarityComputer;
import com.musicg.wave.Wave;

public class DemoMain {

    public static void main(String[] args)
    {
        Wave annunciatemi = new Wave("./music/annunciatemi.wav");
        Wave annunciatemi_clip = new Wave("./music/annunciatemi_clip.wav");
        Wave annunciatemi_reduced = new Wave("./music/annunciatemi_reduced.wav");
        Wave annunciatemi_rec_noise = new Wave("./music/annunciatemi_rec_noise2.wav");

        Wave hello = new Wave("./music/hello.wav");
        Wave hello_clip = new Wave("./music/hello_clip.wav");

        //annunciatemi.getFingerprintSimilarity(annunciatemi);

        FingerprintSimilarity fing;

        fing = annunciatemi.getFingerprintSimilarity(annunciatemi_clip);
        System.out.println("Annunciatemi vs annunciatemi_clip: " + fing.getSimilarity() + " at second: " + fing.getsetMostSimilarTimePosition() + "; score: " + fing.getScore());
        fing = annunciatemi.getFingerprintSimilarity(annunciatemi_rec_noise);
        System.out.println("Annunciatemi vs annunciatemi_rec_noise: " + fing.getSimilarity() + " at second: " + fing.getsetMostSimilarTimePosition() + "; score: " + fing.getScore());
        fing = annunciatemi_reduced.getFingerprintSimilarity(annunciatemi_rec_noise);
        System.out.println("Annunciatemi_reduced vs annunciatemi_rec_noise: " + fing.getSimilarity() + " at second: " + fing.getsetMostSimilarTimePosition() + "; score: " + fing.getScore());
        fing = annunciatemi.getFingerprintSimilarity(annunciatemi_reduced);
        System.out.println("Annunciatemi vs annunciatemi_reduced: " + fing.getSimilarity() + " at second: " + fing.getsetMostSimilarTimePosition() + "; score: " + fing.getScore());
        fing = annunciatemi.getFingerprintSimilarity(hello_clip);
        System.out.println("Annunciatemi vs hello_clip: " + fing.getSimilarity() + " at second: " + fing.getsetMostSimilarTimePosition() + "; score: " + fing.getScore());
        fing = hello.getFingerprintSimilarity(annunciatemi_clip);
        System.out.println("Hello vs annunciatemi_clip: " + fing.getSimilarity() + " at second: " + fing.getsetMostSimilarTimePosition() + "; score: " + fing.getScore());
        fing = hello.getFingerprintSimilarity(hello_clip);
        System.out.println("Hello vs Hello_clip: " + fing.getSimilarity() + " at second: " + fing.getsetMostSimilarTimePosition() + "; score: " + fing.getScore());
    }
}
