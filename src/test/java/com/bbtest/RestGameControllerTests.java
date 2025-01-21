package com.bbtest;

import com.bbtest.exceptions.GameOverException;
import com.bbtest.records.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.wiremock.spring.EnableWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = { "baseUrl=${wiremock.server.baseUrl}" })
@ActiveProfiles("test")
@EnableWireMock
public class RestGameControllerTests {

    @Autowired
    private RestGameController restGameController;

    private final String gameId = "aGame";
    private final String taskId = "aTask";
    private final String itemId = "anItem";

    @Test
    void startGame_ok() {
        StartGameResult expectedResult = new StartGameResult(gameId, 3, 0, 0, 0);
        stubFor(post("/game/start")
                .willReturn(jsonResponse(expectedResult, HttpStatus.OK.value())));
        StartGameResult result = restGameController.startGame();
        assertEquals(expectedResult, result);
    }

    @Test
    void listTasks_ok() {
        Task[] tasks = new Task[] {
                new Task(taskId, "Gamble", 7, 10, "Do something", null)
        };
        stubFor(get("/%s/messages".formatted(gameId))
                .willReturn(jsonResponse(tasks, HttpStatus.OK.value())));
        Task[] result = restGameController.listTasks(gameId);
        assertArrayEquals(tasks, result);
    }

    @Test
    void solveTask_ok() {
        SolveTaskResult expectedResult = new SolveTaskResult(true, 3, 10, 20, 2);
        stubFor(post("/%s/solve/%s".formatted(gameId, taskId))
                .willReturn(jsonResponse(expectedResult, HttpStatus.OK.value())));
        SolveTaskResult result = restGameController.solveTask(gameId, taskId);
        assertEquals(expectedResult, result);
    }

    @Test
    void listItems_ok() {
        Item[] items = new Item[] {
                new Item(itemId, "Great thing", 100)
        };
        stubFor(get("/%s/shop".formatted(gameId))
                .willReturn(jsonResponse(items, HttpStatus.OK.value())));
        Item[] result = restGameController.listItems(gameId);
        assertArrayEquals(items, result);
    }

    @Test
    void buyItem_ok() {
        BuyItemResult expectedResult = new BuyItemResult(true, 30, 2, 3);
        stubFor(post("/%s/shop/buy/%s".formatted(gameId, itemId))
                .willReturn(jsonResponse(expectedResult, HttpStatus.OK.value())));
        BuyItemResult result = restGameController.buyItem(gameId, itemId);
        assertEquals(expectedResult, result);
    }

    @Test
    void buyItem_gameOver() {
        stubFor(post("/%s/shop/buy/%s".formatted(gameId, itemId))
                .willReturn(jsonResponse("{\"status\":\"Game Over\"}", HttpStatus.GONE.value())));
        assertThrows(GameOverException.class, () -> restGameController.buyItem(gameId, itemId));
    }

    @Test
    void solveTask_badGateway() {
        stubFor(post("/%s/solve/%s".formatted(gameId, taskId))
                .willReturn(aResponse().withStatus(HttpStatus.BAD_GATEWAY.value())));
        assertThrows(HttpServerErrorException.BadGateway.class, () -> restGameController.solveTask(gameId, taskId));
    }

}
