## 소개

### 주요 기능
- **ETL 자동화 파이프라인 구축**
    - Apache Airflow를 활용한 3사 쇼핑몰(Musinsa, Handsome, WConcept) **데이터 매일 크롤링, 전처리 및 저장(ETL) 자동화**
- **통합 트렌드 분석**
    - 수집된 데이터를 가공하고 통합하여 **유의미한 지표(노출 지수, 가격대 별 상품, 리뷰 트렌드 등) 도출** 후 시각화
- **이미지 검색**
    - 사용자가 입력한 이미지를 저장된 상품 이미지와 **코사인 유사도 연산을 통해 유사한 상품 제공**
    - 데이터 적재 과정에서 상품 이미지의 **Noise(배경, 모델 등)을 제거한 순수한 상품 이미지를 벡터화**하여 저장

### 서비스 설명

#### Airflow를 통한 ETL 자동화
![image](https://github.com/user-attachments/assets/f8393c2a-7e60-419e-b100-bd8e8084358f)



- 3사 쇼핑몰에서 Top 100 상품 정보들을 크롤링 후, 데이터베이스에 저장이 용이하도록 전처리 후 저장
- 전처리 과정에서 각 상품의 Rank Score 계산
    - Rank Score는 해당 날짜의 상품의 랭크를 기준으로 0~1의 값을 가지며, 순위가 높을수록 값이 커짐
    - 각 날짜의 **Rank Score 합산값(노출 지수)을 통해 일정 기간 동안 상품 인기도 파악 가능**

#### DAG 구성
![image](https://github.com/user-attachments/assets/12943818-2d2a-4e07-9f84-a3c96dcf5cf5)
![image](https://github.com/user-attachments/assets/4acc0b08-b1fe-4ba5-8d97-ab4ac385d088)


- **각 쇼핑몰 별 DAG 구성**을 통해 **데이터 파이프라인 자동화** 및 모니터링 가능
- Extract, Transform, Load의 세 단계(Task)로 분리하여 DAG 구성

## 서비스 화면

### Home
![image](https://github.com/user-attachments/assets/940ab932-f886-4731-8e77-624bfae35f7d)


- **가격대 별 상품 수**
    - 매일 상품 데이터를 저장하고 가공하여 가격대 별 상품 수 제공
    - 필터는 **QueryDSL을 사용한 동적 쿼리**로 아래 값을 입력받음:
        - 쇼핑몰
        - 카테고리
        - 시작일
        - 종료일
- **Top 10 브랜드**
    - Top 10 브랜드는 가격대 별 상품 수와 동일한 필터를 사용
    - 브랜드 순위는 상품들의 **노출 지수 합산값**을 기준으로 결정

> PS: 가격대 별 상품 수 쿼리 속도 최적화  
> 초기 쿼리 속도가 8초 이상 소요되었으나, 데이터를 Spring 애플리케이션에서 처리하도록 변경하여 **쿼리 속도를 비약적으로 단축**함.

### Styles
![image](https://github.com/user-attachments/assets/c6058ccd-2728-41eb-b0f1-d714901671fc)
![image](https://github.com/user-attachments/assets/d70e452e-8086-41f8-9ebc-590134d50ea1)


- **상품 정보 표시 및 정렬 기능**
    - 이미지, 상품명, 쇼핑몰, 브랜드, 노출 지수, 카테고리, 고정가, 할인가 제공
    - 브랜드, 노출 지수, 고정가, 할인가 필드는 **오름차순/내림차순 정렬 가능**
    - 정렬에는 **QueryDSL의 동적 쿼리** 활용:
        - **SortBy**: 정렬 기준 컬럼
        - **SortOrder**: 오름차순/내림차순 설정
    - 상품 클릭 시 상세 화면으로 이동

#### Styles 필터
![image](https://github.com/user-attachments/assets/665ee3c7-6d35-4398-8586-f2385780c3f1)


- **필터 적용 가능**
    - 쇼핑몰, 정렬 기준, 기간, 카테고리, 브랜드 선택
    - 쇼핑몰마다 카테고리/브랜드가 다르므로 API를 활용하여 목록 제공

#### Styles 상세
![image](https://github.com/user-attachments/assets/72939ce3-c399-42bd-8b87-e30e978d3d8f)
![image](https://github.com/user-attachments/assets/e425a420-4cba-4982-9fde-7be0e0c07e71)


- 특정 상품의 **이미지, 기본 정보, 추가 정보, 리뷰 트렌드 제공**
    - 리뷰 트렌드는 **기간/별점 수** 조건에 맞는 데이터를 제공하며, **QueryDSL 동적 쿼리** 활용

### 이미지 검색
![image](https://github.com/user-attachments/assets/58f9cd56-83fc-4a83-860a-094acaed7083)
![image](https://github.com/user-attachments/assets/2e36358c-e940-4214-9e75-f7a3ac3e63d7)
![image](https://github.com/user-attachments/assets/300ff717-e7b6-4923-97ae-ef0b49027688)


- 쇼핑몰, 카테고리, 검색 결과 이미지 개수 설정 가능
- 사용자가 이미지를 업로드하면 **이미지에서 Noise 제거 및 코사인 유사도를 계산**하여 유사한 상품 제공
- 상품 클릭 시 **Styles 상세 화면**으로 이동
