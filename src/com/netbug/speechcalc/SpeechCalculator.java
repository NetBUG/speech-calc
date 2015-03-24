package com.netbug.speechcalc;
import android.annotation.TargetApi;
import android.os.Build;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NetBUG (Oleg Urzhumtsev) on 16/03/15.
 */

public class SpeechCalculator {
    public static int countNumbersAndArithmeticOperations(String s) {
        Pattern pattern = Pattern.compile("\\d+|[\\.+*/%-]");
        Matcher matcher = pattern.matcher(s);

        int count = 0;
        while (matcher.find())
            count++;

        return count;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
	public static List<RecognitionGuess> processRecognitionResult(List<String> recognitionResults) {
        List<RecognitionGuess> normalizedResults = new ArrayList<RecognitionGuess>();
        for (String result: recognitionResults) {
            normalizedResults.add(new RecognitionGuess(result));
        }

        Collections.sort(normalizedResults, new Comparator<RecognitionGuess>() {
            @Override
            public int compare(RecognitionGuess g, RecognitionGuess g2) {
                return countNumbersAndArithmeticOperations(g2.getNormalizedGuess())
                       - countNumbersAndArithmeticOperations(g.getNormalizedGuess());
            }
        });

        Collections.sort(normalizedResults, new Comparator<RecognitionGuess>() {
            @Override
            public int compare(RecognitionGuess g, RecognitionGuess g2) {
                return -Boolean.compare(g.getIsEvaluated(), g2.getIsEvaluated());
            }
        });

        return normalizedResults;
    }
}
