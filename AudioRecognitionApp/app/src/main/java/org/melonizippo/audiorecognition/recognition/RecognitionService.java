package org.melonizippo.audiorecognition.recognition;

import android.content.Context;

import org.melonizippo.audiorecognition.database.AudioMetadata;
import org.melonizippo.audiorecognition.database.FingerprintsDatabaseAdapter;

import java.util.Map;

public class RecognitionService
{
    private static final double RECOGNITION_THRESHOLD = 0.5;

    public static RecognitionResult Recognize(Fingerprint referenceFingerprint, Context context)
    {
        Map<Integer, Fingerprint> fingerprints = getFingerptrints(context);

        int bestId = -1;
        double bestSimilarity = -1;

        for ( Integer id : fingerprints.keySet())
        {
            Fingerprint fingerprint = fingerprints.get(id);
            double similarity = referenceFingerprint.GetSimilarity(fingerprint);

            if(similarity > bestSimilarity)
            {
                bestSimilarity = similarity;
                bestId = id;
            }
        }

        if(bestSimilarity < RECOGNITION_THRESHOLD)
        {
            return null;
        }
        else
        {
            AudioMetadata metadata = getMetadata(bestId, context);
            RecognitionResult result = new RecognitionResult();
            result.metadata = metadata;
            result.similarity = bestSimilarity;

            return result;
        }
    }

    private static Map<Integer, Fingerprint> getFingerptrints(Context context)
    {
        FingerprintsDatabaseAdapter dbAdapter = new FingerprintsDatabaseAdapter(context);
        dbAdapter.createDatabase();
        dbAdapter.open();
        Map<Integer, Fingerprint> fingerprints = dbAdapter.getFingerprints();
        dbAdapter.close();

        return fingerprints;
    }

    private static AudioMetadata getMetadata(int id, Context context)
    {
        FingerprintsDatabaseAdapter dbAdapter = new FingerprintsDatabaseAdapter(context);
        dbAdapter.createDatabase();
        dbAdapter.open();
        AudioMetadata fingerprints = dbAdapter.getMetadata(id);
        dbAdapter.close();

        return fingerprints;
    }
}
