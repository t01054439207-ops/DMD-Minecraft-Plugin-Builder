# DMD Minecraft Plugin Builder V22 — Android Studio project

**주의: 이 파일은 APK가 아니라 APK를 빌드하기 위한 Android Studio 프로젝트입니다.**

## APK 만들기
1. PC에 Android Studio를 설치합니다.
2. 이 폴더를 Android Studio에서 엽니다.
3. Gradle 동기화가 끝날 때까지 기다립니다.
4. Build → Build Bundle(s) / APK(s) → Build APK(s)를 선택합니다.
5. 빌드된 APK를 휴대폰으로 옮겨 설치합니다.

## 기능
- Android 모바일 전용 화면
- Java 또는 Skript 스타터 프로젝트 생성
- Paper / Purpur / Spigot 선택
- Minecraft 버전 선택
- Android 저장 화면을 통한 ZIP 내보내기
- 유료 API 키 불필요

## 현재 제한
- 오프라인 기본 템플릿만 생성하며 AI 자연어 생성은 아직 포함되지 않습니다.
- Java 템플릿은 Paper API 1.20.4와 Java 17을 사용합니다. 화면에서 선택한 서버 플랫폼/버전이 실제 의존성 설정에 자동 반영되지는 않습니다.
- 생성된 프로젝트는 Minecraft 서버에서 빌드·실행 테스트되지 않았습니다.

## APK 빌드 자동화
1. 이 프로젝트 폴더의 내용을 GitHub 저장소에 올립니다.
2. 저장소의 Actions 탭에서 **Build Android APK**를 실행합니다.
3. 작업이 끝나면 `DMD-Plugin-Builder-V22-debug` 아티팩트를 내려받아 `app-debug.apk`를 꺼냅니다.

이 ZIP에는 APK가 포함되어 있지 않습니다. 현재 작업 환경에는 Android SDK와 Gradle 빌드 도구가 없어 APK 컴파일을 직접 수행할 수 없습니다.
