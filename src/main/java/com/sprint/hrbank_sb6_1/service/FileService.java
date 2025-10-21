package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.domain.File;
import com.sprint.hrbank_sb6_1.dto.BinaryContentCreateRequest;
import com.sprint.hrbank_sb6_1.repository.FileRepository;
import com.sprint.hrbank_sb6_1.service.storage.FileStorage;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileStorage fileStorage;
  private final FileRepository fileRepository;

  @Transactional
  public Long putFile(BinaryContentCreateRequest request) throws BadRequestException {

    if(request == null) {
      throw new BadRequestException("Bad Request. no parameters were provided.");
    }

    String[] extension = request.contentType().split("/");
    if(extension == null || extension.length != 2) {
      throw new BadRequestException("Bad Request Content Type : " + request.contentType());
    }

    String fileName = UUID.randomUUID() + "." + extension[1];

    fileStorage.putFile(request.bytes(), fileName);

    File file = new File();
    file.setName(fileName);
    file.setSize(request.bytes().length);
    file.setType(request.contentType());
    File createdFile = fileRepository.save(file);

    return createdFile.getId();
  }


  public ResponseEntity<Resource> getFile(Long id) throws NotFoundException {

    File file = fileRepository.findById(id).orElse(null);
    if(file == null) {
      throw new NotFoundException();
    }

    InputStream stream = fileStorage.getFile(file.getName());
    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(new InputStreamResource(stream));

  }
}
