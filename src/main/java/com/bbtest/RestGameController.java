package com.bbtest;

import com.bbtest.exceptions.GameOverException;
import com.bbtest.records.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Controller
public class RestGameController implements GameController {

    @Value("${baseUrl}")
    private String baseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public StartGameResult startGame() {
        return restTemplate.postForObject(baseUrl + "/game/start", null, StartGameResult.class);
    }

    @Override
    public Task[] listTasks(String gameId) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + gameId + "/messages", Task[].class);
        } catch (RuntimeException e) {
            throw handleException(e);
        }
    }

    @Override
    public SolveTaskResult solveTask(String gameId, String taskId) {
        try {
            return restTemplate.postForObject(baseUrl + "/" + gameId + "/solve/" + taskId, null, SolveTaskResult.class);
        } catch (RuntimeException e) {
            throw handleException(e);
        }
    }

    @Override
    public Item[] listItems(String gameId) {
        try {
            return restTemplate.getForObject(baseUrl + "/" + gameId + "/shop", Item[].class);
        } catch (RuntimeException e) {
            throw handleException(e);
        }
    }

    @Override
    public BuyItemResult buyItem(String gameId, String itemId) {
        try {
            return restTemplate.postForObject(baseUrl + "/" + gameId + "/shop/buy/" + itemId, null, BuyItemResult.class);
        } catch (RuntimeException e) {
            throw handleException(e);
        }
    }

    private RuntimeException handleException(RuntimeException e) {
        if (e instanceof HttpClientErrorException.Gone) {
            return new GameOverException();
        }
        return e;
    }
}
