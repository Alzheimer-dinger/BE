<!-- =========================
= A-dinger README (HTML) =
==========================-->

<!-- 헤더 / 배지 -->
<header id="top" align="center">
  <h1>A-dinger (알츠하이머딩거) — 치매 환자 케어 웹앱</h1>

  <p>
    <a href="https://api.alzheimerdinger.com/swagger-ui/index.html#/">
      <img src="https://img.shields.io/badge/Swagger-API%20Docs-85EA2D?style=for-the-badge&logo=swagger&logoColor=white" alt="Swagger API Docs Badge" />
    </a>
    <img src="https://img.shields.io/badge/License-MIT-000000?style=for-the-badge" alt="MIT License Badge" />
  </p>

  <p><em>보호자–환자 연결, 통화 기록 분석, 감정 리포트, 리마인더와 알림을 제공하는 치매 환자 케어 서비스</em></p>
</header>

<hr />

<!-- 빠른 링크 -->
<nav id="links" align="center">

  <ul>
    <li><strong>GitHub</strong> :
      <a href="https://github.com/Alzheimer-dinger" target="_blank" rel="noopener">https://github.com/Alzheimer-dinger</a>
    </li>
    <li><strong>API 문서(Swagger)</strong> :
      <a href="https://api.alzheimerdinger.com/swagger-ui/index.html#/" target="_blank" rel="noopener">
        https://api.alzheimerdinger.com/swagger-ui/index.html#/
      </a>
    </li>
  </ul>

<h3>📒 목차</h3>
<p class="toc" style="text-align:center; margin:0;">
  <a href="#intro">프로젝트 소개</a>
  <span aria-hidden="true"> | </span>
  <a href="#team">팀원 구성</a>
  <span aria-hidden="true"> | </span>
  <a href="#tech-stack">기술 스택</a>
  <span aria-hidden="true"> | </span>
  <a href="#repository">저장소·브랜치 전략·구조</a>
  <span aria-hidden="true"> | </span>
  <a href="#schedule">개발 기간·작업 관리</a>
  <span aria-hidden="true"> | </span>
  <a href="#quality-notes">신경 쓴 부분</a>
  <span aria-hidden="true"> | </span>
  <a href="#pages">페이지별 기능</a>
  <span aria-hidden="true"> | </span>
  <a href="#api">주요 API</a>
</p>

<p align="right"><a href="#top">맨 위로 ⤴</a></p>
</nav>

<hr />

<!-- 프로젝트 소개 -->
<section id="intro">
  <h2>📖 프로젝트 소개</h2>

  <p>
    본 프로젝트는 치매 환자와 보호자를 위한 <strong>AI 동반 케어 웹앱</strong>입니다.
    환자는 앱에서 인공지능과 <em>실시간 대화(음성/자막)</em>로 일상을 공유하고,
    보호자는 연결 계정을 통해 심리 상태와 이상 징후를 모니터링합니다.
    하루하루 축적되는 대화·활동 데이터를 분석해 <strong>일·주·월 단위 종합 리포트</strong>
    (감정 타임라인, 참여도, 평균 통화시간, 위험 지표)를 제공하여 세심한 돌봄 계획 수립을 돕습니다.
  </p>

  <ul>
    <li>원클릭 통화(대기 → 진행 → 종료), <strong>실시간 자막/응답</strong></li>
    <li><strong>RAG 메모리</strong>로 개인 맥락 유지, 토큰 효율 최적화</li>
    <li>보호자–환자 <strong>관계 관리</strong>(요청/승인/해제) 및 <strong>리마인더/알림</strong></li>
    <li><strong>PWA/FCM</strong> 기반 푸시 알림, 웹 대시보드로 리포트 열람</li>
    <li>운영/모니터링: <strong>Micrometer + Prometheus + Grafana</strong></li>
  </ul>
</section>

<hr />

