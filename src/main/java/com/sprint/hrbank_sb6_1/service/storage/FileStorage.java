package com.sprint.hrbank_sb6_1.service.storage;

import java.io.InputStream;
import java.util.UUID;

/*
요구사항은 로컬에 저장하는 경우이나, 추후 클라우드로 확장 될 수도 있기에 따로 인터페이스를 생성
*/

public interface FileStorage {
  void putFile(byte[] data, String fileName);
  InputStream getFile(String fileName);

}
