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
![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/dc4d4130-19d7-46f9-8ba4-f306cca840c4/image.png)

- 3사 쇼핑몰에서 Top 100 상품 정보들을 크롤링 후, 데이터베이스에 저장이 용이하도록 전처리 후 저장
- 전처리 과정에서 각 상품의 Rank Score 계산
    - Rank Score는 해당 날짜의 상품의 랭크를 기준으로 0~1의 값을 가지며, 순위가 높을수록 값이 커짐
    - 각 날짜의 **Rank Score 합산값(노출 지수)을 통해 일정 기간 동안 상품 인기도 파악 가능**

#### DAG 구성
![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/8b8b3f72-57e4-4c15-8346-ccec256c4e16/image.png)
![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/af9b889f-2c65-453d-ae86-4210a808f522/image.png)

- **각 쇼핑몰 별 DAG 구성**을 통해 **데이터 파이프라인 자동화** 및 모니터링 가능
- Extract, Transform, Load의 세 단계(Task)로 분리하여 DAG 구성

## 서비스 화면

### Home
![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/c022d012-67ba-4448-bb7d-e19ba482c326/image.png)

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
![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/410e2750-0105-40a6-b03d-65414a430f72/image.png)

- **상품 정보 표시 및 정렬 기능**
    - 이미지, 상품명, 쇼핑몰, 브랜드, 노출 지수, 카테고리, 고정가, 할인가 제공
    - 브랜드, 노출 지수, 고정가, 할인가 필드는 **오름차순/내림차순 정렬 가능**
    - 정렬에는 **QueryDSL의 동적 쿼리** 활용:
        - **SortBy**: 정렬 기준 컬럼
        - **SortOrder**: 오름차순/내림차순 설정
    - 상품 클릭 시 상세 화면으로 이동

#### Styles 필터
![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/a0942a3c-2008-4d30-8a4c-42a1620fa08b/image.png)

- **필터 적용 가능**
    - 쇼핑몰, 정렬 기준, 기간, 카테고리, 브랜드 선택
    - 쇼핑몰마다 카테고리/브랜드가 다르므로 API를 활용하여 목록 제공

#### Styles 상세
![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/ac1a078d-e98e-484d-8209-65d35dc227c4/image.png)

- 특정 상품의 **이미지, 기본 정보, 추가 정보, 리뷰 트렌드 제공**
    - 리뷰 트렌드는 **기간/별점 수** 조건에 맞는 데이터를 제공하며, **QueryDSL 동적 쿼리** 활용

### 이미지 검색
![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/a341e72a-53da-4b08-b88b-86c0e32efcdd/image.png)

- 쇼핑몰, 카테고리, 검색 결과 이미지 개수 설정 가능
- 사용자가 이미지를 업로드하면 **이미지에서 Noise 제거 및 코사인 유사도를 계산**하여 유사한 상품 제공
- 상품 클릭 시 **Styles 상세 화면**으로 이동
