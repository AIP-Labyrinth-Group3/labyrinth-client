package com.uni.gameclient.rest.service;
import com.uni.gameclient.game.database.Gameserver;
import com.uni.gameclient.rest.model.PostSend;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class Gameserverservice {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String BASE_URL = "http://localhost:4711/servers";
    // Alle Einträge holen
    public List<Gameserver> getAllGameserver() {
        try {
            Gameserver[] gameservers = restTemplate.getForObject(
                    BASE_URL,
                    Gameserver[].class);
            return Arrays.asList(gameservers);
        }  catch (Exception e) {

            return Arrays.asList();
        }
    }

    // Einzelnen Eintrag holen
    public Gameserver getGameserverById(Long id) {
        return restTemplate.getForObject(
                BASE_URL  + "/" + id,
                Gameserver.class
        );
    }

    // Neuen Eintrag anlegen
    public Gameserver createGameserver(PostSend newPost) {
        return restTemplate.postForObject(
                BASE_URL, newPost, Gameserver.class
        );
    }

    // Eintrag updaten
    public void updateGameserver(Long id, Gameserver updatedGameserver) {
        restTemplate.put(
                BASE_URL  + "/" + id,
                updatedGameserver
        );
    }

    // Eintrag löschen
    public void deleteGameserver(long id) {
        restTemplate.delete(
                BASE_URL + "/" + id
        );
    }


}
