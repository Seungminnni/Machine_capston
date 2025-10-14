## RotorHallSetupMac (app) — README

### 간단한 설명
- 이 디렉터리는 `RotorHallSetupMac` 데스크탑 GUI 애플리케이션의 소스와 빌드 산출물을 포함합니다.
- 메인 UI와 실시간 시리얼 데이터를 그리는 그래프/게이지를 포함한 Java Swing 애플리케이션입니다.
- 애플리케이션의 목적: 로터 홀 센서 설정 및 실시간 모니터링. 사용자가 반경, 경고/위험 임계값, 네트워크 연결, 포트 이름, 두께-전압 관계 공식을 설정하고, 시리얼 포트를 통해 데이터를 받아 그래프와 게이지로 표시합니다.
- 주요 기능: 설정 UI, 실시간 그래프 (전압-시간, 두께-시간), 트래픽 라이트 (경고/위험 표시), 게이지 (충전률 표시), 시뮬레이션 모드 지원.

### 주요 파일(요약)
- `RotorHallSetupMac.java` — 메인 소스 파일 (GUI, 그래프, 시리얼 리더 포함). 기본 패키지에 정의되어 있습니다. 이 파일은 JFrame을 상속한 메인 클래스와 여러 내부 클래스(ChartPanelFlat, TrafficLight 등)를 포함합니다. 총 약 1300줄의 코드로, Swing 컴포넌트를 사용해 UI를 구축하고, 시리얼 통신으로 데이터를 처리합니다.
- `RotorHallSetupMac_new.java`, `TestPort.java` — 보조/테스트 소스 파일. `TestPort.java`는 시리얼 포트 테스트용으로 보입니다.
- `jSerialComm-2.9.3.jar` — 시리얼 통신 라이브러리 (런타임 의존성). Java에서 시리얼 포트를 쉽게 사용할 수 있게 해주는 외부 라이브러리입니다. 버전 2.9.3은 2021년 출시된 안정 버전입니다.
- `*.class`, `*.jar` (예: `RotorHallSetupMac.jar`, `RotorHallSetupMac-fat.jar`, `TestPort.jar`) — 컴파일 산출물 / 번들. `javac`로 컴파일된 바이트코드 파일들입니다. JAR 파일은 실행 가능한 아카이브로, 의존성을 포함할 수 있습니다.
- `runtime/`, `runtime25/`, `runtime25v2/`, `jre-min/` 등 — 패키징을 위해 포함된 JRE 이미지를 닮은 디렉터리 (크기가 큼). 각 폴더는 Java 런타임 환경을 포함하며, 플랫폼별로 분리되어 있습니다 (예: macOS용). 크기가 수백 MB에 달할 수 있어 저장소에 포함시키지 않는 것이 좋습니다.
- `build/`, `temp*/`, `dist.zip` — 빌드/임시 산출물. `build/`는 빌드 도구(예: Maven/Gradle)가 생성하는 폴더, `temp*`는 임시 파일 저장소, `dist.zip`은 배포용 압축 파일입니다.
- `manifest*.txt`, `.jpackage.xml`, `*.mf`, `*.cfg` — 패키징/메타데이터. `manifest.txt`는 JAR 메니페스트 파일로, 메인 클래스와 의존성을 지정합니다. `.jpackage.xml`은 Java 14+의 jpackage 도구 설정 파일입니다.
- `META-INF/` — jar 메타데이터. JAR 파일 내부의 메타데이터 폴더로, 메니페스트와 보안 설정을 포함합니다.
- `20250929_115448.mp4` — 미디어 파일 (소스와 무관). 비디오 파일로, 프로젝트와 관련 없어 보입니다. 아마 테스트나 데모용으로 추가된 것 같습니다.

### 권장 상태(깃 저장소 기준)
- 소스: 커밋할 것
  - `*.java` 파일들 (`RotorHallSetupMac.java`, `TestPort.java`, ...)는 저장소에 있어야 합니다. 이들은 프로젝트의 핵심 코드입니다.
- 무시하거나 릴리스로 이동할 것
  - `*.class`, `*.jar` (컴파일 산출물): 빌드 시마다 생성되므로 버전 관리에서 제외. 대신 CI/CD 파이프라인에서 생성.
  - `build/`, `temp/`, `temp_jar/`, `temp_main/`, `dist.zip`: 임시/빌드 폴더로, 로컬에서만 필요.
  - `runtime/`, `runtime25/`, `runtime25v2/`, `jre-min/` (대형 JRE 이미지는 리포지토리에 보관하지 말고 릴리스에 첨부): 저장소 크기를 줄이고, 다운로드 속도를 높이기 위해.
  - 대용량 미디어(`*.mp4`) 및 OS/IDE 파일(`.DS_Store`, `.vscode/`): 프로젝트 코드와 무관한 파일들.

