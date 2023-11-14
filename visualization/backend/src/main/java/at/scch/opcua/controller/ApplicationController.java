package at.scch.opcua.controller;

import at.scch.opcua.config.NodeDocConfiguration;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin
public class ApplicationController {
    @Value("${application.version}")
    private String version;

    @Autowired
    private NodeDocConfiguration config;

    @ApiOperation(value = "Gets the current version of the application")
    @GetMapping("/version")
    public ResponseEntity getApplicationVersion() {
        return ResponseEntity.ok(version);
    }

    @ApiOperation(value = "Gets the current message of the day")
    @GetMapping("/motd")
    public ResponseEntity<String> getMotd() {
        return ResponseEntity.ok(config.getMotd());
    }

    @ApiOperation(value = "Returns if demo mode is enabled")
    @GetMapping("/demo")
    public ResponseEntity<Boolean> isDemoEnabled() {
        return ResponseEntity.ok(config.isDemo());
    }


}