<!-- 팀원 구성 -->
<section id="team">
  <h2>👥 팀원 구성</h2>

  <table>
    <tbody>
      <tr>
        <td align="center">
          <a href="깃허브주소">
            <img src="https://avatars.githubusercontent.com/Smallt0wn" width="100px" alt="정장우 프로필 이미지" /><br />
            <sub><b>정장우</b></sub>
          </a><br />
          <sub>팀 리더 · 백엔드<br />주요 도메인 · 인프라 구축</sub>
        </td>
        <td align="center">
          <a href="깃허브주소">
            <img src="https://avatars.githubusercontent.com/KoungQ" width="100px" alt="김경규 프로필 이미지" /><br />
            <sub><b>김경규</b></sub>
          </a><br />
          <sub>백엔드<br />인증/인가 · 시스템/인프라 설계</sub>
        </td>
        <td align="center">
          <a href="깃허브주소">
            <img src="https://avatars.githubusercontent.com/ydking0911" width="100px" alt="박영두 프로필 이미지" /><br />
            <sub><b>박영두</b></sub>
          </a><br />
          <sub>백엔드<br />도메인 · CI/CD · 모니터링</sub>
        </td>
        <td align="center">
          <a href="깃허브주소">
            <img src="https://avatars.githubusercontent.com/nyewon" width="100px" alt="노예원 프로필 이미지" /><br />
            <sub><b>노예원</b></sub>
          </a><br />
          <sub>프론트<br />UI/UX · 통화 WebSocket · CD · FCM</sub>
        </td>
      </tr>
      <tr>
        <td align="center">
          <a href="깃허브주소">
            <img src="https://avatars.githubusercontent.com/HY0S" width="100px" alt="김효신 프로필 이미지" /><br />
            <sub><b>김효신</b></sub>
          </a><br />
          <sub>프론트<br />UI/UX · API 연동 · 상태관리</sub>
        </td>
        <td align="center">
          <a href="깃허브주소">
            <img src="https://avatars.githubusercontent.com/hyunbridge" width="100px" alt="서현교 프로필 이미지" /><br />
            <sub><b>서현교</b></sub>
          </a><br />
          <sub>AI<br />아이디어 · RAG 메모리 · 분석 리포트</sub>
        </td>
        <td align="center">
          <a href="깃허브주소">
            <img src="https://avatars.githubusercontent.com/kmj02dev" width="100px" alt="강민재 프로필 이미지" /><br />
            <sub><b>강민재</b></sub>
          </a><br />
          <sub>AI<br />실시간 통화 · 감정 분석·요약</sub>
        </td>
        <td align="center">
          <!-- 필요 시 예비 칸 / 삭제 가능 -->
        </td>
      </tr>
    </tbody>
  </table>
</section>

<hr />

<!-- 기술 스택 -->
<section id="tech-stack">
  <h2>🧰 기술 스택</h2>

  <!-- Frontend -->
<h3>Frontend</h3>
  <p>
    <img alt="React" src="https://img.shields.io/badge/React-19-61DAFB?style=for-the-badge&logo=react&logoColor=000" />
    <img alt="TypeScript" src="https://img.shields.io/badge/TypeScript-5.7.3-3178C6?style=for-the-badge&logo=typescript&logoColor=white" />
    <img alt="Vite" src="https://img.shields.io/badge/Vite-6-646CFF?style=for-the-badge&logo=vite&logoColor=white" />
    <img alt="React Router" src="https://img.shields.io/badge/React_Router-7-CA4245?style=for-the-badge&logo=reactrouter&logoColor=white" />
    <img alt="styled-components" src="https://img.shields.io/badge/styled--components-6-DB7093?style=for-the-badge&logo=styledcomponents&logoColor=white" />
    <img alt="Recharts" src="https://img.shields.io/badge/Recharts-3-764ABC?style=for-the-badge" />
    <img alt="Axios" src="https://img.shields.io/badge/Axios-1.x-5A29E4?style=for-the-badge" />
    <img alt="PWA" src="https://img.shields.io/badge/PWA-Ready-5A0FC8?style=for-the-badge" />
  </p>

  <!-- Backend -->
<h3>Backend</h3>
  <p>
    <img alt="Java" src="https://img.shields.io/badge/Java-17-007396?style=for-the-badge&logo=openjdk&logoColor=white" />
    <img alt="Spring Boot" src="https://img.shields.io/badge/Spring_Boot-3.5.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
    <img alt="Spring Security" src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" />
    <img alt="Spring Data JPA" src="https://img.shields.io/badge/JPA%2FData-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
    <img alt="Spring Actuator" src="https://img.shields.io/badge/Actuator-6DB33F?style=for-the-badge" />
    <img alt="Spring Batch" src="https://img.shields.io/badge/Batch-6DB33F?style=for-the-badge" />
    <img alt="FastAPI" src="https://img.shields.io/badge/FastAPI-Python-009688?style=for-the-badge&logo=fastapi&logoColor=white" />
  </p>

  <!-- AI / Data -->
