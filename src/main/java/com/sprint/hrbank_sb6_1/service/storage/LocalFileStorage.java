package com.sprint.hrbank_sb6_1.service.storage;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalFileStorage implements FileStorage{

  private final Path rootPath;

  public LocalFileStorage(@Value("${spring.hrbank.storage.local.root-path}") String path)
  {
    rootPath = Paths.get(System.getProperty("user.dir"),path);
    init();
  }

  private void init()
  {
    try {
      if(Files.exists(rootPath))
        return;

      Files.createDirectories(rootPath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Long putFile(byte[] data, String fileExtension) {
    return 0L;
  }

  @Override
  public InputStream getFile(Long id) {
    try
    {
       FileInputStream fs = new FileInputStream(rootPath.resolve(id.toString()).toFile());
      BufferedInputStream bis = new BufferedInputStream(fs);
      return bis;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
