package com.bbtest;

import com.bbtest.exceptions.GameOverException;
import com.bbtest.records.*;
import com.bbtest.utils.Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static com.bbtest.records.Item.HEALING_POTION;

public class Game implements Callable<GameResult> {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);
    private static final AtomicInteger GAME_NR = new AtomicInteger();

    static final int DESIRED_NR_OF_LIVES = 4;

    private final GameController gameController;

    private String gameId;
    String getGameId() { return gameId; }

    private int lives;
    int getLives() { return lives; }

    private int score;
    int getScore() { return score; }

    private int gold;
    int getGold() { return gold; }

    private int turn;
    int getTurn() { return turn; }

    private Item healingPotion;
    Item getHealingPotion() { return healingPotion; }

    private List<Item> availableItemsSortedByPriceAsc = new ArrayList<>();
    List<Item> getAvailableItems() { return availableItemsSortedByPriceAsc; }

    private final List<Task> succeededTasks = new LinkedList<>();
    List<Task> getSucceededTasks() { return succeededTasks; }

    private final List<Task> failedTasks = new LinkedList<>();
    List<Task> getFailedTasks() { return failedTasks; }

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
                if (task == null) {
                    break; // all available tasks use unknown encryption :(
                }
                solve(task);

                Item item = pickAnItemForPurchase();
                if (item != null) {
                    buy(item);
                }
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

    void updateGameStatus(int lives, int gold, int turn, int score) {
        this.lives = lives;
        this.gold = gold;
        this.turn = turn;
        this.score = score;
        LOG.debug("Turn: {}, Lives: {}, Score: {}, Gold: {}", turn, lives, score, gold);
    }

    void startGame() {
        StartGameResult startGameResult = gameController.startGame();
        LOG.debug("{}", startGameResult);
        gameId = startGameResult.gameId();
        updateGameStatus(
                startGameResult.lives(),
                startGameResult.gold(),
                startGameResult.turn(),
                startGameResult.score()
        );
    }

    void loadAvailableItems() {
        List<Item> items = new ArrayList<>(Arrays.asList(gameController.listItems(gameId)));
        // 'Healing potion' is kept separately, as we are buying it several times
        healingPotion = items.stream().filter(i -> i.name().equals(HEALING_POTION)).findFirst().get();
        items.remove(healingPotion);
        items.sort(Comparator.comparing(Item::cost));
        items.forEach(i -> LOG.debug("{}", i));
        availableItemsSortedByPriceAsc = items;
    }

    Task pickNextTask() {
        List<Task> tasks = new ArrayList<>(Arrays.stream(gameController.listTasks(gameId))
                .map(Decoder::decryptIfPossible)
                .filter(t -> t.encrypted() == null) // cannot solve tasks which we haven't decrypted
                .toList());

        sortTasks(tasks);

        if (tasks.isEmpty()) return null;
        return tasks.get(0);
    }

    void sortTasks(List<Task> tasks) {
        tasks.sort(
                Comparator.comparing(Task::successEstimate)
                        .thenComparing(Task::reward)
                        .reversed()
        );
        tasks.forEach(t -> LOG.debug("{}", t));
    }

    void solve(Task task) {
        SolveTaskResult result = gameController.solveTask(gameId, task.adId());
        updateGameStatus(
                result.lives(),
                result.gold(),
                result.turn(),
                result.score()
        );
        if (result.success()) {
            LOG.debug("Solved {}", task);
            succeededTasks.add(task);
        } else {
            LOG.debug("Failed to solve {}", task);
            failedTasks.add(task);
        }
    }

    Item pickAnItemForPurchase() {
        if (lives < 1) {
            return null; // game over
        }

        Item item;
        if (lives < DESIRED_NR_OF_LIVES) {
            // priority is to have at least DESIRED_NR_OF_LIVES lives remaining
            item = healingPotion;
        } else {
            item = availableItemsSortedByPriceAsc.isEmpty() ? null :
                    availableItemsSortedByPriceAsc.get(0);
        }
        if (item != null && gold < item.cost()) {
            return null; // not enough gold
        }
        return item;
    }

    void buy(Item item) {
        BuyItemResult result = gameController.buyItem(gameId, item.id());
        updateGameStatus(
                result.lives(),
                result.gold(),
                result.turn(),
                score
        );
        if (result.shoppingSuccess()) {
            availableItemsSortedByPriceAsc.remove(item);
            LOG.debug("Bought {}", item);
        } else {
            LOG.debug("Failed to buy {}", item);
        }
    }

}
