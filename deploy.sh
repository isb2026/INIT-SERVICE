# ECR 에 이미지를 푸시하는 스크립트
# CICD 를 거치치 않은 빠른 배포 및 테스트를 위함이고, 개발 기간 중에만 사용바람
# 강동현 매니저 2025-08-14

docker build -t init-service:latest .
docker tag init-service:latest 855607364597.dkr.ecr.ap-northeast-2.amazonaws.com/primes/init-service:latest

aws ecr get-login-password --region ap-northeast-2 \
| docker login --initname AWS --password-stdin 855607364597.dkr.ecr.ap-northeast-2.amazonaws.com
docker push 855607364597.dkr.ecr.ap-northeast-2.amazonaws.com/primes/init-service:latest