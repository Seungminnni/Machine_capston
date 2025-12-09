# RotorHallSetupMac — 데스크탑 GUI (Java Swing)

✅ 프로젝트 개요

- RotorHallSetupMac은 로터(또는 원심) 장치의 무게(Weight)와 스트레인 게이지(Voltage) 간의 관계를 시각화하고, 실시간으로 데이터(시리얼/WiFi 등)를 받아 모니터링하는 Java Swing 애플리케이션입니다.
- 전압값을 무게로 변환하여 그래프(Voltage vs Time, Weight vs Time), 트래픽라이트(정상/주의/위험), 게이지 표기 및 퍼센트 계산을 수행합니다.

---

💡 주요 기능

- 실시간 전압(V) 및 무게(W) 그래프
- 퍼센트 기반 경고(주의/위험) 및 트래픽라이트
- 게이지(퍼센트)와 현재 전압·무게 수치 표시
- 시뮬레이션 모드(하드웨어 없이 동작 테스트 가능)
- 자동 포트 검색 / 수동 포트 입력 (macOS: `/dev/cu.*` 등)
- 전압→무게 변환식: W = a * S ± b (a, b, ±은 사용자 설정)

---

⚙️ 요구사항

- Java 17 또는 Java 21 (LTS 권장)
- jSerialComm 라이브러리 (예: `app/jSerialComm-2.9.3.jar`)
- macOS: `/dev/cu.*` 또는 `/dev/tty.*` 포트 접근 권한 필요

---

📁 프로젝트 구조 (예시)

- app/
  - `RotorHallSetupMac.java` — 메인 애플리케이션 (GUI, 그래프, 시리얼 처리 로직)
  - `TestPort.java` — 시리얼 포트 테스트용 유틸리티
  - `jSerialComm-2.9.3.jar` — 시리얼 통신 라이브러리(예)
  - `out/` — 컴파일된 클래스 파일 (권장: Git에 포함하지 않음)
  - `runtime*/` — 포함된 JRE 이미지(권장: 저장소에 포함하지 않음)

---

🚀 빠른 시작 (macOS / zsh 기준)

1) 소스 디렉터리로 이동

```bash
cd ~/Desktop/Machine_capston/RotorHallSetupMacV15/app
```

2) 컴파일

```bash
mkdir -p out
javac -d out -cp jSerialComm-2.9.3.jar *.java
```

3) 실행

```bash
java -cp out:jSerialComm-2.9.3.jar RotorHallSetupMac
```

- Windows에서는 클래스패스 구분자로 `;`를 사용합니다.

4) JAR 생성(선택)

```bash
jar --create --file RotorHallSetupMac.jar -C out .
java -cp RotorHallSetupMac.jar:jSerialComm-2.9.3.jar RotorHallSetupMac
```

---

🧪 시뮬레이션 모드

- 앱은 시뮬레이션으로 전압/무게를 생성하여 실제 하드웨어 없이 동작을 테스트할 수 있습니다.

---

🔧 설정 & 입력값

- Port Name: 시리얼 포트 (예: `/dev/cu.usbmodemXXXXX`) 혹은 네트워크 주소(예: `192.168.0.10:1234`).
- Network: Serial / WiFi / Bluetooth
- a, b, ± : W = a * S ± b 변환식을 통해 전압을 무게로 변환 (예: a = 4.2, b = 0.1, sign = -)
- Caution/Danger %: 퍼센트(예: 40, 60) — 설정한 radius(또는 max weight)에 근거해 임계값 계산

---

🧭 문제 해결

- 포트가 검색되지 않음
  - `ls /dev/cu.*` 또는 `ls /dev/tty.*`로 포트 확인
  - macOS에서 터미널 접근 권한 허용 필요
- 권한 문제
  - macOS의 경우 `sudo java ...` 로 테스트 가능(보안상 권장되진 않음)
- Windows에서 DLL 에러
  - `jSerialComm.dll`이 필요할 수 있으므로 적절한 위치에 DLL을 두거나 PATH를 설정하세요.

---

# 컴파일 산출물
*.class
*.jar

# 포팅된 Java runtime
runtime*/

# 로그/미디어
*.log
*.mp4

### 개발 기여 내용
- 개발 내용 전체적인 작동 매커니즘 : 슈키르전
- 전압별 축적 기능, 프론트 일부 수정, 전압별 음수,양수 반영 수식 재구성 : 이승민
- .exe 패키징 + DLL관련 패키징 관련 수정 : 전정웅, 우지안

### 공개 및 라이선스 관련
- 본 프로젝트는 영남대학교 기계공학과와 콜라보 소프트웨어이며 허가 없이 재사용, 수정, 활용을 금합니다