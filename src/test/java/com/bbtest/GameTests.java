package com.bbtest;

import com.bbtest.exceptions.AllTasksUseUnknownEncryptionException;
import com.bbtest.exceptions.GameOverException;
import com.bbtest.exceptions.NoTasksAvailableException;
import com.bbtest.records.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bbtest.Game.DESIRED_NR_OF_LIVES;
import static com.bbtest.records.Item.HEALING_POTION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameTests {

    @Mock
    private GameController gameController;

    private Game game;

    @BeforeEach
    void init() {
        game = new Game(gameController);
    }

    @Test
    void startGame() {
        StartGameResult startGameResult = new StartGameResult(
                "gameId", 4, 3, 2, 1
        );
        when(gameController.startGame())
                .thenReturn(startGameResult);

        game.startGame();

        assertEquals(startGameResult.gameId(), game.getGameId());
        assertEquals(startGameResult.lives(), game.getLives());
        assertEquals(startGameResult.gold(), game.getGold());
        assertEquals(startGameResult.score(), game.getScore());
        assertEquals(startGameResult.turn(), game.getTurn());
    }

    @Test
    void loadAvailableItems() {
        Item healingPotion = new Item("hpot", HEALING_POTION, 50);
        Item[] items = new Item[] {
                new Item("id1", "name1", 100),
                new Item("id2", "name2", 300),
                healingPotion
        };
        when(gameController.listItems(game.getGameId()))
                .thenReturn(items);

        game.loadAvailableItems();
        List<Item> availableItems = game.getAvailableItems();

        assertEquals(healingPotion, game.getHealingPotion());
        assertEquals(100, availableItems.get(0).cost());
        assertEquals(300, availableItems.get(1).cost());
    }

    @Test
    void pickAnItemForPurchase_noLivesRemaining() {
        loadAvailableItems();
        game.updateGameStatus(0, 0, 0, 0);

        Item pickedItem = game.pickAnItemForPurchase();

        assertNull(pickedItem);
    }

    @Test
    void pickAnItemForPurchase_lessThanDesiredLivesRemaining_haveEnoughGold() {
        loadAvailableItems();
        Item healingPotion = game.getHealingPotion();
        game.updateGameStatus(DESIRED_NR_OF_LIVES - 1, healingPotion.cost(), 0, 0);

        Item pickedItem = game.pickAnItemForPurchase();

        assertEquals(healingPotion, pickedItem);
    }

    @Test
    void pickAnItemForPurchase_lessThanDesiredLivesRemaining_notEnoughGold() {
        loadAvailableItems();
        Item healingPotion = game.getHealingPotion();
        game.updateGameStatus(DESIRED_NR_OF_LIVES - 1, healingPotion.cost() - 1, 0, 0);

        Item pickedItem = game.pickAnItemForPurchase();

        assertNull(pickedItem);
    }

    @Test
    void pickAnItemForPurchase_desiredLivesRemaining_haveEnoughGold() {
        loadAvailableItems();
        Item expectedItem = game.getAvailableItems().get(0);
        game.updateGameStatus(DESIRED_NR_OF_LIVES, expectedItem.cost(), 0, 0);

        Item pickedItem = game.pickAnItemForPurchase();

        assertEquals(expectedItem, pickedItem);
    }

    @Test
    void buyItem_healingPotion_success() {
        loadAvailableItems();
        List<Item> availableItemsBeforePurchace = new ArrayList<>(game.getAvailableItems());
        Item item = game.getHealingPotion();
        BuyItemResult buyItemResult = new BuyItemResult(true, 10, 2, 5);
        when(gameController.buyItem(game.getGameId(), item.id()))
                .thenReturn(buyItemResult);

        game.buy(item);

        assertEquals(buyItemResult.lives(), game.getLives());
        assertEquals(buyItemResult.gold(), game.getGold());
        assertEquals(buyItemResult.turn(), game.getTurn());
        assertArrayEquals(availableItemsBeforePurchace.toArray(), game.getAvailableItems().toArray());
    }

    @Test
    void buyItem_notHealingPotion_success() {
        loadAvailableItems();
        List<Item> availableItems = new ArrayList<>(game.getAvailableItems());
        Item item = availableItems.get(0);
        BuyItemResult buyItemResult = new BuyItemResult(true, 10, 2, 5);
        when(gameController.buyItem(game.getGameId(), item.id()))
                .thenReturn(buyItemResult);

        game.buy(item);

        assertEquals(buyItemResult.lives(), game.getLives());
        assertEquals(buyItemResult.gold(), game.getGold());
        assertEquals(buyItemResult.turn(), game.getTurn());
        availableItems.remove(item);
        assertArrayEquals(availableItems.toArray(), game.getAvailableItems().toArray());
    }

    @Test
    void buyItem_notHealingPotion_failure() {
        loadAvailableItems();
        List<Item> availableItems = new ArrayList<>(game.getAvailableItems());
        Item item = availableItems.get(0);
        BuyItemResult buyItemResult = new BuyItemResult(false, 10, 2, 5);
        when(gameController.buyItem(game.getGameId(), item.id()))
                .thenReturn(buyItemResult);

        game.buy(item);

        assertEquals(buyItemResult.lives(), game.getLives());
        assertEquals(buyItemResult.gold(), game.getGold());
        assertEquals(buyItemResult.turn(), game.getTurn());
        assertArrayEquals(availableItems.toArray(), game.getAvailableItems().toArray());
    }

    @Test
    void pickAnItemForPurchase_desiredLivesRemaining_noItemsAvailable() {
        game.updateGameStatus(DESIRED_NR_OF_LIVES, 1000, 0, 0);

        Item pickedItem = game.pickAnItemForPurchase();

        assertNull(pickedItem);
    }

    @Test
    void pickNextTask_noTasksAvailable() {
        when(gameController.listTasks(game.getGameId()))
                .thenReturn(new Task[]{});

        assertThrows(NoTasksAvailableException.class, () -> game.pickNextTask());
    }

    @Test
    void pickNextTask_allTasksUseUnknownEncryption() {
        Task[] tasks = new Task[] {
                new Task("task1", "Gamble", 7, 10, "Do something", (byte) 5),
                new Task("task2", "Risky", 6, 9, "Do something", (byte) 5)
        };
        when(gameController.listTasks(game.getGameId()))
                .thenReturn(tasks);

        assertThrows(AllTasksUseUnknownEncryptionException.class, () -> game.pickNextTask());
    }

    @Test
    void sortTasks() {
        List<Task> tasks = Arrays.asList(
                new Task("task1", "Gamble", 7, 10, "Escort ...", null),
                new Task("task2", "Sure thing", 6, 9, "Help ...", null),
                new Task("task3", "Sure thing", 6, 100, "Steal ...", null),
                new Task("task4", "Sure thing", 6, 200, "Kill ...", null),
                new Task("task5", "Sure thing", 6, 10, "Help ...", null)
        );
        game.sortTasks(tasks);

        assertEquals("task5", tasks.get(0).adId());
        assertEquals("task2", tasks.get(1).adId());
        assertEquals("task1", tasks.get(2).adId());
        assertEquals("task4", tasks.get(3).adId());
        assertEquals("task3", tasks.get(4).adId());
    }

    @Test
    void pickNextTask() {
        Task[] tasks = new Task[] {
                new Task("task1", "Gamble", 7, 10, "Escort ...", null),
                new Task("task2", "Sure thing", 6, 9, "Help ...", null),
                new Task("task3", "Sure thing", 6, 100, "Steal ...", null),
                new Task("task3", "Sure thing", 6, 200, "Kill ...", null),
                new Task("task2", "Sure thing", 6, 10, "Help ...", null)
        };
        when(gameController.listTasks(game.getGameId()))
                .thenReturn(tasks);

        Task task = game.pickNextTask();

        assertEquals(tasks[4], task);
    }

    @Test
    void solveTask_success() {
        Task task = new Task("taskId", "Sure thing", 6, 9, "Help ...", null);
        SolveTaskResult solveTaskResult = new SolveTaskResult(true, 3, 10, 20, 2);
        when(gameController.solveTask(game.getGameId(), task.adId()))
                .thenReturn(solveTaskResult);

        game.solve(task);

        assertEquals(solveTaskResult.lives(), game.getLives());
        assertEquals(solveTaskResult.gold(), game.getGold());
        assertEquals(solveTaskResult.turn(), game.getTurn());
        assertEquals(solveTaskResult.score(), game.getScore());
        assertTrue(game.getSucceededTasks().contains(task));
    }

    @Test
    void solveTask_failure() {
        Task task = new Task("taskId", "Sure thing", 6, 9, "Help ...", null);
        SolveTaskResult solveTaskResult = new SolveTaskResult(false, 3, 10, 20, 2);
        when(gameController.solveTask(game.getGameId(), task.adId()))
                .thenReturn(solveTaskResult);

        game.solve(task);

        assertEquals(solveTaskResult.lives(), game.getLives());
        assertEquals(solveTaskResult.gold(), game.getGold());
        assertEquals(solveTaskResult.turn(), game.getTurn());
        assertEquals(solveTaskResult.score(), game.getScore());
        assertTrue(game.getFailedTasks().contains(task));
    }

    @Test
    void playGame_startingWithZeroLives() {
        StartGameResult startGameResult = new StartGameResult(
                "gameId", 0, 0, 0, 0
        );
        when(gameController.startGame())
                .thenReturn(startGameResult);
        when(gameController.listItems(startGameResult.gameId()))
                .thenReturn(new Item[] {
                        new Item("hpot", HEALING_POTION, 50)
                });

        GameResult gameResult = game.playGame();

        assertEquals(startGameResult.gameId(), gameResult.gameId());
        assertEquals(startGameResult.lives(), gameResult.lives());
        assertEquals(startGameResult.gold(), gameResult.gold());
        assertEquals(startGameResult.score(), gameResult.score());
        assertEquals(startGameResult.turn(), gameResult.turn());
        assertNull(gameResult.exception());
    }

    @Test
    void playGame_encounterNoTasksAvailableException() {
        StartGameResult startGameResult = new StartGameResult(
                "gameId", 1, 0, 0, 0
        );
        when(gameController.startGame())
                .thenReturn(startGameResult);
        when(gameController.listItems(startGameResult.gameId()))
                .thenReturn(new Item[] {
                        new Item("hpot", HEALING_POTION, 50)
                });
        when(gameController.listTasks(startGameResult.gameId()))
                .thenReturn(new Task[]{});

        GameResult gameResult = game.playGame();

        assertEquals(startGameResult.gameId(), gameResult.gameId());
        assertEquals(startGameResult.lives(), gameResult.lives());
        assertEquals(startGameResult.gold(), gameResult.gold());
        assertEquals(startGameResult.score(), gameResult.score());
        assertEquals(startGameResult.turn(), gameResult.turn());
        assertEquals(NoTasksAvailableException.class, gameResult.exception().getClass());
    }

    @Test
    void playGame_encounterGameOverException() {
        StartGameResult startGameResult = new StartGameResult(
                "gameId", 1, 0, 0, 0
        );
        when(gameController.startGame())
                .thenReturn(startGameResult);
        when(gameController.listItems(startGameResult.gameId()))
                .thenReturn(new Item[] {
                        new Item("hpot", HEALING_POTION, 50)
                });
        when(gameController.listTasks(startGameResult.gameId()))
                .thenThrow(new GameOverException());

        GameResult gameResult = game.playGame();

        assertEquals(startGameResult.gameId(), gameResult.gameId());
        assertEquals(startGameResult.lives(), gameResult.lives());
        assertEquals(startGameResult.gold(), gameResult.gold());
        assertEquals(startGameResult.score(), gameResult.score());
        assertEquals(startGameResult.turn(), gameResult.turn());
        assertNull(gameResult.exception());
    }

    @Test
    void playGame_solveTaskAndBuyItem() {
        StartGameResult startGameResult = new StartGameResult(
                "gameId", 1, 0, 0, 0
        );
        when(gameController.startGame())
                .thenReturn(startGameResult);

        Item item = new Item("hpot", HEALING_POTION, 50);
        when(gameController.listItems(startGameResult.gameId()))
                .thenReturn(new Item[] { item });

        Task task = new Task("taskId", "Sure thing", 6, 9, "Help ...", null);
        when(gameController.listTasks(startGameResult.gameId()))
                .thenReturn(new Task[] { task });

        SolveTaskResult solveTaskResult = new SolveTaskResult(true, 1, item.cost(), 20, 1);
        when(gameController.solveTask(startGameResult.gameId(), task.adId()))
                .thenReturn(solveTaskResult);

        BuyItemResult buyItemResult = new BuyItemResult(false, 10, 0, 2);
        when(gameController.buyItem(startGameResult.gameId(), item.id()))
                .thenReturn(buyItemResult);

        GameResult gameResult = game.playGame();

        assertEquals(startGameResult.gameId(), gameResult.gameId());
        assertEquals(buyItemResult.lives(), gameResult.lives());
        assertEquals(buyItemResult.gold(), gameResult.gold());
        assertEquals(solveTaskResult.score(), gameResult.score());
        assertEquals(buyItemResult.turn(), gameResult.turn());
        assertNull(gameResult.exception());
    }

}
