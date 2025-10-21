package com.sprint.hrbank_sb6_1.service;

import com.sprint.hrbank_sb6_1.service.storage.FileStorage;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {

  private final FileStorage fileStorage;

  public InputStream getFile(Long id) {
    return fileStorage.getFile(id);
  }
}
