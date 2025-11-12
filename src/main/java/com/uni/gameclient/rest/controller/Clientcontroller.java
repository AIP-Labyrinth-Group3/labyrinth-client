package com.uni.gameclient.rest.controller;
import com.uni.gameclient.game.database.Gameserver;
import com.uni.gameclient.rest.model.PostSend;
import com.uni.gameclient.rest.service.Gameserverservice;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servers")
public class Clientcontroller {

    private final Gameserverservice gameserverservice;

    public Clientcontroller(Gameserverservice postService) {
        this.gameserverservice = postService;
    }

    @GetMapping("/")
    public List<Gameserver> getAllGameserver() {
        return gameserverservice.getAllGameserver();
    }

    @GetMapping("/{id}")
    public Gameserver getGameserver(@PathVariable Long id) {
        return gameserverservice.getGameserverById(id);
    }
    @PostMapping("/")
    public Gameserver createGameserver(@RequestBody PostSend newPost) {
        return gameserverservice.createGameserver(newPost);
    }

    @PutMapping("/{id}")
    public void updateGameserver(@PathVariable("id") long id, @RequestBody Gameserver updatedGameserver) {
        gameserverservice.updateGameserver(id, updatedGameserver);
    }

    @DeleteMapping("/{id}")
    public void deleteGameserver(@PathVariable("id") long id) {
        gameserverservice.deleteGameserver(id);
    }


}
