#!/bin/bash

# Google Chat 알림 스크립트
# 사용법: ./google-chat-notification.sh <webhook_url> <message_type> [additional_info]

set -e

WEBHOOK_URL="$1"
MESSAGE_TYPE="$2"
ADDITIONAL_INFO="$3"

if [ -z "$WEBHOOK_URL" ] || [ -z "$MESSAGE_TYPE" ]; then
    echo "사용법: $0 <webhook_url> <message_type> [additional_info]"
    echo "message_type: success, failure, merge_request, deployment"
    exit 1
fi

# 색상 및 아이콘 설정
case "$MESSAGE_TYPE" in
    "success")
        COLOR="#00FF00"
        ICON="✅"
        TITLE="빌드 성공"
        ;;
    "failure")
        COLOR="#FF0000"
        ICON="❌"
        TITLE="빌드 실패"
        ;;
    "merge_request")
        COLOR="#0099FF"
        ICON="🔀"
        TITLE="새로운 Merge Request"
        ;;
    "deployment")
        COLOR="#FF9900"
        ICON="🚀"
        TITLE="배포 완료"
        ;;
    *)
        COLOR="#808080"
        ICON="ℹ️"
        TITLE="알림"
        ;;
esac

# GitLab CI 환경 변수에서 정보 추출
PROJECT_NAME="${CI_PROJECT_NAME:-Unknown Project}"
BRANCH_NAME="${CI_COMMIT_REF_NAME:-Unknown Branch}"
COMMIT_SHA="${CI_COMMIT_SHORT_SHA:-Unknown}"
COMMIT_MESSAGE="${CI_COMMIT_MESSAGE:-No message}"
PIPELINE_URL="${CI_PIPELINE_URL:-}"
MERGE_REQUEST_URL="${CI_MERGE_REQUEST_PROJECT_URL:-}/merge_requests/${CI_MERGE_REQUEST_IID:-}"
# 작성자 정보 우선순위: GITLAB_USER_NAME > CI_COMMIT_AUTHOR_NAME > Unknown Author
AUTHOR_NAME="${GITLAB_USER_NAME:-${CI_COMMIT_AUTHOR_NAME:-Unknown Author}}"
JOB_NAME="${CI_JOB_NAME:-Unknown Job}"

# 디버그 정보 출력
echo "디버그 정보:"
echo "GITLAB_USER_NAME: ${GITLAB_USER_NAME:-없음}"
echo "CI_COMMIT_AUTHOR_NAME: ${CI_COMMIT_AUTHOR_NAME:-없음}"
echo "최종 AUTHOR_NAME: $AUTHOR_NAME"

# 메시지 내용 구성
case "$MESSAGE_TYPE" in
    "success")
        MESSAGE="$ICON $TITLE\n\n"
        MESSAGE+="프로젝트: $PROJECT_NAME\n"
        MESSAGE+="브랜치: $BRANCH_NAME\n"
        MESSAGE+="커밋: $COMMIT_SHA\n"
        MESSAGE+="작성자: ${GITLAB_USER_NAME:-${CI_COMMIT_AUTHOR_NAME:-Unknown Author}}\n"
        MESSAGE+="작업: $JOB_NAME\n"
        if [ -n "$PIPELINE_URL" ]; then
            MESSAGE+="\n파이프라인: $PIPELINE_URL"
        fi
        ;;
    "failure")
        MESSAGE="$ICON $TITLE\n\n"
        MESSAGE+="프로젝트: $PROJECT_NAME\n"
        MESSAGE+="브랜치: $BRANCH_NAME\n"
        MESSAGE+="커밋: $COMMIT_SHA\n"
        MESSAGE+="작성자: ${GITLAB_USER_NAME:-${CI_COMMIT_AUTHOR_NAME:-Unknown Author}}\n"
        MESSAGE+="실패한 작업: $JOB_NAME\n"
        MESSAGE+="오류 정보: $ADDITIONAL_INFO\n"
        if [ -n "$PIPELINE_URL" ]; then
            MESSAGE+="\n파이프라인: $PIPELINE_URL"
        fi
        ;;
    "merge_request")
        MESSAGE="$ICON $TITLE\n\n"
        MESSAGE+="프로젝트: $PROJECT_NAME\n"
        MESSAGE+="브랜치: $BRANCH_NAME → ${CI_MERGE_REQUEST_TARGET_BRANCH_NAME:-main}\n"
        MESSAGE+="작성자: ${GITLAB_USER_NAME:-${CI_COMMIT_AUTHOR_NAME:-Unknown Author}}\n"
        MESSAGE+="제목: ${CI_MERGE_REQUEST_TITLE:-No title}\n"
        if [ -n "$CI_MERGE_REQUEST_IID" ]; then
            MESSAGE+="\nMerge Request: $MERGE_REQUEST_URL"
        fi
        ;;
    "deployment")
        MESSAGE="$ICON $TITLE\n\n"
        MESSAGE+="프로젝트: $PROJECT_NAME\n"
        MESSAGE+="환경: ${CI_ENVIRONMENT_NAME:-Production}\n"
        MESSAGE+="이미지: $ADDITIONAL_INFO\n"
        MESSAGE+="배포자: ${GITLAB_USER_NAME:-${CI_COMMIT_AUTHOR_NAME:-Unknown Author}}\n"
        if [ -n "$PIPELINE_URL" ]; then
            MESSAGE+="\n파이프라인: $PIPELINE_URL"
        fi
        ;;
esac

# Google Chat 메시지 JSON 구성
JSON_PAYLOAD=$(cat <<EOF
{
  "cards": [
    {
      "header": {
        "title": "$TITLE",
        "subtitle": "$PROJECT_NAME",
        "imageUrl": "https://about.gitlab.com/images/press/logo/png/gitlab-logo-gray-rgb.png"
      },
      "sections": [
        {
          "widgets": [
            {
              "textParagraph": {
                "text": "$MESSAGE"
              }
            }
          ]
        }
      ]
    }
  ]
}
EOF
)

# Google Chat으로 메시지 전송
echo "Google Chat으로 알림 전송 중..."
echo "메시지 타입: $MESSAGE_TYPE"

RESPONSE=$(curl -s -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d "$JSON_PAYLOAD" \
    "$WEBHOOK_URL")

HTTP_CODE="${RESPONSE: -3}"
RESPONSE_BODY="${RESPONSE%???}"

if [ "$HTTP_CODE" = "200" ]; then
    echo "✅ Google Chat 알림이 성공적으로 전송되었습니다."
else
    echo "❌ Google Chat 알림 전송 실패 (HTTP $HTTP_CODE)"
    echo "응답: $RESPONSE_BODY"
    exit 1
fi