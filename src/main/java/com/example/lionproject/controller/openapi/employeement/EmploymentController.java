package com.example.lionproject.controller.openapi.employeement;

import com.example.lionproject.domain.dto.EmploymentJsonDto;
import com.example.lionproject.domain.entity.Employment;
import com.example.lionproject.repository.employment.EmploymentRepository;
import com.example.lionproject.service.Api.WebClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/employment")
public class EmploymentController {

    private final WebClientService webClientService;
    private final EmploymentRepository repository;

    @GetMapping("/test")
    public String returnEntity(@RequestParam Integer startIndex, @RequestParam Integer endIndex) {
        String res = webClientService.employmentSupport(startIndex, endIndex);
        return res;
    }

    @GetMapping()   //확인용
    public ResponseEntity<EmploymentJsonDto> returnDto(@RequestParam Integer startIndex, @RequestParam Integer endIndex) {
        EmploymentJsonDto employmentDto = webClientService.returnEmploymentDto(startIndex, endIndex);
        return new ResponseEntity<>(employmentDto, HttpStatus.OK);
    }

    /**
     * 전체 데이터 반환
     */
    @GetMapping("/all_data")
    public ResponseEntity<List<EmploymentResponse>> returnAllData() {
        List<Employment> temp = repository.findAllByOrderByApplicationEndDateAsc();
        List<EmploymentResponse> res = temp.stream().map(this::convertToEmploymentResponse)
                .collect(Collectors.toList());
//        log.info("res size : {}", res.size());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * 체크박스 필터 적용한 데이터 조회
     */
    @PostMapping("/filter_data")
    public ResponseEntity<List<EmploymentResponse>> returnFilterData(@RequestBody EmploymentRequest dto) {
        //검색 필터에 일치하는 내용들 가져오기
        List<Employment> temp;
        if (dto.getRegistCost() == null && dto.getApplyState() == null) {
            temp = repository.findAll();
        } else if (dto.getRegistCost() != null && dto.getApplyState() == null) {
            temp = repository.findByRegistCost(dto.getRegistCost());
        } else if (dto.getRegistCost() == null && dto.getApplyState() != null) {
            temp = repository.findByApplyState(dto.getApplyState());
        }else {
            temp = repository.findByRegistCostAndApplyState(dto.getRegistCost(), dto.getApplyState());
        }

        List<EmploymentResponse> res = temp.stream().map(this::convertToEmploymentResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * 교육명으로 검색
     */
    @GetMapping("/find")
    public ResponseEntity<List<EmploymentResponse>> returnFindData(@RequestParam String subject) {
        List<Employment> temp = repository.findBySubjectContaining(subject);
        List<EmploymentResponse> res = temp.stream().map(this::convertToEmploymentResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    private EmploymentResponse convertToEmploymentResponse(Employment dto) {

        /**
         * 날짜 신경 안쓰기로 함
         LocalDate today = LocalDate.now(); // 오늘
         LocalDate applicationEndDate = LocalDate.parse(dto.getApplicationEndDate()); // applicationEndDate가 "yyyy-MM-dd" 형식

         if (applicationEndDate.isBefore(today)) {
         return null;
         } //지난 applicationEndDate의 경우 null 반환
         */

        EmploymentResponse vo = new EmploymentResponse();
        vo.setSubject(dto.getSubject());
        vo.setStartDate(dto.getStartDate());
        vo.setEndDate(dto.getEndDate());
        vo.setApplicationStartDate(dto.getApplicationStartDate());
        vo.setApplicationEndDate(dto.getApplicationEndDate());
        vo.setRegistCost(dto.getRegistCost());
        vo.setRegistPeople(dto.getRegistPeople());
        vo.setApplyState(dto.getApplyState());
        vo.setViewDetail(dto.getViewDetail());
        return vo;
    }
}
