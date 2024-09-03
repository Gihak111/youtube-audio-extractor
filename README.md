## 간단한 유튜브 음원 추출 사이트 만들기  
### 기타 라이브러리들과 버젼 충돌이 있으므로, 가상환경을 만들고 시작하자.  
1. 가상환경 만들기  
```bash
python -m venv myenv
```

2. 가상환경 활성화  
```bash
.\myenv\Scripts\activate
```

3. 필요한 패키지 설치  
```bash
pip install yt-dlp
pip install gradio-client openxlab tensorflow-intel
```

4. 스프링부트 실행  
```bash
mvn spring-boot:run
```

ffmeg 설치 해야 한다.  

기본적으로 유튜브 음원추출은 불법이므로, 이런 기능이 있다. 이런식으로 코드가 돌아간다 정도를 파악하는, 교육 목적으로만 보도록 하자.  


## 코딩 중 발생한 디버깅 로그 정리
1. 자바 버젼문제
안드로이드 스튜디오 하면서 깔아놓은 자바 버젼이 맞지 않음.
버젼을 17로 새로 설치 후 진행

2. 의존성 충돌 문제
yt-dlp와 달느 의존성들의 버젼 층돌하면서 맞지 않음
myenv 가상환경에 새로운 환경 구축 후 진행

3. ffmpeg 인식 오류 문제
ffmpeg가 설치되어 있고, 잘 싱행 되지만, IDE 에서 정상적으로 인식되지 않음을 확인
--ffmpeg-location 옵션을 설정해 스크립트 내부에 파일 위치를 지정하는 것으로 해결

4. HTTP 요청시 인코딩 된 상채로 전달되는 문제
한국어가 포함되 ㄴ영상을 사이트에 집어넣으면 400 Bad Request 오류가 발생
파일 이름을 URl에 포함 시키기 전에 URLEncoder 클래스를 사용하여 URL에 적합한 형식으로 인코딩 하는걸로 해결

5. 파일의 내용 표시 오류
다운로드 누를 시 MP3 파일이 아닌 파일의 내용이 브라우저에 텍스트로 표시되는 문제 발생
이는 파일을 제대로 전송하지 못했거나, 응답의 Content-Type이 올바르게 설정되지 않았기 때문일 가능성 있으므로 적절한 헤더와 Content-Type을 설정, 헤더 설정, Content-Type을 "audio/mpeg로 설정 하는 것으로 해결
