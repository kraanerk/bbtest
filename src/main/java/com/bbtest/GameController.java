package com.bbtest;

import com.bbtest.records.*;

public interface GameController {

    StartGameResult startGame();

    Task[] listTasks(String gameId);

    SolveTaskResult solveTask(String gameId, String taskId);

    Item[] listItems(String gameId);

    BuyItemResult buyItem(String gameId, String itemId);

}
