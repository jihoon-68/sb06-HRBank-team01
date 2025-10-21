package com.sprint.hrbank_sb6_1.controller;

import com.sprint.hrbank_sb6_1.service.FileService;
import com.sprint.hrbank_sb6_1.service.storage.FileStorage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import java.io.IOException;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@ResponseBody
@RequestMapping("/api/files")
public class FileController {

  private final FileService fileService;
  private final FileStorage fileStorage;

  @GetMapping("/{id}/download")
  public ResponseEntity<?> download(@PathVariable("id") Long id) {
    return fileStorage.getFile(id);
  }
}
