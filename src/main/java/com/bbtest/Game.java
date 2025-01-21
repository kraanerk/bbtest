package com.bbtest;

import com.bbtest.exceptions.GameOverException;
import com.bbtest.records.*;
import com.bbtest.utils.Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class Game implements Callable<GameResult> {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);
    private static final AtomicInteger GAME_NR = new AtomicInteger();

    private final GameController gameController;
    private String gameId;
    private int lives;
    int getLives() { return lives; }
    private int score;
    int getScore() { return score; }
    private int gold;
    int getGold() { return gold; }
    private int turn;
    int getTurn() { return turn; }
    private List<Item> availableItemsSortedByPriceAsc;
    List<Item> getAvailableItems() { return availableItemsSortedByPriceAsc; }
    private final List<Task> succeededTasks = new LinkedList<>();
    private final List<Task> failedTasks = new LinkedList<>();

    public Game(GameController gameController) {
        this.gameController = gameController;
    }

    @Override
    public GameResult call() {
        GameResult result;
        try {
            startGame();
            loadAvailableItems();

            while (lives > 0 ) {
                Task task = pickNextTask();
                if (task == null) break; // all available tasks are encrypted :(
                solve(task);
                buyAnItemIfPossible();
                LOG.debug("Turn: {}, Lives: {}, Score: {}, Gold: {}", turn, lives, score, gold);
            }

            result = new GameResult(GAME_NR.incrementAndGet(), gameId, score, turn, failedTasks, succeededTasks, null);
        } catch (GameOverException goe) {
            result = new GameResult(GAME_NR.incrementAndGet(), gameId, score, turn, failedTasks, succeededTasks, null);
        } catch (Throwable t) {
            result = new GameResult(GAME_NR.incrementAndGet(), gameId, score, turn, failedTasks, succeededTasks, t);
        }
        LOG.info("{}", result);
        return result;
    }

    void startGame() {
        StartGameResult startGameResult = gameController.startGame();
        LOG.debug("{}", startGameResult);
        gameId = startGameResult.gameId();
        lives = startGameResult.lives();
        score = startGameResult.score();
        gold = startGameResult.gold();
        turn = startGameResult.turn();
    }

    void loadAvailableItems() {
        List<Item> items = Arrays.asList(gameController.listItems(gameId));
        items.sort((a, b) -> a.cost().compareTo(b.cost()));
        items.forEach(i -> LOG.debug("{}", i));
        availableItemsSortedByPriceAsc = new ArrayList<>(items);
    }

    Task pickNextTask() {
        List<Task> tasks = new ArrayList<>(Arrays.stream(gameController.listTasks(gameId))
                .map(Decoder::decryptIfPossible)
                .filter(t -> t.encrypted() == null) // cannot solve tasks which we haven't decrypted
                .toList());

        tasks.sort((a, b) -> {
            int probabilityComparisonResult =
                    TaskSuccessEvaluator.estimateSuccess(a).compareTo(TaskSuccessEvaluator.estimateSuccess(b)) * -1;
            if (probabilityComparisonResult != 0) return probabilityComparisonResult;

            return a.reward().compareTo(b.reward()) * -1;
        });
        tasks.forEach(t -> LOG.debug("{}", t));

        if (tasks.isEmpty()) return null;
        return tasks.getFirst();
    }

    void solve(Task task) {
        SolveTaskResult result = gameController.solveTask(gameId, task.adId());
        lives = result.lives();
        gold = result.gold();
        turn = result.turn();
        score = result.score();
        if (result.success()) {
            LOG.debug("Solved {}", task);
            succeededTasks.add(task);
        } else {
            LOG.debug("Failed to solve {}", task);
            failedTasks.add(task);
        }
    }

    void buyAnItemIfPossible() {
        Item item = availableItemsSortedByPriceAsc.isEmpty() ? null :
                availableItemsSortedByPriceAsc.getFirst();
        if (lives > 0 && item != null && gold >= item.cost()) {
            BuyItemResult result = gameController.buyItem(gameId, item.id());
            availableItemsSortedByPriceAsc.removeFirst();
            lives = result.lives();
            gold = result.gold();
            turn = result.turn();
            if (result.shoppingSuccess()) {
                LOG.debug("Bought {}", item);
            } else {
                LOG.debug("Failed to buy {}", item);
            }
        }
    }

}
