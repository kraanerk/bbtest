package com.bbtest;

import com.bbtest.records.Task;

import java.util.HashMap;
import java.util.Map;

public class TaskSuccessEvaluator {
    // Key format: {Task::probability}-{Task::firstMessageWord}
    private static final Map<String, Double> successFailureRatios;
    static {
        // The content of the map has been populated using the output of
        // GameResultsAnalyzer::logTaskSuccessFailureRatiosByType
        successFailureRatios = new HashMap<>();
        successFailureRatios.put("Sure thing-Help", 6838.00);
        successFailureRatios.put("Sure thing-Escort", 560.00);
        successFailureRatios.put("Sure thing-Create", 541.00);
        successFailureRatios.put("Sure thing-Rescue", 293.00);
        successFailureRatios.put("Sure thing-Steal", 60.22);
        successFailureRatios.put("Piece of cake-Steal", 25.34);
        successFailureRatios.put("Piece of cake-Help", 23.35);
        successFailureRatios.put("Piece of cake-Escort", 22.22);
        successFailureRatios.put("Piece of cake-Rescue", 19.50);
        successFailureRatios.put("Piece of cake-Create", 18.48);
        successFailureRatios.put("Piece of cake-Investigate", 10.00);
        successFailureRatios.put("Walk in the park-Investigate", 8.60);
        successFailureRatios.put("Piece of cake-Infiltrate", 7.0);
        successFailureRatios.put("Walk in the park-Steal", 5.75);
        successFailureRatios.put("Walk in the park-Create", 5.61);
        successFailureRatios.put("Walk in the park-Escort", 5.58);
        successFailureRatios.put("Walk in the park-Help", 5.52);
        successFailureRatios.put("Walk in the park-Infiltrate", 5.25);
        successFailureRatios.put("Walk in the park-Rescue", 5.05);
        successFailureRatios.put("Quite likely-Steal", 3.28);
        successFailureRatios.put("Quite likely-Infiltrate", 3.25);
        successFailureRatios.put("Quite likely-Help", 3.05);
        successFailureRatios.put("Quite likely-Create", 2.99);
        successFailureRatios.put("Quite likely-Escort", 2.90);
        successFailureRatios.put("Quite likely-Rescue", 2.57);
        successFailureRatios.put("Hmmm....-Escort", 2.08);
        successFailureRatios.put("Playing with fire-Kill", 2.0);
        successFailureRatios.put("Hmmm....-Infiltrate", 1.95);
        successFailureRatios.put("Hmmm....-Steal", 1.93);
        successFailureRatios.put("Quite likely-Investigate", 1.90);
        successFailureRatios.put("Hmmm....-Help", 1.89);
        successFailureRatios.put("Hmmm....-Create", 1.86);
        successFailureRatios.put("Hmmm....-Rescue", 1.82);
        successFailureRatios.put("Hmmm....-Investigate", 1.66);
        successFailureRatios.put("Gamble-Rescue", 1.35);
        successFailureRatios.put("Gamble-Help", 1.24);
        successFailureRatios.put("Gamble-Escort", 1.22);
        successFailureRatios.put("Gamble-Create", 1.20);
        successFailureRatios.put("Gamble-Steal", 1.12);
        successFailureRatios.put("Gamble-Infiltrate", 1.04);
        successFailureRatios.put("Gamble-Investigate", 1.01);
        successFailureRatios.put("Risky-Kill", 1.00);
        successFailureRatios.put("Rather detrimental-Kill", 1.00);
        successFailureRatios.put("Risky-Rescue", 0.92);
        successFailureRatios.put("Risky-Infiltrate", 0.83);
        successFailureRatios.put("Risky-Create", 0.81);
        successFailureRatios.put("Risky-Investigate", 0.81);
        successFailureRatios.put("Risky-Help", 0.81);
        successFailureRatios.put("Risky-Steal", 0.78);
        successFailureRatios.put("Risky-Escort", 0.77);
        successFailureRatios.put("Rather detrimental-Steal", 0.6);
        successFailureRatios.put("Rather detrimental-Help", 0.57);
        successFailureRatios.put("Rather detrimental-Rescue", 0.54);
        successFailureRatios.put("Rather detrimental-Create", 0.53);
        successFailureRatios.put("Rather detrimental-Escort", 0.51);
        successFailureRatios.put("Rather detrimental-Investigate", 0.48);
        successFailureRatios.put("Rather detrimental-Infiltrate", 0.43);
        successFailureRatios.put("Playing with fire-Create", 0.40);
        successFailureRatios.put("Playing with fire-Rescue", 0.37);
        successFailureRatios.put("Playing with fire-Help", 0.36);
        successFailureRatios.put("Playing with fire-Escort", 0.34);
        successFailureRatios.put("Playing with fire-Steal", 0.28);
        successFailureRatios.put("Playing with fire-Investigate", 0.27);
        successFailureRatios.put("Playing with fire-Infiltrate", 0.25);
        successFailureRatios.put("Suicide mission-Rescue", 0.22);
        successFailureRatios.put("Suicide mission-Create", 0.16);
        successFailureRatios.put("Suicide mission-Escort", 0.15);
        successFailureRatios.put("Suicide mission-Infiltrate", 0.14);
        successFailureRatios.put("Suicide mission-Help", 0.14);
        successFailureRatios.put("Suicide mission-Steal", 0.12);
        successFailureRatios.put("Suicide mission-Investigate", 0.09);
        successFailureRatios.put("Suicide mission-Kill", 0.07);
        successFailureRatios.put("Impossible-Kill", 0.00);
        successFailureRatios.put("Gamble-Kill", 0.00);
        successFailureRatios.put("Impossible-Infiltrate", 0.00);
        successFailureRatios.put("Impossible-Investigate", 0.00);
    }

    public static Double estimateSuccess(Task task) {
        String action = task.firstMessageWord();
        if (action.equals("Steal") || action.equals("Kill")) return 0d;
        String key = task.probability() + "-" + action;
        return successFailureRatios.getOrDefault(key, 0d);
    }
}
