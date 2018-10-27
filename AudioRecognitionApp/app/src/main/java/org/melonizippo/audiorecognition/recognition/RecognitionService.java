package org.melonizippo.audiorecognition.recognition;

import android.content.Context;
import android.util.Log;

import org.melonizippo.audiorecognition.database.AudioMetadata;
import org.melonizippo.audiorecognition.database.FingerprintsDatabaseAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class RecognitionService
{
    private static final double RECOGNITION_THRESHOLD = 0.5;
    private static final String LOG_TAG = "RecognitionService";

    public static RecognitionResult recognize(Fingerprint referenceFingerprint, Context context)
    {
        Map<Integer, Fingerprint> fingerprints = getFingerprints(context);

        Map<Integer, Double> similarities = computeSimilarities(referenceFingerprint, fingerprints);

        int bestId = -1;
        double bestSimilarity = -1;

        for ( Integer id : similarities.keySet())
        {
            double similarity = similarities.get(id);

            //Debug logging
            AudioMetadata metadata = getMetadata(id, context);
            Log.d(LOG_TAG, "Processed " + metadata.title + " with similarity " + similarity);

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

    private static Map<Integer, Double> computeSimilarities(Fingerprint referenceFingerprint, Map<Integer, Fingerprint> fingerprints)
    {
        ExecutorService executorService = Executors.newWorkStealingPool();
        Map<Integer, Future<Double>> futures = new HashMap<>();
        for ( Integer id : fingerprints.keySet())
        {
            Callable<Double> callable = () -> {
                Fingerprint fingerprint = fingerprints.get(id);
                return referenceFingerprint.GetSimilarity(fingerprint);
            };
            Future<Double> future = executorService.submit(callable);
            futures.put(id, future);
        }

        executorService.shutdown();
        try{
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            Map<Integer, Double> similarities = new HashMap<>();
            for( Integer id : futures.keySet())
            {
                similarities.put(id, futures.get(id).get());
            }
            return similarities;
        }
        catch (InterruptedException e)
        {
            Log.d(LOG_TAG,"Executor interrupted");
            return null;
        }
        catch (ExecutionException e)
        {
            Log.d(LOG_TAG, "Future was not ready");
            return null;
        }
    }

    private static Map<Integer, Fingerprint> getFingerprints(Context context)
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
