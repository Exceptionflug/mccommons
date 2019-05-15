package de.exceptionflug.mccommons.config.remote.server.controllers;

import de.exceptionflug.mccommons.config.remote.model.ConfigData;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/config")
public interface ConfigRestController {

    @GetMapping
    ConfigData getConfig(@RequestParam("path") final String path) throws IOException;

    @PutMapping
    void update(@RequestParam("path") final String path, @RequestBody final ConfigData configData) throws IOException;

}
