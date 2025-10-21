package com.sprint.hrbank_sb6_1.service.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
  public void putFile(byte[] data, String fileName)
  {
    Path path = rootPath.resolve(fileName);
    try(FileOutputStream fos = new FileOutputStream(path.toFile());
        BufferedOutputStream bos = new BufferedOutputStream(fos))
    {
      bos.write(data);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public InputStream getFile(String fileName) {
    try
    {
       FileInputStream fs = new FileInputStream(rootPath.resolve(fileName).toFile());
      return new BufferedInputStream(fs);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
