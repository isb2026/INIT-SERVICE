package com.lts5.init.service;

import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.EntityNotFoundException;
import com.primes.library.common.exceptions.AlreadyExistException;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.CodeDto;
import com.lts5.init.dto.CodeGroupDto;
import com.lts5.init.entity.Code;
import com.lts5.init.entity.CodeGroup;
import com.lts5.init.repository.code.CodeRepository;
import com.lts5.init.repository.codegroup.CodeGroupRepository;
import com.primes.library.service.BaseService;
import com.primes.library.util.NumberCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import com.primes.library.common.exceptions.BadDataException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CodeManagementService extends BaseService {
    private final CodeRepository codeRepository;
    private final CodeGroupRepository codeGroupRepository;
    private final NumberCodeGenerator numberCodeGenerator;
    private final GlobalMapper globalMapper;

    // Code 관련 메서드들
    @Transactional
    public CodeDto createCode(CodeDto dto) {
        CodeGroup codeGroup = codeGroupRepository.findByIdAndIsDeleteFalse(dto.getCodeGroupId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("Id 가 %d인 코드 그룹이 존재하지 않습니다.", dto.getCodeGroupId())));

        // 계층형 codeValue 자동 생성 (예: COM-004-001)
        String generatedCodeValue = generateHierarchicalCodeValue(codeGroup);
        dto.setCodeValue(generatedCodeValue);

        Code savedCode = codeRepository.save(globalMapper.map(dto, Code.class));
        return globalMapper.map(savedCode, CodeDto.class);
    }

    /**
     * 계층형 codeValue 자동 생성
     * @param codeGroup 코드 그룹
     * @return 생성된 계층형 codeValue (예: COM-004-001)
     */
    private String generateHierarchicalCodeValue(CodeGroup codeGroup) {
        String groupCode = codeGroup.getGroupCode();
        
        // 그룹에 속한 코드 개수 조회하여 다음 번호 생성
        NumberCodeGenerator.CodeGeneratorParam param =
                NumberCodeGenerator.CodeGeneratorParam.series(
                    codeRepository::countSeriesByCodeGroup, 
                    "three", 
                    codeGroup.getId()
                );

        String nextNumber = numberCodeGenerator.generate(param);
        
        // groupCode + "-" + nextNumber 형식으로 반환
        return groupCode + "-" + nextNumber;
    }
    
    @Transactional
    public CodeDto updateCode(Long id, CodeDto dto) {
        // 엔티티 조회
        Code code = codeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 코드 데이터가 없습니다.", id)));

        // 동적으로 필드 업데이트
        updateEntityFromDto(code, dto);

        // DTO로 변환 후 반환
        return globalMapper.map(code, CodeDto.class);
    }

    @Transactional
    public void deleteCode(Long id) {
        Code code = codeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 코드 데이터가 없습니다.", id)));
        code.delete();
        codeRepository.save(code);
    }

    /**
     * 전체 계층형 코드 트리 조회 (재귀 없이 groupCode 기반)
     * @return 모든 루트 그룹과 하위 그룹, 코드들을 포함한 트리 구조
     */
    public List<CodeGroupDto> getAllCodesTree() {
        // 1. 모든 루트 그룹들 조회
        List<CodeGroup> rootGroups = codeGroupRepository.findByIsRootAndIsDeleteFalse(true);
        
        return rootGroups.stream()
                .map(this::buildCodeGroupTreeByGroupCode)
                .toList();
    }

    /**
     * 코드 그룹의 완전한 트리 구조를 groupCode 기반으로 구성 (재귀 없음)
     * @param codeGroup 구성할 코드 그룹
     * @return 하위 그룹과 코드들을 포함한 완전한 DTO
     */
    private CodeGroupDto buildCodeGroupTreeByGroupCode(CodeGroup codeGroup) {
        // 기본 DTO 생성
        CodeGroupDto dto = globalMapper.map(codeGroup, CodeGroupDto.class);
        
        // groupCode로 자식 그룹들 조회 (예: COM -> COM-001, COM-002, COM-003)
        String rootGroupCode = codeGroup.getGroupCode();
        List<CodeGroup> childGroups = codeGroupRepository.findByGroupCodeStartingWithAndParentIdNotNullAndIsDeleteFalse(rootGroupCode + "-");
        List<CodeGroupDto> childDtos = childGroups.stream()
                .map(childGroup -> {
                    CodeGroupDto childDto = globalMapper.map(childGroup, CodeGroupDto.class);
                    // 각 자식 그룹에 속한 코드들 조회
                    List<Code> codes = codeRepository.findByCodeGroupAndIsDeleteFalse(childGroup);
                    List<CodeDto> codeDtos = codes.stream().map(code -> globalMapper.map(code, CodeDto.class)).toList();
                    childDto.setCodes(codeDtos);
                    return childDto;
                })
                .toList();
        
        // 현재 그룹에 속한 코드들 조회
        List<Code> codes = codeRepository.findByCodeGroupAndIsDeleteFalse(codeGroup);
        List<CodeDto> codeDtos = codes.stream().map(code -> globalMapper.map(code, CodeDto.class)).toList();
        
        // DTO에 자식 그룹과 코드 설정
        dto.setCodes(codeDtos);
        dto.setChildren(childDtos);
        
        return dto;
    }

    // CodeGroup 관련 메서드들
    @Transactional
    public CodeGroupDto createCodeGroup(CodeGroupDto dto) {
        // groupCode 중복 체크
        if (codeGroupRepository.existsByGroupCode(dto.getGroupCode())) {
            throw new AlreadyExistException(ErrorCode.ALREADY_EXIST_ERROR,
                String.format("그룹 코드 '%s'는 이미 존재합니다.", dto.getGroupCode()));
        }

        if(dto.getIsRoot() == true) {
            dto.setGroupCode(dto.getGroupCode());
        } else {
            // 부모 그룹 조회하여 계층형 groupCode 생성
            CodeGroup parentGroup = codeGroupRepository.findByIdAndIsDeleteFalse(dto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                        String.format("Id 가 %d인 부모 그룹이 존재하지 않습니다.", dto.getParentId())));
            
            NumberCodeGenerator.CodeGeneratorParam param =
                    NumberCodeGenerator.CodeGeneratorParam.series(
                            codeGroupRepository::countSeriesByParentId,
                            "three",
                            dto.getParentId()
                    );

            String generatedCodeValue = numberCodeGenerator.generate(param);
            // 부모의 groupCode + "-" + 생성된 번호 형식으로 설정
            dto.setGroupCode(parentGroup.getGroupCode() + "-" + generatedCodeValue);
        }

        CodeGroup codeGroup = globalMapper.map(dto, CodeGroup.class);
        CodeGroup savedCodeGroup = codeGroupRepository.save(codeGroup);
        return globalMapper.map(savedCodeGroup, CodeGroupDto.class);
    }
    
    @Transactional
    public CodeGroupDto updateCodeGroup(Long id, CodeGroupDto dto) {
        // 엔티티 조회
        CodeGroup codeGroup = codeGroupRepository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 코드그룹 데이터가 없습니다.", id)));

        // groupCode 중복 체크 (자신을 제외하고)
        if (!codeGroup.getGroupCode().equals(dto.getGroupCode()) && 
            codeGroupRepository.existsByGroupCode(dto.getGroupCode())) {
            throw new AlreadyExistException(ErrorCode.ALREADY_EXIST_ERROR, 
                String.format("그룹 코드 '%s'는 이미 존재합니다.", dto.getGroupCode()));
        }

        // 동적으로 필드 업데이트
        updateEntityFromDto(codeGroup, dto);

        // DTO로 변환 후 반환
        return globalMapper.map(codeGroup, CodeGroupDto.class);
    }

    @Transactional
    public void deleteCodeGroup(Long id) {
        CodeGroup codeGroup = codeGroupRepository.findByIdAndIsDeleteFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 코드그룹 데이터가 없습니다.", id)));
        codeGroup.delete();
        codeGroupRepository.save(codeGroup);
    }

    /**
     * 계층형 코드 조회 (ROOT-CHILD 형식) - groupCode 기반
     * @param rootGroupCode 루트 그룹 코드 (예: PRD)
     * @param childGroupCode 자식 그룹 코드 (예: 001)
     * @return 해당 자식 그룹에 속한 코드 목록
     */
    public List<CodeDto> getCodesByHierarchy(String rootGroupCode, String childGroupCode) {
        // 1. 루트 그룹 찾기 (is_root = true, parent_id = null)
        CodeGroup rootGroup = codeGroupRepository.findByGroupCodeAndIsRoot(rootGroupCode, true)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("루트 그룹 코드 '%s'를 찾을 수 없습니다.", rootGroupCode)));

        // 2. groupCode로 자식 그룹 찾기 (예: PRD-001)
        String fullChildGroupCode = rootGroupCode + "-" + childGroupCode;
        CodeGroup childGroup = codeGroupRepository.findByGroupCodeAndIsDeleteFalse(fullChildGroupCode)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("'%s' 그룹을 찾을 수 없습니다.", fullChildGroupCode)));

        // 3. 해당 그룹에 속한 코드들 조회
        List<Code> codes = codeRepository.findByCodeGroupAndIsDeleteFalse(childGroup);
        
        return codes.stream()
                .map(code -> globalMapper.map(code, CodeDto.class))
                .toList();
    }

    /**
     * 계층형 패스를 파싱하여 코드 조회
     * @param hierarchyPath 계층형 패스 (예: PRD, PRD-001, 또는 PRD-001-001)
     * @return PRD이면 List<CodeGroupDto>, PRD-001이면 List<CodeDto>, PRD-001-001이면 CodeDto
     */
    public Object getCodesByHierarchyPath(String hierarchyPath) {
        String[] parts = hierarchyPath.split("-");
        
        if (parts.length == 1) {
            // PRD 형태 → 루트 그룹의 자식 그룹들 반환
            return getChildGroupsByRoot(parts[0]);
        } else if (parts.length == 2) {
            // PRD-001 형태 → 코드 리스트 반환
            return getCodesByHierarchy(parts[0], parts[1]);
        } else if (parts.length == 3) {
            // PRD-001-001 형태 → 개별 코드 반환
            return getCodeByHierarchy(parts[0], parts[1], parts[2]);
        } else {
            throw new BadDataException(ErrorCode.BAD_DATA_ERROR,
                String.format("잘못된 계층형 패스 형식입니다. 올바른 형식: 'ROOT', 'ROOT-CHILD' 또는 'ROOT-CHILD-CODE' (입력값: %s)", hierarchyPath));
        }
    }

    /**
     * 루트 그룹의 자식 그룹들 조회 - groupCode 기반
     * @param rootGroupCode 루트 그룹 코드 (예: PRD)
     * @return 해당 루트 그룹의 자식 그룹 목록
     */
    public List<CodeGroupDto> getChildGroupsByRoot(String rootGroupCode) {
        // 1. 루트 그룹 찾기
        CodeGroup rootGroup = codeGroupRepository.findByGroupCodeAndIsRoot(rootGroupCode, true)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("루트 그룹 코드 '%s'를 찾을 수 없습니다.", rootGroupCode)));

        // 2. groupCode로 자식 그룹들 조회 (예: PRD-001, PRD-002, PRD-003)
        List<CodeGroup> childGroups = codeGroupRepository.findByGroupCodeStartingWithAndParentIdNotNullAndIsDeleteFalse(rootGroupCode + "-");
        
        return childGroups.stream()
                .map(codeGroup -> globalMapper.map(codeGroup, CodeGroupDto.class))
                .toList();
    }

    /**
     * 계층형 개별 코드 조회 (ROOT-CHILD-CODE 형식) - groupCode 기반
     * @param rootGroupCode 루트 그룹 코드 (예: PRD)
     * @param childGroupCode 자식 그룹 코드 (예: 001)
     * @param codeValue 코드 값 (예: 001)
     * @return 해당 개별 코드
     */
    public CodeDto getCodeByHierarchy(String rootGroupCode, String childGroupCode, String codeValue) {
        // 1. 루트 그룹 찾기
        CodeGroup rootGroup = codeGroupRepository.findByGroupCodeAndIsRoot(rootGroupCode, true)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("루트 그룹 코드 '%s'를 찾을 수 없습니다.", rootGroupCode)));

        // 2. groupCode로 자식 그룹 찾기 (예: PRD-001)
        String fullChildGroupCode = rootGroupCode + "-" + childGroupCode;
        CodeGroup childGroup = codeGroupRepository.findByGroupCodeAndIsDeleteFalse(fullChildGroupCode)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("'%s' 그룹을 찾을 수 없습니다.", fullChildGroupCode)));

        // 3. 해당 그룹에서 특정 코드 값을 가진 코드 찾기
        // codeValue가 COM-004-001 형식이므로 전체 codeValue로 조회
        String fullCodeValue = fullChildGroupCode + "-" + codeValue;
        Code code = codeRepository.findByCodeGroupAndCodeValueAndIsDeleteFalse(childGroup, fullCodeValue)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("'%s-%s' 그룹에서 코드 값 '%s'를 찾을 수 없습니다.", rootGroupCode, childGroupCode, fullCodeValue)));

        return globalMapper.map(code, CodeDto.class);
    }

    /**
     * 계층형 코드 조회 (ROOT-CHILD-CODE 형식) - 전체 codeValue 기반
     * @param hierarchyPath 계층형 경로 (예: COM-004-001)
     * @return 해당 개별 코드
     */
    public CodeDto getCodeByFullHierarchyPath(String hierarchyPath) {
        // 전체 codeValue로 코드 직접 찾기 (예: COM-004-001)
        Code code = codeRepository.findByCodeValueAndIsDeleteFalse(hierarchyPath)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, 
                    String.format("코드 값 '%s'를 찾을 수 없습니다.", hierarchyPath)));

        return globalMapper.map(code, CodeDto.class);
    }
} 