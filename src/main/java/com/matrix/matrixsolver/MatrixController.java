package com.matrix.matrixsolver;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
@RestController
@RequestMapping("/matrix")
public class MatrixController {

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitMatrix(@RequestBody double[][] arr) {
        Matrix matrix = new Matrix(arr);
        Map<String, Object> response = new HashMap<>();

        try {
            List<List<Double>> generalSolution = matrix.getGeneralSolution();
            double[][] rowEchelonForm = matrix.getRowEchelonForm();
            double[][] reducedRowEchelonForm = matrix.getReducedRowEchelonForm();

            response.put("generalSolution", matrix.isGeneralSolutionValid(generalSolution) ? matrix.buildGeneralSolution() : "NO SOLUTION");
            response.put("rowEchelonForm", rowEchelonForm);
            response.put("reducedRowEchelonForm", reducedRowEchelonForm);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
 
