package com.bootcamp.service;

import com.bootcamp.dto.EmployeeDto;
import com.bootcamp.dto.EmpBySkillDto;
import com.bootcamp.repository.EmployeeRepository;
import com.bootcamp.repository.EmployeeSkillRepository;
import com.bootcamp.util.KafkaProducer;
import com.bootcamp.util.Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeSkillRepository employeeSkillRepository;

    @Autowired
    private KafkaProducer kafkaProducer;

    public Mono<EmployeeDto> createEmployee(Mono<EmployeeDto> employeeDtoMono) {
        return employeeDtoMono.flatMap(employeeDto -> employeeRepository.findOneByKeyId(employeeDto.getId())
                .doOnNext(employee -> {
                    if (employee != null)
                        employeeDto.setStatus("Already Exists");
                })
                .switchIfEmpty(Mono.defer(() -> employeeSkillRepository.save(Mapper.mapDtoToEmpSkillEntity(employeeDto))
                        .flatMap(savedEmpSkill -> employeeRepository.save(Mapper.mapDtoToEmpEntity(employeeDto)))
                        .map(savedEmp -> {
                            employeeDto.setStatus("Created");
                            log.debug("Employee creation Successful, producing kafka message now");
                            kafkaProducer.produceEmpMessage(employeeDto);
                            return savedEmp;
                        })
                        .doOnError(e -> log.error("Error occurred => " + e.getMessage()))))
                .map(emp ->
                        employeeDto));
    }

    public Flux<EmpBySkillDto> getEmpByJavaExpAndSpringExp(double springExp, double javaExp) {
        log.debug("Getting employee details with spring and java experience");
        return employeeSkillRepository
                .findByKeySpringExpGreaterThanEqualAndKeyJavaExpGreaterThanEqual(springExp, javaExp)
                .flatMap(empSkill ->
                        employeeRepository.findOneByKeyId(empSkill.getKey().getId())
                                .map(emp -> Mapper.mapEmpAndEmpSkillToDto(emp, empSkill))
                );
    }

    public Flux<EmpBySkillDto> getEmpByJavaExp(double javaExp) {
        log.debug("Getting employee details with java experience");
        return employeeSkillRepository.findByKeyJavaExpGreaterThanEqual(javaExp)
                .flatMap(empSkill ->
                        employeeRepository.findOneByKeyId(empSkill.getKey().getId())
                                .map(emp -> Mapper.mapEmpAndEmpSkillToDto(emp, empSkill))
                );
    }

    public Flux<EmpBySkillDto> getEmpBySpringExp(double springExp) {
        log.debug("Getting employee details with Spring experience");
        return employeeSkillRepository.findByKeySpringExpGreaterThanEqual(springExp)
                .flatMap(empSkill ->
                        employeeRepository.findOneByKeyId(empSkill.getKey().getId())
                                .map(emp -> Mapper.mapEmpAndEmpSkillToDto(emp, empSkill))
                );
    }

}