<h3>AI / Data</h3>
  <p>
    <img alt="Vertex AI" src="https://img.shields.io/badge/Google_Vertex_AI-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white" />
    <img alt="Gemini Live API" src="https://img.shields.io/badge/Gemini_Live_API-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white" />
    <img alt="Hugging Face Inference" src="https://img.shields.io/badge/Hugging_Face-Inference-FFD21E?style=for-the-badge&logo=huggingface&logoColor=000" />
    <img alt="Pinecone" src="https://img.shields.io/badge/Pinecone-Vector_DB-0E5EE8?style=for-the-badge&logo=pinecone&logoColor=white" />
  </p>

  <!-- Database / Messaging / Caching -->
<h3>Database / Messaging / Caching</h3>
  <p>
    <img alt="MySQL" src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
    <img alt="MongoDB" src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white" />
    <img alt="Redis" src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white" />
    <img alt="Apache Kafka" src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white" />
  </p>

  <!-- Infra / DevOps -->
<h3>Infra / DevOps</h3>
  <p>
    <img alt="GCP Compute Engine" src="https://img.shields.io/badge/GCP_Compute_Engine-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white" />
    <img alt="Google Cloud Storage" src="https://img.shields.io/badge/Google_Cloud_Storage-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white" />
    <img alt="Artifact Registry" src="https://img.shields.io/badge/Artifact_Registry-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white" />
    <img alt="Docker" src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
    <img alt="Nginx" src="https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx&logoColor=white" />
    <img alt="GitHub Actions" src="https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white" />
    <img alt="Cloudflare" src="https://img.shields.io/badge/Cloudflare-F38020?style=for-the-badge&logo=cloudflare&logoColor=white" />
  </p>

  <!-- Monitoring / Docs / Test -->
<h3>Monitoring / Docs / Test</h3>
  <p>
    <img alt="Micrometer" src="https://img.shields.io/badge/Micrometer-1.x-13BDBD?style=for-the-badge" />
    <img alt="Prometheus" src="https://img.shields.io/badge/Prometheus-E6522C?style=for-the-badge&logo=prometheus&logoColor=white" />
    <img alt="Grafana" src="https://img.shields.io/badge/Grafana-F46800?style=for-the-badge&logo=grafana&logoColor=white" />
    <a href="https://api.alzheimerdinger.com/swagger-ui/index.html#/">
      <img alt="Swagger API Docs" src="https://img.shields.io/badge/Swagger-API_Docs-85EA2D?style=for-the-badge&logo=swagger&logoColor=white" />
    </a>
    <img alt="JUnit 5" src="https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white" />
    <img alt="Postman" src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white" />
  </p>

  <!-- Push / Notification -->
<h3>Push / Notification</h3>
  <p>
    <img alt="FCM" src="https://img.shields.io/badge/FCM-Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=000" />
    <img alt="Firebase Admin SDK" src="https://img.shields.io/badge/Firebase_Admin_SDK-FFCA28?style=for-the-badge&logo=firebase&logoColor=000" />
  </p>
</section>

<hr />

