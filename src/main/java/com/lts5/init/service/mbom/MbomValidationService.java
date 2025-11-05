package com.lts5.init.service.mbom;

import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.IllegalArgumentException;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.MbomDto;
import com.lts5.init.entity.Mbom;
import com.lts5.init.repository.mbom.MbomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MbomValidationService {
    private final MbomRepository mbomRepository;
    private final GlobalMapper globalMapper;

    /**
     * 루트 아이템 데이터 정합성 검증 및 자동 수정
     * @param dto 검증할 MBOM DTO
     * @return 수정된 MBOM DTO
     */
    public MbomDto validateAndFixRootItemData(MbomDto dto) {
        // GlobalMapper를 사용하여 DTO 복사
        MbomDto resultDto = globalMapper.map(dto, MbomDto.class);
        
        if (resultDto.getParentItemId() == null) {
            // 부모 아이템이 없으면 완제품(루트)으로 설정
            resultDto.setIsRoot(true);
            
            // 완제품 관련 필드들을 null로 설정
            resultDto = clearRootItemFields(resultDto);
        } else {
            // 부모 아이템이 있으면 투입품으로 설정
            resultDto.setIsRoot(false);
            
            // 투입품의 경우 필수 검증
            if (resultDto.getParentProgressId() == null) {
                log.warn("투입품의 parent_progress_id가 null입니다 - itemId: {}, parentItemId: {}", 
                    resultDto.getItemId(), resultDto.getParentItemId());
            }
        }
        
        return resultDto;
    }
    
    /**
     * 완제품 관련 필드들을 null로 설정
     * @param dto MBOM DTO
     * @return 수정된 MBOM DTO
     */
    private MbomDto clearRootItemFields(MbomDto dto) {
        // GlobalMapper를 사용하여 DTO 복사
        MbomDto resultDto = globalMapper.map(dto, MbomDto.class);
        
        // 완제품 관련 필드들을 null로 설정
        resultDto.setParentProgressId(null);
        resultDto.setInputNum(null);
        resultDto.setInputUnitCode(null);
        resultDto.setInputUnit(null);
        
        return resultDto;
    }

    /**
     * MBOM 순환 참조 검증
     * @param dto 검증할 MBOM DTO
     * @return 검증 결과 (true: 순환 참조 없음, false: 순환 참조 있음)
     */
    public boolean validateCircularReference(MbomDto dto) {
        if (dto.getParentItemId() == null || dto.getItemId() == null) {
            return true; // 부모나 자식 아이템이 없으면 순환 참조 불가능
        }
        
        // 자기 자신을 참조하는 경우 체크
        if (dto.getParentItemId().equals(dto.getItemId())) {
            log.warn("자기 자신을 참조할 수 없습니다. 아이템 {}", dto.getItemId());
            return false;
        }
        
        // 기본적인 순환 참조 검증 (부모-자식 관계에서)
        if (dto.getParentItemId() != null && hasCircularReferenceSimple(dto.getParentItemId(), dto.getItemId())) {
            log.warn("순환 참조가 감지되었습니다. {} → {} 관계를 추가할 수 없습니다.", 
                dto.getParentItemId(), dto.getItemId());
            return false;
        }
        
        return true;
    }
    
    /**
     * MBOM 순환 참조 검증 (예외 발생 버전 - 기존 호환성 유지)
     * @param dto 검증할 MBOM DTO
     * @throws IllegalArgumentException 순환 참조가 발견된 경우
     */
    public void validateCircularReferenceWithException(MbomDto dto) {
        if (!validateCircularReference(dto)) {
            if (dto.getParentItemId() != null && dto.getParentItemId().equals(dto.getItemId())) {
                throw new IllegalArgumentException(ErrorCode.BAD_DATA_ERROR,
                    String.format("자기 자신을 참조할 수 없습니다. 아이템 %d", dto.getItemId()));
            } else {
                throw new IllegalArgumentException(ErrorCode.BAD_DATA_ERROR,
                    String.format("순환 참조가 감지되었습니다. %d → %d 관계를 추가할 수 없습니다.", 
                        dto.getParentItemId(), dto.getItemId()));
            }
        }
    }
    
    /**
     * 간단한 순환 참조 검증 (새로운 스키마용)
     */
    private boolean hasCircularReferenceSimple(Long parentItemId, Long childItemId) {
        Set<Long> visited = new HashSet<>();
        return isAncestorSimple(parentItemId, childItemId, visited);
    }
    
    /**
     * 간단한 상위 계층 검증
     */
    private boolean isAncestorSimple(Long parentItemId, Long targetItemId, Set<Long> visited) {
        if (visited.contains(parentItemId)) {
            return false; // 이미 방문한 노드면 순환 참조 아님
        }
        visited.add(parentItemId);
        
        if (parentItemId.equals(targetItemId)) {
            return true; // 순환 참조 발견
        }
        
        // 부모의 부모들을 재귀적으로 확인
        List<Mbom> parentMboms = mbomRepository.findByItemId(parentItemId);
        for (Mbom mbom : parentMboms) {
            if (mbom.getParentItemId() != null && 
                isAncestorSimple(mbom.getParentItemId(), targetItemId, visited)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * MBOM 관계 추가 가능 여부 검증
     * @param rootItemId 루트 아이템 ID
     * @param parentItemId 부모 아이템 ID
     * @param childItemId 자식 아이템 ID
     * @return 추가 가능 여부 (true: 가능, false: 불가능)
     */
    public boolean canAddRelation(Long rootItemId, Long parentItemId, Long childItemId) {
        // Root Item 직계인지 확인 (rootItemId == parentItemId)
        Long actualParentItemId = rootItemId.equals(parentItemId) ? null : parentItemId;
        
        // 임시 DTO 생성하여 검증
        MbomDto tempDto = new MbomDto();
        tempDto.setParentItemId(actualParentItemId);
        tempDto.setItemId(childItemId);
        tempDto.setIsRoot(actualParentItemId == null);
        
        // 순환 참조 검증 결과 반환
        return validateCircularReference(tempDto);
    }
}
