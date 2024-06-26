package com.syscrud.web2.controller;

import com.syscrud.web2.dto.alunoDTO;
import com.syscrud.web2.model.AlunoEntity;
import com.syscrud.web2.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @GetMapping
    public ResponseEntity<List<AlunoEntity>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(alunoService.getAllActiveStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable(value = "id") Long id) {
        Optional<AlunoEntity> alunoEntity = alunoService.getStudentById(id);

        return alunoEntity.<ResponseEntity<Object>>map(entity
                -> ResponseEntity.status(HttpStatus.OK).body(entity)).orElseGet(()
                -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found"));
    }

    @PostMapping
    public ResponseEntity<Object> postAluno(@Valid @RequestBody alunoDTO aluno, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errors);
        }
        AlunoEntity alunoEntity = alunoService.createStudent(aluno);
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoEntity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> putAluno(@PathVariable(value = "id") Long id,
            @RequestBody alunoDTO aluno) {
        AlunoEntity updatedAluno = alunoService.updateStudent(id, aluno);
        return (updatedAluno != null)
                ? ResponseEntity.status(HttpStatus.CREATED).body(updatedAluno)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAluno(@PathVariable(value = "id") Long id) {
        alunoService.deleteStudent(id);
        return ResponseEntity.status(HttpStatus.OK).body("Student deleted successfully!");
    }

    @DeleteMapping("/inactivate/{id}")
    public ResponseEntity<Object> logicDeleteAluno(@PathVariable(value = "id") Long id) {
        AlunoEntity inactivatedAluno = alunoService.inactivateStudent(id);
        return (inactivatedAluno != null)
                ? ResponseEntity.status(HttpStatus.CREATED).body(inactivatedAluno)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
    }
}