<!-- 주요 API -->
<section id="api">
  <h2>🔑 주요 API (요약)</h2>

  <p>전체 스펙은 Swagger에서 확인: <a href="https://api.alzheimerdinger.com/swagger-ui/index.html#/">https://api.alzheimerdinger.com/swagger-ui/index.html#/</a></p>

  <table>
    <thead>
      <tr>
        <th align="left">Method</th>
        <th align="left">Endpoint</th>
        <th align="left">설명</th>
        <th align="center">인증</th>
      </tr>
    </thead>
    <tbody>
      <tr><td>POST</td><td><code>/api/users/sign-up</code></td><td>회원가입(Guardian/Patient, 선택: 환자코드)</td><td align="center">❌</td></tr>
      <tr><td>POST</td><td><code>/api/users/login</code></td><td>로그인(JWT Access/Refresh 발급, FCM 토큰 접수)</td><td align="center">❌</td></tr>
      <tr><td>DELETE</td><td><code>/api/users/logout</code></td><td>로그아웃(토큰 무효화)</td><td align="center">✅</td></tr>
      <tr><td>POST</td><td><code>/api/token</code></td><td>토큰 재발급(<code>refreshToken</code> 쿼리)</td><td align="center">✅</td></tr>
      <tr><td>GET</td><td><code>/api/users/profile</code></td><td>프로필 조회</td><td align="center">✅</td></tr>
      <tr><td>PATCH</td><td><code>/api/users/profile</code></td><td>프로필 수정(이름/성별/비밀번호)</td><td align="center">✅</td></tr>
      <tr><td>GET</td><td><code>/api/images/profile/upload-url</code></td><td>GCS Presigned 업로드 URL 발급(<code>extension</code>)</td><td align="center">✅</td></tr>
      <tr><td>POST</td><td><code>/api/images/profile</code></td><td>업로드 파일을 프로필 이미지로 적용(<code>fileKey</code>)</td><td align="center">✅</td></tr>
      <tr><td>POST</td><td><code>/api/relations/send</code></td><td>관계 요청 전송(<code>patientCode</code>)</td><td align="center">✅</td></tr>
      <tr><td>POST</td><td><code>/api/relations/resend</code></td><td>만료 요청 재전송(<code>relationId</code>)</td><td align="center">✅</td></tr>
      <tr><td>PATCH</td><td><code>/api/relations/reply</code></td><td>관계 요청 응답(<code>relationId</code>, <code>status</code>)</td><td align="center">✅</td></tr>
      <tr><td>GET</td><td><code>/api/relations</code></td><td>관계 목록 조회</td><td align="center">✅</td></tr>
      <tr><td>DELETE</td><td><code>/api/relations</code></td><td>관계 해제(<code>relationId</code>)</td><td align="center">✅</td></tr>
      <tr><td>GET</td><td><code>/api/reminder</code></td><td>리마인더 조회</td><td align="center">✅</td></tr>
      <tr><td>POST</td><td><code>/api/reminder</code></td><td>리마인더 등록(<code>fireTime</code>, <code>status</code>)</td><td align="center">✅</td></tr>
      <tr><td>GET</td><td><code>/api/transcripts</code></td><td>통화 기록 목록(요약)</td><td align="center">✅</td></tr>
      <tr><td>GET</td><td><code>/api/transcripts/{sessionId}</code></td><td>통화 기록 상세(요약/대화 로그)</td><td align="center">✅</td></tr>
      <tr><td>GET</td><td><code>/api/analysis/report/latest</code></td><td>최근 분석 리포트(<code>periodEnd</code>, <code>userId</code>)</td><td align="center">✅</td></tr>
      <tr><td>GET</td><td><code>/api/analysis/period</code></td><td>기간별 감정 분석(<code>start</code>, <code>end</code>, <code>userId</code>)</td><td align="center">✅</td></tr>
      <tr><td>GET</td><td><code>/api/analysis/day</code></td><td>일별 감정 분석(<code>date</code>, <code>userId</code>)</td><td align="center">✅</td></tr>
      <tr><td>POST</td><td><code>/api/feedback</code></td><td>피드백 저장(<code>rating</code>, <code>reason</code>)</td><td align="center">✅</td></tr>
    </tbody>
  </table>

  <p><em>참고:</em> <em>실시간 통화(음성/자막)</em>은 클라이언트 ↔ AI 서버(WebSocket/Streaming) 연결을 통해 처리되며, 백엔드는 세션/기록/리포트 API를 제공합니다.</p>

  <p align="right"><a href="#top">맨 위로 ⤴</a></p>
</section>

<!-- 저장소 · 브랜치 전략 · 프로젝트 구조 -->
<section id="repository">
  <h2>📦 저장소 &nbsp;·&nbsp; 브랜치 전략 · 프로젝트 구조</h2>

  <p>
    <strong>GitHub</strong> :
    <a href="https://github.com/Alzheimer-dinger">https://github.com/Alzheimer-dinger</a>
  </p>

<h3>브랜치 전략 (Git-flow 기반)</h3>
  <ul>
    <li><code>main</code> — 배포용 안정 브랜치. 태깅(<code>vX.Y.Z</code>) 후 배포.</li>
    <li><code>develop</code> — 통합 개발 브랜치. 기능/버그 픽스 머지 대상.</li>
    <li><code>feature/&lt;scope&gt;-&lt;short-desc&gt;</code> — 기능 단위 작업. 완료 시 PR → <code>develop</code>.</li>
    <li><code>hotfix/&lt;issue&gt;</code> — 긴급 수정. PR → <code>main</code> 및 <code>develop</code> 양쪽 반영.</li>
    <li><code>release/&lt;version&gt;</code> — 릴리즈 준비(버전, 문서, 마이그레이션) 후 <code>main</code> 병합.</li>
  </ul>

