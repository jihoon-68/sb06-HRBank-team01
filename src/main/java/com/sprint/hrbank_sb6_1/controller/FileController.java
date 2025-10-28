package com.sprint.hrbank_sb6_1.controller;

import com.sprint.hrbank_sb6_1.dto.BinaryContentCreateRequest;
import com.sprint.hrbank_sb6_1.service.FileService;
import com.sprint.hrbank_sb6_1.service.storage.FileStorage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.server.PathParam;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

  private final FileService fileService;

  @GetMapping("/{id}/download")
  public ResponseEntity download(@PathVariable("id") Long id) throws NotFoundException {
    return fileService.getFile(id);
  }

//  @DeleteMapping("/{id}")
//  public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
//    fileService.deleteFile(id);
//    return ResponseEntity.noContent().build();
//  }
//
//  @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//  public ResponseEntity<Long> uploadTest(@RequestPart(value = "profile") MultipartFile profile)
//      throws IOException {
//
//    Optional<BinaryContentCreateRequest> profileRequest = Optional.ofNullable(profile)
//        .flatMap(x -> {
//          try {
//            return Optional.of(new BinaryContentCreateRequest(x.getName()
//                ,x.getContentType()
//                ,x.getBytes()));
//          } catch (IOException e) {
//            throw new RuntimeException(e);
//          }
//        });
//
//    return ResponseEntity.status(HttpStatus.OK)
//        .body(fileService.putFile(profileRequest.orElse(null)));
//  }
}
