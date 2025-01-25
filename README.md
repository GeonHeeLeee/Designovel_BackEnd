
  ##  **주요 기능**
        - **ETL 자동화 파이프라인 구축**
            - Apache Airflow를 활용한 3사 쇼핑몰(Musinsa, Handsome, WConcept) **데이터 매일 크롤링, 전처리 및 저장(ETL) 자동화**
        - **통합 트렌드 분석**
            - 수집된 데이터를 가공하고 통합하여 **유의미한 지표(노출 지수, 가격대 별 상품, 리뷰 트렌드 등) 도출** 후 시각화
        - **이미지 검색**
            - 사용자가 입력한 이미지를 저장된 상품 이미지와 **코사인 유사도 연산을 통해 유사한 상품 제공**
            - 데이터 적재 과정에서 상품 이미지의 **Noise(배경, 모델 등)을 제거한 순수한 상품 이미지를 벡터화**하여 저장

          
  ## **서비스 설명**
    
  ###  **Airflow를 통한 ETL 자동화**
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/dc4d4130-19d7-46f9-8ba4-f306cca840c4/image.png)
        
        - 3사 쇼핑몰에서 Top 100 상품 정보들을 크롤링 후, 데이터베이스에 저장이 용이하도록 전처리 후 저장
        - 전처리 과정에서 각 상품의 Rank Score 계산
            - Rank Score는 해당 날짜의 상품의 랭크
            - 0~1의 값을 가지며, 순위가 높을 수록 값이 커짐
            - 각 날짜의 **Rank Score의 합산을 노출 지수라 하며, 노출 지수를 이용해 일정 기간 동안 해당 상품의 인기도를 파악** 가능
        
        **DAG 구성**
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/8b8b3f72-57e4-4c15-8346-ccec256c4e16/image.png)
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/af9b889f-2c65-453d-ae86-4210a808f522/image.png)
        
        - **각 쇼핑몰 별 DAG를 구성**하여 **데이터 파이프라인 자동화** 및 모니터링 가능
        - Extract, Transform, Load 세개의 과정(Task)로 분리하여 DAG를 구성
        
  ## **서비스 화면**
        
  ###   **Home**
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/c022d012-67ba-4448-bb7d-e19ba482c326/image.png)
        
        - **가격대 별 상품 수**
            - 매일 상품의 데이터가 저장되므로, 이를 가공하여 가격대 별 상품 수를 제공
            - 필터는 **QueryDSL을 사용한 동적 쿼리**로, 아래의 값을 입력 받음
                - 쇼핑몰
                - 카테고리
                - 시작일
                - 종료일
        - **Top 10 브랜드**
            - Top 10 브랜드의 필터도 가격대 별 상품 수와 같은 값을 입력 받음
            - 해당 필터의 조건에서 브랜드의 **각 상품들의 노출 지수 합산으로 순위 결정**
            
        - PS. 가격대 별 상품 수는 프로젝트 초기에 쿼리 속도가 8초가 걸렸었다. **데이터가 약 100만개 이상이였고, 쿼리 자체에서 Case문을 사용하여 인덱스를 활용하지 않아** 매우 속도가 느렸었다. 이를 후에 **DB 쿼리에서 처리하는게 아닌, Spring 어플리케이션에서 처리하도록 변경**하여 쿼**리 속도를 비약적으로 단축**시켰다.
        
### **Styles**
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/410e2750-0105-40a6-b03d-65414a430f72/image.png)
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/7a0ab604-09fb-4b2f-b76a-29b63fe443ea/image.png)
        
        - **필터를 적용 혹은 적용하지 않고 상품의 다양한 정보를 표시**하는 화면
            - 이미지/상품명/쇼핑몰/브랜드/노출 지수/카테고리/고정가/할인가
            - 브랜드/노출 지수/고정가/할인가는 **오름차순, 내림차순 정렬이 가능**
                - **QueryDSL의 동적 쿼리를 활용**하여 Front-end에서 두 개의 필드를 입력 받음
                - **SortBy** - 어떠한 컬럼을 정렬할 것인지
                - **SortOrder** - 오름차순/내림차순
            - 해당 상품을 클릭하면 상품의 상세 화면으로 이동
        
### **Styles 필터**
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/a0942a3c-2008-4d30-8a4c-42a1620fa08b/image.png)
        
        - **Styles의 필터 적용 화면**
            - 쇼핑몰/정렬 기준/기간/카테고리/브랜드 선택 가능
            - 쇼핑몰마다 카**테고리/브랜드가 다르므로 API를 이용**해 목록을 불러옴
            - **QueryDSL의 동적 쿼리를 활용**
        
### **Styles 상세**
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/ac1a078d-e98e-484d-8209-65d35dc227c4/image.png)
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/bdde27a6-2e3e-44ba-9474-ac9b98ce71e1/image.png)
        
        - 특정 상품의 **이미지/기본 정보/추가 정보/리뷰 트렌드 제공**
            - **쇼핑몰마다 고유 데이터가 다르므로,** 가격/상품 이름 이외의 추가 정보들은 쇼핑몰마다 다르게 제공
            - 리뷰 트렌드
                - QueryDSL을 활용한 동적 쿼리로 **기간/별점 수를 입력 받아 해당 조건에 맞는 리뷰만 제공**
        
### **이미지 검색**
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/a341e72a-53da-4b08-b88b-86c0e32efcdd/image.png)
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/4444a8a1-74aa-4739-8acd-6d9f0e08145d/image.png)
        
        ![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/14e004ed-a5cd-4396-8560-b24091ed4d0b/8fcd3fbb-8ef7-47bd-9e14-d9577fe3b26e/image.png)
        
        - 쇼핑몰/카테고리/제공 받을 이미지 개수 설정 가능
        - 사용자가 이미지를 업로드하고 카테고리를 선택하면 **이미지에서 해당 카테고리 외 Noise를 제거하고 데이터베이스에 저장된 이미지들에서 코사인 유사도를 계산**하여 비슷한 상품 제공(카테고리 미입력 시 ‘의류’로 프롬프트가 설정)
        - **이미지 클릭 시, 해당 상품의 상세 정보로 이동**(Styles 상세 화면)
