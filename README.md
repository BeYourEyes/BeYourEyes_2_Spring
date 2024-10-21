## 당신의 안식 BeYourEyes 어플리케이션 - v2.0
노인을 위한 영양정보 제공 앱 버전 2 어플리케이션입니다.
UI/UX 개선 작업 중입니다.

기존 프로젝트 파일을 일부 초기화

## AndroidManifest.xml :
- 모든 액티비티 삭제 후 처음 실행되는 액티비티로 MainActivity만 등록
- 인터넷, 저장소, 카메라, 한글OCR 사용을 위한 설정은 보존

## Kotlin 클래스 : 
- 모든 데이터, 액티비티 클래스 삭제 후 openCV 테스트용 코드가 있는 MainActivity 클래스 작성

## 각종 리소스 파일 : 
- res/drawable : 기본 앱 아이콘 제외하고 모두 삭제
- res/font : pretender 폰트 삭제, WantedSans 폰트 추가
- res/layout : 모든 레이아웃 xml 파일 삭제 후 빈 activity_main.xml만 작성
- res/mipmap : drawable을 다 정리했기 때문에, 앱 아이콘 설정도 기본으로 세팅
- res/values :
  - colors.xml : 기본 색상 외 따로 등록한 색상 설정 모두 삭제
  - strings.xml : 앱 이름, id 관련 정보 외 모두 삭제
  - styles.xml : 내용 모두 삭제
  - themes.xml : 액션바는 없애고, 상태바는 보이게 설정(새 디자인 적용 위해)
- res/xml : (애니메이션 설정 파일, firebase db 관련 규칙, 사진 임시 저장소 설정 파일 기존 파일) 일단 모두 보존

## 빌드 관련 설정
- opencv2 import, CI/CD 설정 보존
- 타겟 api만 34로 업그레이드 후 gradle 파일 그대로 보존
  - 구글 플레이 앱스토어 정책 상 타겟을 34로 해야 앞으로 업로드 가능!
 
## CI/CD 파이프라인 구축
- main 제외한 모든 branch에 코드 push시, 자동 build, test 수행
- build 완료 후 APK, AAB 파일 생성

## OCR API + OPEN API 문자 추출 로직 개선