<h4>PR 규칙</h4>
  <ul>
    <li>PR 템플릿 사용: 배경/변경점/테스트/스크린샷/체크리스트 포함</li>
    <li>리뷰 1명 이상 승인(🚦 최소 1 Approve), CI 통과 필수</li>
    <li>라벨: <code>feature</code>, <code>fix</code>, <code>refactor</code> 등</li>
  </ul>

<h4>커밋 컨벤션 (Conventional Commits)</h4>
  <pre><code>feat(auth): add refresh token rotation
fix(api): handle null imageUrl in profile response
refactor(ui): split ReportChart into small components
docs(readme): add tech stack badges
chore(ci): bump node to 20.x in workflow
</code></pre>

<h3>프로젝트 구조</h3>
  <pre><code>/
├─ BE/
│  ├─ build.gradle
│  ├─ src/main/java/opensource/alzheimerdinger/core
│  │  ├─ global/
│  │  └─ domain/
│  │     ├─ user/
│  │     ├─ image/
│  │     ├─ relation/
│  │     ├─ reminder/
│  │     ├─ transcript/
│  │     ├─ analysis/
│  │     └─ feedback/
│  └─ src/main/resources/
│
├─ FE/
│  ├─ package.json
│  └─ src/
│
└─infra/
   ├─ docker-compose.yml
   ├─ nginx/
   ├─ prometheus/
   └─ grafana/
</code></pre>
</section>

<hr />

<!-- 개발 기간 · 작업 관리 -->
<section id="schedule">
  <h2>🗓️ 개발 기간 &nbsp;·&nbsp; 작업 관리</h2>

  <table>
    <thead>
      <tr>
        <th>기간</th>
        <th>스프린트 목표</th>
        <th>주요 산출물</th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>2025-06-20 ~ 2025-07-03 (1~2주차)</td>
        <td>요구사항 정의 · API 명세 · DB 설계</td>
        <td>요구사항 정의서, ERD, Swagger 초안</td>
      </tr>
      <tr>
        <td>2025-07-04 ~ 2025-07-31 (3~6주차)</td>
        <td>핵심 기능·UI/UX 개발, RAG 구현, 프롬프트 엔지니어링</td>
        <td>FE 페이지/컴포넌트, BE 도메인/인증, RAG 서비스</td>
      </tr>
      <tr>
        <td>2025-08-01 ~ 2025-08-14 (7~8주차)</td>
        <td>기능 통합·안정화 테스트</td>
        <td>E2E/통합 테스트, 버그픽스, 성능/보안 점검</td>
      </tr>
      <tr>
        <td>2025-08-15 ~ 2025-08-21 (9주차)</td>
        <td>배포·모니터링·운영</td>
        <td>릴리즈 노트, 대시보드, 알림 룰</td>
      </tr>
    </tbody>
  </table>

<h3>작업 관리 방식</h3>
  <ul>
    <li><strong>이슈 추적</strong>: GitHub Issues (템플릿: <em>bug/feature/chore</em>)</li>
    <li><strong>칸반</strong>: GitHub Projects — <em>Backlog → In&nbsp;Progress → In&nbsp;Review → Done</em></li>
    <li><strong>WIP 제한</strong>: 인당 2개(리뷰 포함), 급한 이슈는 라벨 <code>priority:high</code></li>
    <li><strong>릴리즈</strong>: 주 1회 태깅(세맨틱 버저닝), 체인지로그 자동화</li>
    <li><strong>품질 게이트</strong>: CI 빌드/테스트/리포트, 린트·포맷·타입체크</li>
  </ul>
</section>

<hr />

