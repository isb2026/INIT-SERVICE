package com.lts5.init.service;

import com.primes.library.common.codes.ErrorCode;
import com.primes.library.common.exceptions.EntityNotFoundException;
import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.TerminalDto;
import com.lts5.init.entity.Terminal;
import com.lts5.init.payload.request.terminal.TerminalSearchRequest;
import com.lts5.init.repository.terminal.TerminalRepository;
import com.primes.library.util.DynamicFieldQueryUtil;
import com.primes.library.util.NumberCodeGenerator;
import com.primes.library.filter.TenantContext;
import com.primes.library.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TerminalService extends BaseService {
    private final TerminalRepository terminalRepository;
    private final DynamicFieldQueryUtil dynamicFieldQueryUtil;
    private final NumberCodeGenerator numberCodeGenerator;
    private final GlobalMapper globalMapper;

    @Transactional
    public TerminalDto create(TerminalDto dto) {
        // terminalCode가 없으면 자동 생성
        if (dto.getTerminalCode() == null || dto.getTerminalCode().trim().isEmpty()) {
            String generatedTerminalCode = generateTerminalCode();
            dto.setTerminalCode(generatedTerminalCode);
        }
        
        Terminal entity = globalMapper.map(dto, Terminal.class);
        Terminal savedEntity = terminalRepository.save(entity);
        return globalMapper.map(savedEntity, TerminalDto.class);
    }
    
    @Transactional
    public List<TerminalDto> createList(List<TerminalDto> dtos) {
        // 각 DTO에 terminalCode 자동 생성
        dtos.forEach(dto -> {
            if (dto.getTerminalCode() == null || dto.getTerminalCode().trim().isEmpty()) {
                String generatedTerminalCode = generateTerminalCode();
                dto.setTerminalCode(generatedTerminalCode);
            }
        });
        
        List<Terminal> entities = dtos.stream()
                .map(dto -> globalMapper.map(dto, Terminal.class))
                .toList();
        
        List<Terminal> savedEntities = terminalRepository.saveAll(entities);
        return savedEntities.stream()
                .map(entity -> globalMapper.map(entity, TerminalDto.class))
                .toList();
    }
    
    @Transactional
    public TerminalDto update(Long id, TerminalDto dto) {
        dto.setId(id);  // DTO에 ID 설정
        return updateSingle(dto);
    }
    
    @Transactional
    public List<TerminalDto> updateAll(List<TerminalDto> dtos) {
        return dtos.stream()
                .map(this::updateSingle)
                .toList();
    }

    @Transactional
    public void delete(List<Long> ids) {
        List<Terminal> existingEntities = terminalRepository.findAllById(ids);
        
        if (existingEntities.size() != ids.size()) {
            List<Long> existingIds = existingEntities.stream().map(Terminal::getId).toList();
            List<Long> notFoundIds = new ArrayList<>(ids);
            notFoundIds.removeAll(existingIds);

            throw new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR,
                String.format("Id가 %s인 Terminal 데이터가 없습니다.", notFoundIds));
        }

        for (Terminal entity : existingEntities) {
            entity.delete();
        }
        terminalRepository.saveAll(existingEntities);
    }

    public List<?> getFieldValues(String fieldName, TerminalSearchRequest searchRequest) {
        if (searchRequest == null) {
            return dynamicFieldQueryUtil.getFieldValues(Terminal.class, fieldName);
        }else{
            return dynamicFieldQueryUtil.getFieldValuesWithFilter(Terminal.class,  fieldName, searchRequest);
        }
    }

    public Page<TerminalDto> search(TerminalSearchRequest searchRequest, Pageable pageable) {
        return terminalRepository.search(searchRequest, pageable)
                .map(entity -> globalMapper.map(entity, TerminalDto.class));
    }

    public List<TerminalDto> findAll(TerminalSearchRequest searchRequest) {
        // 페이징 없이 전체 리스트 반환
        return terminalRepository.searchAll(searchRequest)
                .stream()
                .map(entity -> globalMapper.map(entity, TerminalDto.class))
                .toList();
    }

    // ==================== 유틸리티 메서드 ====================

    /**
     * 테넌트 단위로 Terminal 코드 자동 생성
     */
    private String generateTerminalCode() {
        Short tenantId = TenantContext.getTenantId();
        
        NumberCodeGenerator.CodeGeneratorParam<Short> param =
                NumberCodeGenerator.CodeGeneratorParam.series(
                    this::countTerminalsByTenant, 
                    "three", 
                    tenantId
                );

        String nextNumber = numberCodeGenerator.generate(param);
        return "T" + nextNumber; // T001, T002, T003 형식
    }

    /**
     * 현재 테넌트의 Terminal 개수 조회
     */
    private Long countTerminalsByTenant(Short tenantId) {
        return terminalRepository.countByTenantIdAndIsDeleteFalse(tenantId);
    }

    private TerminalDto updateSingle(TerminalDto dto) {
        Terminal entity = terminalRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND_ERROR, String.format("Id 가 %d인 Terminal 데이터가 없습니다.", dto.getId())));
        updateEntityFromDto(entity, dto);
        return globalMapper.map(entity, TerminalDto.class);
    }
}