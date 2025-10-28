# team01

### 🔗 [Noition Link](https://www.notion.so/Home-2928fadfac9e80709f68c08bff7cd3ed?pvs=21)

## **팀원 구성**

- 이현욱 ( https://github.com/hyunwook13 )
- 박종건 ( [https://github.com/3Park](https://github.com/3Park/) )
- 박지훈 ( https://github.com/jihoon-68 )
- 정진혁 ( https://github.com/Jinhyeok22 )
- 이진우 ( https://github.com/jionu102 )
- 김태헌 ( https://github.com/Taehun88 )

---

## **프로젝트 소개**

- 인사관리 시스템의 Spring 백엔드 시스템 구축
- 프로젝트 기간: 2025.10.20 ~ 2025.10.28

---

## **기술 스택**

- Backend: Spring Boot, Spring Data JPA, Lombok, MapStruct
- Database: PostgreSQL
- Deploy: Railway.io
- 공통 Tool: Git & Github, Discord

---

## **팀원별 구현 기능 상세**

### 이현욱

- 부서 추가 기능
    - 부서명, 부서 설립일자, 설명을 받아 부서를 생성하는 기능
- 부서 관련 기능
    - 부서 수정, 삭제 기능
- 부서 검색 기능
    - 페이징, 커서를 활용한 검색 기능

### 박종건

- 파일 저장 기능
    - 프로필 파일 등 로컬에 파일 저장 하는 기능
- 파일 다운로드 기능
    - 파일 id 를 통한 파일 다운로드 기능
- 파일 삭제 기능
    - 파일 id를 통한 파일 삭제 기능
- Global Exception 처리
    - RestControllerAdvice 를 통한 global exception 처리

### 박지훈

- 백업 생성 기능
    - 최근 성공한 백업의 백업 시작 시간 기준으로 새로운 유저 수정 로그가 특정 개숫 이상이면 백업 파일을 저장 하는 서비스에 이벤트 메시지를 보내서 백업 파일 저장 시키는 기능을 합니다
- 백업 결과 저장
    - 백업 파일 생성 에서 백업 파일 데이터가 있는 이벤트 메세지를 받습니다.
    - 에러 여부애따라 백업 상태(성공/실패)를 변경하고 백업객체 에 파일 객체 추가 합니다.
    - 백업 객체와 파일 객체를 DB에 저장합니다.
- 백업 파일 백업 .csv 파일 기능
    - 백업 생성으로 부터 이벤트 메시지가 오면 비동기적으로 작업을 시작합니다.
    - 사원DB에 사원 데이터 전체를 Stream<Employee>로 불러와 로컬 백업 파일에 하나씩 저장 합니다.
    - 작업이 성공하면 백업 서비스 백업 결과 저장 메소드로 이벤트 메시지에 백업 객체 와 저장 파일 데이터를 넣어 전송합니다.
    - 작업이 실패하면 백업 서비스 백업 결과 저장 메소드로 이벤트 메시지에 백업 객체 와 에러 로그 파일 데이터를 넣어 전송합니다.

### 정진혁

- 백업 스케줄링 기능
- 설정된 cron 주기에 따라 자동으로 백업 서비스가 실행되도록 스케줄러 구성
- 백업 트리거 역할을 수행하며, 실제 파일 저장은 이벤트 기반으로 별도 처리.
- 백업 목록 조회 기능
- QueryDSL 기반으로 검색 조건에 따라 동적으로 쿼리 구성
- 맵퍼를 통해 엔티티를 DTO 변환 후 API 응답으로 반환
- 커서 기반 페이징 메타데이터를 함께 전달하여 프론트 측 무한스크롤 UI 지원
- 최신 백업 조회 기능
- 특정 상태에 따른 가장 최근 백업 하나만 조회하는 API 제공
- 페이지에서 최근 백업 결과를 빠르게 확인할 수 있도록 설계

### 김태헌

- 직원 생성 수정 이력 관리 기능
    - 수정된 이력을 Query에 맞춰서 response 하는 기능
- 직원 생성 수정 상세 이력
    - 수정된 내용의 상세 내용을 불러오는 기능
- Entity 기본 설계
    - Database 기초 설계 및 Entity 정의

### 이진우

- 직원 목록 조회
    - 커서 페이지네이션을 적용한 직원 목록 조회 API 구현
- 직원 등록
    - 요청받은 직원 정보(email, 직함 등)를 저장하는 API 구현
- 직원 상세 조회
    - 직원 id를 받아 직원 정보를 반환하는 API 구현
- 직원 삭제
    - 직원Id를 받아 프로필 파일과 함께 해당 직원을 삭제하는 API 구현
- 직원 수정
    - 직원 번호와 기본키를 제외한 나머지 정보를 입력받아 직원 정보를 수정하는 API 구현
- 직원 수 추이 조회
    - 시작 일시, 종료 일시, 시간 단위를 받아 시간 단위 별 직원 수, 날짜, 이전 시점 대비 증감과 증감률을 반환하는 API 구현
- 직원 분포 조회
    - 그룹화 기준과 직원 상태를 받아 그룹별 분류, 직원 수, 비율을 반환하는 API 구현
- 직원 수 조회
    - 직원 상태(재직중, 휴직중, 퇴사) , 입사일의 범위를 입력받아 직원 수를 반환하는 API 구현

---

## **파일 구조**

```
📦 sb06-HRBank
├─ .DS_Store
├─ .gitattributes
├─ .gitignore
├─ build.gradle
├─ docker-compose.yaml
├─ gradle
│  └─ wrapper
│     ├─ gradle-wrapper.jar
│     └─ gradle-wrapper.properties
├─ gradlew
├─ gradlew.bat
├─ settings.gradle
└─ src
   ├─ main
   │  ├─ java
   │  │  └─ com
   │  │     └─ sprint
   │  │        └─ hrbank_sb6_1
   │  │           ├─ HRbankSb61Application.java
   │  │           ├─ common
   │  │           │  └─ init
   │  │           ├─ config
   │  │           │  ├─ QueryDslConfig.java
   │  │           │  ├─ SchedulerConfig.java
   │  │           │  └─ init
   │  │           ├─ controller
   │  │           │  ├─ BackupController.java
   │  │           │  ├─ ChangeLogController.java
   │  │           │  ├─ DepartmentController.java
   │  │           │  ├─ EmployeeController.java
   │  │           │  └─ FileController.java
   │  │           ├─ domain
   │  │           │  ├─ Backup.java
   │  │           │  ├─ BackupStatus.java
   │  │           │  ├─ ChangeDiff.java
   │  │           │  ├─ ChangeLog.java
   │  │           │  ├─ ChangeLogStatus.java
   │  │           │  ├─ Department.java
   │  │           │  ├─ Employee.java
   │  │           │  ├─ EmployeeStatus.java
   │  │           │  └─ File.java
   │  │           ├─ dto
   │  │           │  ├─ BackupDto.java
   │  │           │  ├─ BinaryContentCreateRequest.java
   │  │           │  ├─ ChangeLogDto.java
   │  │           │  ├─ CursorPageBackupDto.java
   │  │           │  ├─ CursorPageResponse.java
   │  │           │  ├─ CursorPageResponseBackupDto.java
   │  │           │  ├─ CursorPageResponseChangeLogDto.java
   │  │           │  ├─ DepartmentCreateRequest.java
   │  │           │  ├─ DepartmentResponse.java
   │  │           │  ├─ DepartmentSearchCond.java
   │  │           │  ├─ DepartmentSortBy.java
   │  │           │  ├─ DepartmentUpdateRequest.java
   │  │           │  ├─ DiffDto.java
   │  │           │  ├─ SortDirection.java
   │  │           │  ├─ data
   │  │           │  │  ├─ EmployeeDistributionDto.java
   │  │           │  │  ├─ EmployeeDto.java
   │  │           │  │  └─ EmployeeTrendDto.java
   │  │           │  └─ request
   │  │           │     ├─ EmployeeCreateRequest.java
   │  │           │     ├─ EmployeeFindAllRequest.java
   │  │           │     ├─ EmployeeUpdateRequest.java
   │  │           │     └─ SearchBackupRequest.java
   │  │           ├─ event
   │  │           │  ├─ BackupEvent.java
   │  │           │  └─ BackupIoEvent.java
   │  │           ├─ exception
   │  │           │  ├─ GlobalExceptionHandler.java
   │  │           │  └─ init
   │  │           ├─ mapper
   │  │           │  ├─ BackupMapper.java
   │  │           │  ├─ BackupPagingMapper.java
   │  │           │  └─ EmployeeMapper.java
   │  │           ├─ repository
   │  │           │  ├─ BackupRepository.java
   │  │           │  ├─ BackupRepositoryCustom.java
   │  │           │  ├─ BackupRepositoryImpl.java
   │  │           │  ├─ ChangeDiffRepository.java
   │  │           │  ├─ ChangeLogRepository.java
   │  │           │  ├─ ChangeLogRepositoryCustom.java
   │  │           │  ├─ ChangeLogRepositoryImpl.java
   │  │           │  ├─ DepartmentRepository.java
   │  │           │  ├─ EmployeeRepository.java
   │  │           │  ├─ EmployeeRepositoryCustom.java
   │  │           │  ├─ EmployeeRepositoryCustomImpl.java
   │  │           │  └─ FileRepository.java
   │  │           └─ service
   │  │              ├─ BackupIoService.java
   │  │              ├─ BackupService.java
   │  │              ├─ DepartmentService.java
   │  │              ├─ EmployeeService.java
   │  │              ├─ FileService.java
   │  │              ├─ basic
   │  │              │  ├─ BasicBackupIoService.java
   │  │              │  ├─ BasicBackupService.java
   │  │              │  └─ BasicEmployeeService.java
   │  │              ├─ changelog
   │  │              │  ├─ ChangeLogService.java
   │  │              │  └─ ChangeLogServiceImpl.java
   │  │              ├─ scheduler
   │  │              │  └─ BackupScheduler.java
   │  │              └─ storage
   │  │                 ├─ FileStorage.java
   │  │                 └─ LocalFileStorageImpl.java
   │  └─ resources
   │     ├─ application-prod.yaml
   │     ├─ application.yaml
   │     └─ static
   │        ├─ assets
   │        │  ├─ images
   │        │  │  └─ default-profile.svg
   │        │  └─ index-aNksrdbr.js
   │        ├─ favicon.ico
   │        └─ index.html
   └─ test
      └─ java
         └─ com
            └─ sprint
               └─ hrbank_sb6_1
                  └─ HRbankSb61ApplicationTests.java
```

---

## 구현 **홈페이지**

[구현 링크](http://sb06-hrbank-team01-production-6482.up.railway.app)

---

## **프로젝트 회고록**

([ 초급 프로젝트 발표](https://www.notion.so/2998fadfac9e80fc81b9fc2f9237f1ba?pvs=21))
