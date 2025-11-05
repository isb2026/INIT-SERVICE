package com.lts5.init.service.mbom;

import com.primes.library.common.mapper.GlobalMapper;
import com.lts5.init.dto.MbomTreeDto;
import com.lts5.init.dto.ProcessTreeNodeDto;
import com.lts5.init.dto.FullBomTreeDto;
import com.lts5.init.dto.RootItemTreeDto;
import com.lts5.init.dto.ProcessNodeDto;
import com.lts5.init.dto.ProductInfoDto;
import com.lts5.init.dto.InputItemDto;
import com.lts5.init.entity.Mbom;
import com.lts5.init.entity.ItemProgress;
import com.lts5.init.entity.Item;
import com.lts5.init.repository.mbom.MbomRepository;
import com.lts5.init.repository.itemprogress.ItemProgressRepository;
import com.lts5.init.repository.item.ItemRepository;
import com.primes.library.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MbomTreeService extends BaseService {
    private final MbomRepository mbomRepository;
    private final ItemProgressRepository itemProgressRepository;
    private final ItemRepository itemRepository;
    private final GlobalMapper globalMapper;
    private final MbomUtilService mbomUtilService;

    /**
     * 공정 순서별 MBOM 트리 조회
     * @param rootItemId 루트 아이템 ID
     * @return 공정 순서별로 정렬된 MBOM 트리
     */
    public List<MbomTreeDto> getMbomTreeByProcessOrder(Long rootItemId) {
        // 1. 루트 아이템의 공정 정보를 순서대로 조회
        List<ItemProgress> rootProgresses = itemProgressRepository.findByItemIdOrderByProgressOrder(rootItemId);
        
        List<MbomTreeDto> result = new ArrayList<>();
        
        for (ItemProgress progress : rootProgresses) {
            // 2. 각 공정에 투입되는 MBOM들을 조회 (해당 공정번호로 필터링)
            List<Mbom> allMboms = mbomRepository.findByParentItemIdOrderByParentProgressId(rootItemId);
            List<Mbom> mboms = allMboms.stream()
                    .filter(mbom -> progress.getId().longValue() == (mbom.getParentProgressId() != null ? mbom.getParentProgressId() : 0L))
                    .toList();
            
            for (Mbom mbom : mboms) {
                MbomTreeDto treeDto = buildMbomTreeNode(mbom, progress, 0, progress.getProgressOrder() != null ? String.valueOf(progress.getProgressOrder()) : "0");
                
                // 3. 재귀적으로 하위 BOM 구성
                buildChildrenRecursively(treeDto, 1);
                
                result.add(treeDto);
            }
        }
        
        return result;
    }
    
    /**
     * 특정 아이템의 MBOM 트리 조회 (공정 순서 기반)
     * @param itemId 아이템 ID
     * @param maxDepth 최대 깊이 (null이면 무제한)
     * @return 공정 순서별로 정렬된 MBOM 트리
     */
    public List<MbomTreeDto> getMbomTreeByItem(Long itemId, Integer maxDepth) {
        // 해당 아이템이 부모로 사용되는 MBOM들을 조회
        List<Mbom> parentMboms = mbomRepository.findByParentItemIdOrderByParentProgressId(itemId);
        
        List<MbomTreeDto> result = new ArrayList<>();
        
        for (Mbom mbom : parentMboms) {
            // 공정 정보 조회
            ItemProgress progress = null;
            if (mbom.getParentProgressId() != null) {
                progress = itemProgressRepository.findById(mbom.getParentProgressId()).orElse(null);
            }
            
            MbomTreeDto treeDto = buildMbomTreeNode(mbom, progress, 0, "1");
            
            // 재귀적으로 하위 BOM 구성 (깊이 제한 적용)
            if (maxDepth == null || maxDepth > 1) {
                buildChildrenRecursively(treeDto, 1, maxDepth);
            }
            
            result.add(treeDto);
        }
        
        // 공정 순서로 정렬
        result.sort((a, b) -> {
            if (a.getProgressOrder() == null && b.getProgressOrder() == null) return 0;
            if (a.getProgressOrder() == null) return 1;
            if (b.getProgressOrder() == null) return -1;
            return a.getProgressOrder().compareTo(b.getProgressOrder());
        });
        
        return result;
    }
    
    /**
     * TreeView UI용 공정 트리 조회
     * @param itemId 제품 아이템 ID
     * @return TreeView UI에 최적화된 트리 구조
     */
    public List<ProcessTreeNodeDto> getProcessTreeForUI(Long itemId) {
        // 1. 해당 아이템의 공정들을 순서대로 조회
        List<ItemProgress> progresses = itemProgressRepository.findByItemIdOrderByProgressOrder(itemId);
        
        // 2. 아이템 정보 조회
        Item item = itemRepository.findById(itemId).orElse(null);
        
        List<ProcessTreeNodeDto> result = new ArrayList<>();
        
        for (int i = 0; i < progresses.size(); i++) {
            ItemProgress progress = progresses.get(i);
            
            // 공정 노드 생성
            ProcessTreeNodeDto processNode = buildProcessNodeForUI(progress, item, 0, String.valueOf(i + 1));
            
            // 해당 공정에 투입되는 MBOM들을 하위 노드로 추가
            List<Mbom> allMboms = mbomRepository.findByParentItemIdOrderByParentProgressId(itemId);
            List<Mbom> inputMboms = allMboms.stream()
                    .filter(mbom -> progress.getId().longValue() == (mbom.getParentProgressId() != null ? mbom.getParentProgressId() : 0L))
                    .toList();
            
            if (!inputMboms.isEmpty()) {
                List<ProcessTreeNodeDto> materialNodes = new ArrayList<>();
                
                for (int j = 0; j < inputMboms.size(); j++) {
                    Mbom mbom = inputMboms.get(j);
                    ProcessTreeNodeDto materialNode = buildMaterialNodeForUI(mbom, 1, processNode.getPath() + "." + (j + 1));
                    materialNodes.add(materialNode);
                }
                
                processNode.setChildren(materialNodes);
                processNode.setHasChildren(true);
                processNode.setChildrenCount(materialNodes.size());
            } else {
                processNode.setChildren(new ArrayList<>());
                processNode.setHasChildren(false);
                processNode.setChildrenCount(0);
            }
            
            result.add(processNode);
        }
        
        return result;
    }
    
    /**
     * 재귀적 TreeView UI용 공정 트리 조회 (하위 제품 포함)
     * @param rootItemId 루트 제품 ID
     * @param maxDepth 최대 깊이 (null이면 무제한)
     * @return 재귀적 TreeView UI 트리 구조
     */
    public List<ProcessTreeNodeDto> getRecursiveProcessTreeForUI(Long rootItemId, Integer maxDepth) {
        List<ProcessTreeNodeDto> result = getProcessTreeForUI(rootItemId);
        
        // 각 공정의 투입품을 확인하여 하위 제품이 있으면 재귀적으로 처리
        for (ProcessTreeNodeDto processNode : result) {
            if (processNode.getChildren() != null) {
                for (ProcessTreeNodeDto materialNode : processNode.getChildren()) {
                    if ("MATERIAL".equals(materialNode.getNodeType()) && materialNode.getItemId() != null) {
                        // 투입품이 다른 제품인지 확인 (해당 아이템에 공정이 있는지 체크)
                        List<ItemProgress> subProgresses = itemProgressRepository.findByItemIdOrderByProgressOrder(materialNode.getItemId());
                        
                        if (!subProgresses.isEmpty() && (maxDepth == null || materialNode.getLevel() < maxDepth - 1)) {
                            // 하위 제품의 공정 트리를 구성
                            List<ProcessTreeNodeDto> subProcessTree = buildSubProcessTreeForUI(materialNode.getItemId(), materialNode.getLevel() + 1, materialNode.getPath(), maxDepth);
                            
                            if (!subProcessTree.isEmpty()) {
                                materialNode.setChildren(subProcessTree);
                                materialNode.setHasChildren(true);
                                materialNode.setChildrenCount(subProcessTree.size());
                                materialNode.setNodeType("ITEM"); // 하위 공정이 있는 경우 ITEM으로 변경
                                materialNode.setIcon("item");
                            }
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * 전체 BOM 트리 조회
     * 모든 루트 아이템들과 그 하위 트리를 포함한 전체 BOM 구조를 반환
     * 동일한 아이템이 루트와 투입품 역할을 동시에 할 수 있는 유연한 설계 지원
     * @return 전체 BOM 트리 구조
     */
    public FullBomTreeDto getFullBomTree() {
        // 1. 루트로 등록된 아이템들만 조회 (parentItemId가 null인 아이템들)
        List<Mbom> rootMboms = mbomRepository.findByParentItemIdIsNull();
        
        // 루트 아이템들의 중복 제거 (동일한 itemId를 가진 경우)
        Map<Long, Mbom> uniqueRoots = rootMboms.stream()
            .collect(Collectors.toMap(
                Mbom::getItemId, 
                Function.identity(), 
                (existing, replacement) -> existing // 중복 시 첫 번째 것 유지
            ));
        
        // 2. 각 고유 루트 아이템에 대해 공정-투입품 계층 구조 생성
        List<RootItemTreeDto> rootTrees = new ArrayList<>();
        for (Mbom rootMbom : uniqueRoots.values()) {
            RootItemTreeDto rootTree = buildProcessItemTree(rootMbom.getItemId());
            rootTrees.add(rootTree);
        }
        
        // 3. 전체 BOM 개수 조회
        Long totalCount = mbomRepository.countAllMboms();
        
        return FullBomTreeDto.builder()
                .rootItems(rootTrees)
                .totalCount(totalCount)
                .rootItemCount(rootTrees.size())
                .build();
    }

    /**
     * 공정-투입품 계층 구조 트리 생성 (개선된 구조)
     * @param rootItemId 루트 아이템 ID
     * @return 공정-투입품 계층 구조
     */
    private RootItemTreeDto buildProcessItemTree(Long rootItemId) {
        return buildProcessItemTree(rootItemId, new HashSet<>());
    }
    
    /**
     * 공정-투입품 계층 구조 트리 생성 (순환 참조 방지 포함)
     * @param rootItemId 루트 아이템 ID
     * @param visitedRoots 방문한 루트 아이템들 (순환 참조 방지)
     * @return 공정-투입품 계층 구조
     */
    private RootItemTreeDto buildProcessItemTree(Long rootItemId, Set<Long> visitedRoots) {
        // 순환 참조 방지
        if (visitedRoots.contains(rootItemId)) {
            log.warn("순환 참조 감지하여 스킵 - rootItemId: {}", rootItemId);
            return null;
        }
        visitedRoots.add(rootItemId);
        
        // 1. 루트 아이템의 모든 공정 조회 (순서대로)
        List<ItemProgress> rootProgresses = itemProgressRepository.findByItemIdOrderByProgressOrder(rootItemId);
        
        // 2. 루트 아이템을 부모로 하는 모든 MBOM 조회
        List<Mbom> allInputMboms = mbomRepository.findByParentItemIdOrderByParentProgressId(rootItemId);
        
        // 3. 루트 아이템 정보 조회
        ProductInfoDto productInfo = mbomUtilService.buildProductInfo(rootItemId);
        
        if (rootProgresses.isEmpty()) {
            return RootItemTreeDto.builder()
                    .rootItemId(rootItemId)
                    .productInfo(productInfo)
                    .processTree(new ArrayList<>())
                    .totalProcessCount(0)
                    .totalInputItemCount(0)
                    .build();
        }

        // 각 공정별로 투입품들을 그룹화하여 트리 구조 생성
        List<ProcessNodeDto> processNodes = new ArrayList<>();
        int totalInputItemCount = 0;
        
        for (ItemProgress progress : rootProgresses) {
            // 해당 공정에 투입되는 MBOM들 필터링
            List<Mbom> processInputMboms = allInputMboms.stream()
                    .filter(mbom -> {
                        boolean matches = progress.getId().equals(mbom.getParentProgressId());
                        return matches;
                    })
                    .toList();
            
            // 투입품 DTO 리스트 생성
            List<InputItemDto> inputItems = new ArrayList<>();
            for (int j = 0; j < processInputMboms.size(); j++) {
                Mbom inputMbom = processInputMboms.get(j);
                String path = progress.getProgressOrder() + "." + (j + 1);
                
                InputItemDto inputItem = mbomUtilService.buildInputItemDto(inputMbom, path);
                inputItems.add(inputItem);
                totalInputItemCount++;
            }
            
            // 공정 노드 생성
            ProcessNodeDto processNode = ProcessNodeDto.builder()
                    .progressId(progress.getId())
                    .progressOrder(progress.getProgressOrder() != null ? progress.getProgressOrder().intValue() : 0)
                    .progressName(progress.getProgressName())
                    .progressTypeName(progress.getProgressTypeName())
                    .inputItems(inputItems)
                    .path(progress.getProgressOrder() != null ? String.valueOf(progress.getProgressOrder()) : "0")
                    .inputItemCount(inputItems.size())
                    .build();
            
            processNodes.add(processNode);
        }
        
        return RootItemTreeDto.builder()
                .rootItemId(rootItemId)
                .productInfo(productInfo)
                .processTree(processNodes)
                .totalProcessCount(processNodes.size())
                .totalInputItemCount(totalInputItemCount)
                .build();
    }
    
    /**
     * 하위 제품의 공정 트리 구성 (UI용)
     */
    private List<ProcessTreeNodeDto> buildSubProcessTreeForUI(Long itemId, Integer level, String parentPath, Integer maxDepth) {
        if (maxDepth != null && level >= maxDepth) {
            return new ArrayList<>();
        }
        
        List<ItemProgress> progresses = itemProgressRepository.findByItemIdOrderByProgressOrder(itemId);
        Item item = itemRepository.findById(itemId).orElse(null);
        
        List<ProcessTreeNodeDto> subTree = new ArrayList<>();
        
        for (int i = 0; i < progresses.size(); i++) {
            ItemProgress progress = progresses.get(i);
            String childPath = parentPath + "." + (i + 1);
            
            ProcessTreeNodeDto processNode = buildProcessNodeForUI(progress, item, level, childPath);
            
            // 투입품 조회 및 하위 노드 구성
            List<Mbom> allMboms = mbomRepository.findByParentItemIdOrderByParentProgressId(itemId);
            List<Mbom> inputMboms = allMboms.stream()
                    .filter(mbom -> progress.getId().longValue() == (mbom.getParentProgressId() != null ? mbom.getParentProgressId() : 0L))
                    .toList();
            
            if (!inputMboms.isEmpty()) {
                List<ProcessTreeNodeDto> materialNodes = new ArrayList<>();
                
                for (int j = 0; j < inputMboms.size(); j++) {
                    Mbom mbom = inputMboms.get(j);
                    ProcessTreeNodeDto materialNode = buildMaterialNodeForUI(mbom, level + 1, processNode.getPath() + "." + (j + 1));
                    materialNodes.add(materialNode);
                }
                
                processNode.setChildren(materialNodes);
                processNode.setHasChildren(true);
                processNode.setChildrenCount(materialNodes.size());
            }
            
            subTree.add(processNode);
        }
        
        return subTree;
    }
    
    /**
     * TreeView UI용 공정 노드 생성
     */
    private ProcessTreeNodeDto buildProcessNodeForUI(ItemProgress progress, Item item, Integer level, String path) {
        String nodeId = "process_" + progress.getId();
        String label = String.format("[%d] %s", progress.getProgressOrder(), progress.getProgressName());
        
        if (progress.getIsOutsourcing() != null && progress.getIsOutsourcing()) {
            label += " (외주)";
        }
        
        ProcessTreeNodeDto dto = globalMapper.map(progress, ProcessTreeNodeDto.class);
        dto.setId(nodeId);
        dto.setLabel(label);
        dto.setIcon("process");
        dto.setNodeType("PROCESS");
        dto.setItemId(item != null ? item.getId() : null);
        dto.setItemName(item != null ? item.getItemName() : null);
        dto.setItemNumber(item != null ? item.getItemNumber() : null);
        dto.setLevel(level);
        dto.setPath(path);
        dto.setDisabled(false);
        
        return dto;
    }
    
    /**
     * TreeView UI용 자재 노드 생성
     */
    private ProcessTreeNodeDto buildMaterialNodeForUI(Mbom mbom, Integer level, String path) {
        String nodeId = "material_" + mbom.getId();
        String label = String.format("📦 %s (%.1f %s)", 
                mbom.getItemId(), 
                mbom.getInputNum(), 
                mbom.getInputUnit() != null ? mbom.getInputUnit() : mbom.getInputUnitCode());
        
        ProcessTreeNodeDto dto = globalMapper.map(mbom, ProcessTreeNodeDto.class);
        dto.setId(nodeId);
        dto.setLabel(label);
        dto.setIcon("material");
        dto.setNodeType("MATERIAL");
        dto.setLevel(level);
        dto.setPath(path);
        dto.setDisabled(false);
        dto.setChildren(new ArrayList<>());
        dto.setHasChildren(false);
        dto.setChildrenCount(0);
        
        return dto;
    }
    
    /**
     * MBOM 트리 노드 생성
     */
    private MbomTreeDto buildMbomTreeNode(Mbom mbom, ItemProgress progress, Integer level, String path) {
        MbomTreeDto treeDto = globalMapper.map(mbom, MbomTreeDto.class);
        
        // 공정 정보 설정
        if (progress != null) {
            treeDto.setProgressOrder(progress.getProgressOrder());
            treeDto.setProgressName(progress.getProgressName());
            treeDto.setProgressTypeName(progress.getProgressTypeName());
        }
        
        // 트리 구조 정보 설정
        treeDto.setLevel(level);
        treeDto.setPath(path);
        treeDto.setTotalInputNum(mbom.getInputNum());
        treeDto.setInputUnitDisplay(mbom.getInputUnit() != null ? mbom.getInputUnit() : mbom.getInputUnitCode());
        treeDto.setChildren(new ArrayList<>());
                
        return treeDto;
    }
    
    /**
     * 재귀적으로 하위 BOM 구성 (깊이 제한 없음)
     */
    private void buildChildrenRecursively(MbomTreeDto parentNode, Integer level) {
        buildChildrenRecursively(parentNode, level, null);
    }
    
    /**
     * 재귀적으로 하위 BOM 구성 (깊이 제한 적용, 순환 참조 방지)
     */
    private void buildChildrenRecursively(MbomTreeDto parentNode, Integer level, Integer maxDepth) {
        buildChildrenRecursively(parentNode, level, maxDepth, new HashSet<Long>());
    }
    
    /**
     * 재귀적으로 하위 BOM 구성 (순환 참조 방지 포함)
     */
    private void buildChildrenRecursively(MbomTreeDto parentNode, Integer level, Integer maxDepth, Set<Long> visitedItems) {
        // 깊이 제한 체크
        if (maxDepth != null && level >= maxDepth) {
            return;
        }
        
        // 순환 참조 방지 - 이미 방문한 아이템인지 확인
        if (visitedItems.contains(parentNode.getItemId())) {
            log.warn("순환 참조 방지 - 이미 방문한 아이템: {}, 경로: {}", parentNode.getItemId(), parentNode.getPath());
            return;
        }
        
        // 방문 목록에 추가
        visitedItems.add(parentNode.getItemId());
        
        // 현재 노드의 itemId를 부모로 하는 하위 MBOM들을 조회
        List<Mbom> childMboms = mbomRepository.findByParentItemIdOrderByParentProgressId(parentNode.getItemId());
        
        int childIndex = 1;
        for (Mbom childMbom : childMboms) {
            // 순환 참조 체크 - 자식이 이미 방문한 아이템인지 확인
            if (visitedItems.contains(childMbom.getItemId())) {
                log.warn("순환 참조 감지하여 스킵 - 부모: {}, 자식: {}", parentNode.getItemId(), childMbom.getItemId());
                continue;
            }
            
            // 공정 정보 조회
            ItemProgress progress = null;
            if (childMbom.getParentProgressId() != null) {
                progress = itemProgressRepository.findById(childMbom.getParentProgressId()).orElse(null);
            }
            
            String childPath = parentNode.getPath() + "." + childIndex;
            MbomTreeDto childNode = buildMbomTreeNode(childMbom, progress, level, childPath);
            
            // 재귀 호출 (방문 목록 복사하여 전달)
            buildChildrenRecursively(childNode, level + 1, maxDepth, new HashSet<Long>(visitedItems));
            
            parentNode.getChildren().add(childNode);
            childIndex++;
        }
        
        // 공정 순서로 정렬
        parentNode.getChildren().sort((a, b) -> {
            if (a.getProgressOrder() == null && b.getProgressOrder() == null) return 0;
            if (a.getProgressOrder() == null) return 1;
            if (b.getProgressOrder() == null) return -1;
            return a.getProgressOrder().compareTo(b.getProgressOrder());
        });
        
        // 하위 정보 설정
        parentNode.setHasChildren(!parentNode.getChildren().isEmpty());
        parentNode.setChildrenCount(parentNode.getChildren().size());
    }
}