<!-- 신경 쓴 부분 -->
<section id="quality-notes">
  <h2>🔧 신경 쓴 부분</h2>

  <ul>
    <li><strong>접근성/UX</strong>: 시맨틱 마크업, ARIA 라벨, 키보드 포커스(모달/토스트), 고대비/폰트 크기 대응</li>
    <li><strong>성능</strong>: 이미지 압축/지연로딩, 코드 스플리팅, 캐시 헤더, Recharts 렌더 최적화, PWA 오프라인 폴백</li>
    <li><strong>보안</strong>: JWT 액세스/리프레시 분리·로테이션, CORS 엄격화, HTTPS, BCrypt 해시, 헤더 보안(XFO/XCTO)</li>
    <li><strong>신뢰성</strong>: API 재시도·백오프, 카프카 비동기 처리, 타임아웃/서킷브레이커(중요 호출부)</li>
    <li><strong>관측</strong>: Micrometer 태깅(유저/엔드포인트/상태), Grafana 알림 룰, 상관관계 ID로 로그 추적</li>
    <li><strong>데이터 보호</strong>: PII 최소화, 역할/권한 분리(GUARDIAN/PATIENT), Presigned URL로 업로드 경로 제한</li>
    <li><strong>테스트</strong>: 단위/통합/계약 테스트, Swagger + Postman 시나리오, 스테이징 연기 테스트</li>
  </ul>
</section>

<hr />

<!-- 페이지별 기능 -->
<section id="pages">
  <h2>🧭 페이지별 기능</h2>

  <details>
    <summary><strong>Splash · 온보딩</strong></summary>
    <ul>
      <li>앱 로드시 스플래시 → 로그인 상태에 따라 라우팅</li>
      <li>간단 소개/권한 안내(마이크, 푸시)</li>
    </ul>
  </details>

  <details>
    <summary><strong>로그인/회원가입</strong></summary>
    <ul>
      <li>이메일·비밀번호 유효성 검사, 오류 메시지 인라인 표시</li>
      <li>회원가입 후 프로필 초기 설정(이름/성별/환자코드 옵션)</li>
      <li>JWT 발급(Access/Refresh), FCM 토큰 등록</li>
    </ul>
  </details>

  <details>
    <summary><strong>프로필(내/타 유저)</strong></summary>
    <ul>
      <li>내 프로필: 이미지/이름/성별/비밀번호 수정, 판매 영역은 미사용</li>
      <li>타 유저: 팔로우 개념 대신 <em>관계(보호자-환자)</em> 상태 표시</li>
    </ul>
  </details>

  <details>
    <summary><strong>관계 관리</strong></summary>
    <ul>
      <li>환자코드로 요청, 만료 시 재전송, 승인/거절</li>
      <li>관계 목록/해제, 상태(REQUESTED/APPROVED 등) 표시</li>
    </ul>
  </details>

  <details>
    <summary><strong>통화(실시간 AI)</strong></summary>
    <ul>
      <li>흐름: <code>CallWaiting → CallActive → End</code></li>
      <li>마이크 권한, 발화 감지(<em>useAudioStream</em>), WebSocket/Streaming</li>
      <li>실시간 자막/응답, 종료 후 기록 저장</li>
    </ul>
  </details>

  <details>
    <summary><strong>통화 기록(Transcripts)</strong></summary>
    <ul>
      <li>목록: 세션ID/제목/일시/지속시간 요약</li>
      <li>상세: 요약/대화 로그, 페이징/검색</li>
    </ul>
  </details>

  <details>
    <summary><strong>분석 리포트</strong></summary>
    <ul>
      <li><em>일간</em>: 날짜 선택, 월간 이모지 캘린더, 감정 점수, 원형 스코어</li>
      <li><em>종합</em>: 기간(1주/1달/사용자 지정) 선택, 감정 타임라인, 참여도/평균 통화시간/위험도</li>
    </ul>
  </details>

  <details>
    <summary><strong>리마인더</strong></summary>
    <ul>
      <li>알림 시각·상태 등록/조회(ACTIVE/INACTIVE)</li>
      <li>PWA/FCM 기반 푸시</li>
    </ul>
  </details>

  <details>
    <summary><strong>설정/로그아웃</strong></summary>
    <ul>
      <li>세션 종료(토큰 무효화), 보안/알림 설정</li>
    </ul>
  </details>

  <details>
    <summary><strong>피드백</strong></summary>
    <ul>
      <li>평점(예: VERY_LOW~)과 사유 저장, 운영 개선에 활용</li>
    </ul>
  </details>

  <p align="right"><a href="#top">맨 위로 ⤴</a></p>
</section>