### 예시 `.gitignore` (권장)
```
# build / ide
build/
dist.zip
.vscode/
.DS_Store

# java compiled
*.class
*.jar

# runtime / packaged jre
runtime/
runtime25/
runtime25v2/
jre-min/
temp/
temp_jar/
temp_main/

# media/logs
*.mp4
*.log
```

이 `.gitignore`는 Git이 위 파일들을 추적하지 않도록 합니다. 저장소 크기를 줄이고, 불필요한 충돌을 방지합니다.

### 의존성
- jSerialComm: 이미 `app/jSerialComm-2.9.3.jar`가 포함되어 있습니다. 이 라이브러리는 크로스 플랫폼 시리얼 통신을 지원합니다. 최신 버전은 2.10.4 (2024년)지만, 호환성을 위해 현재 버전을 유지하는 것이 좋습니다.
- Java LTS 권장: Java 21 (2023 LTS) 또는 조직에서 지정한 LTS 버전 사용 권장. Java 17 (2021 LTS)도 호환됩니다. Swing은 Java 8+에서 지원되지만, 최신 LTS를 사용해 보안 패치와 성능 향상을 얻으세요.

### 빌드 & 실행 (간단한 로컬 개발 방법)
1) 컴파일
```bash
# 작업 디렉터리를 app/로 이동
cd app
# 출력을 담을 폴더 생성
mkdir -p out
# javac로 컴파일 (macOS zsh 기준)
javac -d out -cp jSerialComm-2.9.3.jar *.java
```
- `-d out`: 컴파일된 클래스를 `out` 폴더에 저장.
- `-cp jSerialComm-2.9.3.jar`: 클래스 경로에 jSerialComm 라이브러리 포함.
- 이 명령은 모든 `.java` 파일을 컴파일합니다. 오류가 발생하면 Java 버전이나 라이브러리 경로를 확인하세요.

2) 실행 (클래스 경로에 jSerialComm 포함)
```bash
cd app
java -cp out:jSerialComm-2.9.3.jar RotorHallSetupMac
```
- `-cp out:jSerialComm-2.9.3.jar`: 클래스 경로에 컴파일된 클래스와 라이브러리 포함.
- 메인 클래스는 `RotorHallSetupMac`입니다. 실행 시 GUI 창이 열립니다.

3) (옵션) 실행 가능한 JAR 만들기
```bash
cd app
# 메인 클래스는 기본 패키지의 RotorHallSetupMac
jar --create --file RotorHallSetupMac.jar -C out .
# 실행: java -cp RotorHallSetupMac.jar:jSerialComm-2.9.3.jar RotorHallSetupMac
```
- `jar --create`: JAR 파일 생성.
- `-C out .`: `out` 폴더의 내용을 JAR에 복사.
- 이 JAR는 의존성(jSerialComm)을 포함하지 않으므로 실행 시 별도 지정 필요.

4) 포함된 JRE로 실행 (이미 `runtime*/bin/java`가 존재하는 경우)
```bash
# 예: app/runtime25/bin/java 가 있을 때
app/runtime25/bin/java -cp out:jSerialComm-2.9.3.jar RotorHallSetupMac
```
- 포함된 JRE를 사용해 Java 버전을 고정. 배포 시 유용합니다.

### macOS 관련 유의사항
- 시리얼 포트 접근: macOS에서 실제 디바이스(`/dev/tty.*` 또는 `/dev/cu.*`)에 접근하려면 적절한 권한이 필요합니다. 권한 문제가 발생하면 관리자 권한이나 보안/개인정보 보호 설정에서 터미널 접근을 허용해야 할 수 있습니다. 예: `sudo java ...`로 실행하거나, 시스템 설정 > 보안 및 개인정보 보호 > 개인정보 보호 > 파일 및 폴더에서 터미널 허용.
- 포트 리스트가 비어있다면 `fillPorts()` 함수가 포트명을 찾지 못한 경우입니다. `ls /dev/tty.*` 또는 `ls /dev/cu.*`로 확인하세요. USB 시리얼 어댑터가 연결되어 있는지 확인.
- macOS Big Sur 이상에서는 추가 권한이 필요할 수 있습니다. 애플리케이션을 코드 서명하거나, 개발자 모드를 활성화하세요.

### macOS 관련 유의사항
- 개발 내용 전체적인 작동 매커니즘 : 슈키르전
- 전압별 축적 기능, 프론트 일부 수정, 전압별 음수,양수 반영 수식 재구성 : 이승민
- .exe 패키징 + DLL관련 패키징 관련 수정 : 전정웅, 우지안
