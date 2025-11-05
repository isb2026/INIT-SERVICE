#!/bin/bash

# ECS 자동 배포 스크립트
# 사용법: ./deploy-to-ecs.sh <service-name> <image-uri> [cluster-name]

set -e

# 색상 정의
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 로그 함수
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
    exit 1
}

# 파라미터 확인
SERVICE_PREFIX=${1:-"primes-init-service"}
IMAGE_URI=${2}
CLUSTER_NAME=${3:-"primes"}
REGION=${AWS_DEFAULT_REGION:-"ap-northeast-2"}

if [ -z "$IMAGE_URI" ]; then
    log_error "이미지 URI가 필요합니다. 사용법: $0 <service-prefix> <image-uri> [cluster-name]"
fi

log_info "ECS 배포 시작"
log_info "서비스 접두사: $SERVICE_PREFIX"
log_info "클러스터: $CLUSTER_NAME"
log_info "이미지: $IMAGE_URI"
log_info "리전: $REGION"

# AWS CLI 및 권한 확인
log_info "AWS 환경 확인 중..."
aws sts get-caller-identity > /dev/null || log_error "AWS 자격 증명이 유효하지 않습니다"

# 클러스터 존재 확인
log_info "ECS 클러스터 확인 중..."
if ! aws ecs describe-clusters --clusters "$CLUSTER_NAME" --region "$REGION" --query 'clusters[0].status' --output text | grep -q "ACTIVE"; then
    log_error "클러스터 '$CLUSTER_NAME'을 찾을 수 없거나 활성 상태가 아닙니다"
fi

# 서비스명 동적 검색
log_info "서비스명 검색 중..."
SERVICE_NAME=$(aws ecs list-services --cluster "$CLUSTER_NAME" --region "$REGION" --query 'serviceArns[]' --output text | tr '\t' '\n' | grep -o "[^/]*$" | grep "^${SERVICE_PREFIX}" | head -1)

if [ -z "$SERVICE_NAME" ]; then
    log_error "서비스 접두사 '$SERVICE_PREFIX'로 시작하는 서비스를 찾을 수 없습니다"
fi

log_info "찾은 서비스: $SERVICE_NAME"

# 서비스 존재 확인
log_info "ECS 서비스 확인 중..."
SERVICE_EXISTS=$(aws ecs describe-services --cluster "$CLUSTER_NAME" --services "$SERVICE_NAME" --region "$REGION" --query 'services[0].status' --output text 2>/dev/null || echo "NOT_FOUND")

if [ "$SERVICE_EXISTS" = "NOT_FOUND" ] || [ "$SERVICE_EXISTS" = "None" ]; then
    log_error "서비스 '$SERVICE_NAME'이 존재하지 않거나 접근할 수 없습니다"
fi

# 현재 Task Definition 가져오기
log_info "현재 Task Definition 가져오는 중..."
CURRENT_TASK_DEF=$(aws ecs describe-services --cluster "$CLUSTER_NAME" --services "$SERVICE_NAME" --region "$REGION" --query 'services[0].taskDefinition' --output text)

if [ -z "$CURRENT_TASK_DEF" ] || [ "$CURRENT_TASK_DEF" = "None" ]; then
    log_error "현재 Task Definition을 가져올 수 없습니다"
fi

log_info "현재 Task Definition: $CURRENT_TASK_DEF"

# Task Definition 상세 정보 가져오기
log_info "Task Definition 상세 정보 가져오는 중..."
aws ecs describe-task-definition --task-definition "$CURRENT_TASK_DEF" --region "$REGION" --query 'taskDefinition' > /tmp/current-task-def.json

# 새로운 Task Definition 생성 (이미지 업데이트)
log_info "새로운 Task Definition 생성 중..."
jq --arg IMAGE "$IMAGE_URI" '
    .containerDefinitions[0].image = $IMAGE |
    del(.taskDefinitionArn, .revision, .status, .requiresAttributes, .placementConstraints, .compatibilities, .registeredAt, .registeredBy)
' /tmp/current-task-def.json > /tmp/new-task-def.json

# 새 Task Definition 등록
log_info "새 Task Definition 등록 중..."
NEW_TASK_DEF_ARN=$(aws ecs register-task-definition --cli-input-json file:///tmp/new-task-def.json --region "$REGION" --query 'taskDefinition.taskDefinitionArn' --output text)

if [ -z "$NEW_TASK_DEF_ARN" ]; then
    log_error "새 Task Definition 등록에 실패했습니다"
fi

log_success "새 Task Definition 등록 완료: $NEW_TASK_DEF_ARN"

# 서비스 업데이트
log_info "ECS 서비스 업데이트 중..."
aws ecs update-service \
    --cluster "$CLUSTER_NAME" \
    --service "$SERVICE_NAME" \
    --task-definition "$NEW_TASK_DEF_ARN" \
    --region "$REGION" > /dev/null

log_success "서비스 업데이트 요청 완료"

# 배포 상태 모니터링
log_info "배포 상태 모니터링 중..."
for i in {1..60}; do
    DEPLOYMENT_STATUS=$(aws ecs describe-services --cluster "$CLUSTER_NAME" --services "$SERVICE_NAME" --region "$REGION" --query 'services[0].deployments[0].status' --output text 2>/dev/null || echo "UNKNOWN")
    RUNNING_COUNT=$(aws ecs describe-services --cluster "$CLUSTER_NAME" --services "$SERVICE_NAME" --region "$REGION" --query 'services[0].runningCount' --output text 2>/dev/null || echo "0")
    DESIRED_COUNT=$(aws ecs describe-services --cluster "$CLUSTER_NAME" --services "$SERVICE_NAME" --region "$REGION" --query 'services[0].desiredCount' --output text 2>/dev/null || echo "1")

    log_info "배포 상태: $DEPLOYMENT_STATUS, 실행 중: $RUNNING_COUNT/$DESIRED_COUNT (${i}/60)"

    if [[ "$DEPLOYMENT_STATUS" == "PRIMARY" && "$RUNNING_COUNT" == "$DESIRED_COUNT" ]]; then
        log_success "배포가 성공적으로 완료되었습니다!"
        break
    elif [[ "$DEPLOYMENT_STATUS" == "FAILED" ]]; then
        log_error "배포가 실패했습니다"
    fi

    if [[ $i -eq 60 ]]; then
        log_warning "배포 완료 대기 시간이 초과되었습니다. 수동으로 상태를 확인하세요."
        break
    fi

    sleep 10
done

# 배포 결과 요약
log_info "배포 결과 요약:"
echo "  - 서비스: $SERVICE_NAME"
echo "  - 클러스터: $CLUSTER_NAME"
echo "  - 새 Task Definition: $NEW_TASK_DEF_ARN"
echo "  - 이미지: $IMAGE_URI"
echo "  - 배포 시간: $(date)"

# 임시 파일 정리
rm -f /tmp/current-task-def.json /tmp/new-task-def.json

log_success "ECS 배포 스크립트 실행 완료!"