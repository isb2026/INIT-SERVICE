package com.lts5.init.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressInfo {
    
    private Long id;                    // 공정 ID
    private String progressName;        // 공정명
    private Byte progressOrder;         // 공정 순서
    private String progressTypeCode;    // 공정 타입 코드
    private String progressTypeName;    // 공정 타입명
    private Boolean isOutsourcing;      // 외주 여부
    private Float defaultCycleTime;     // 기본 사이클 타임
    private String progressDefaultSpec; // 기본 스펙
    
    // 추가 정보들 (필요시)
    private String keyManagementContents;  // 핵심 관리 내용
}
