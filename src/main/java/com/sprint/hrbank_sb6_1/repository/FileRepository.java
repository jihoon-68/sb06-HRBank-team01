package com.sprint.hrbank_sb6_1.repository;

import com.sprint.hrbank_sb6_1.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface FileRepository extends JpaRepository<File,Long>{

}
