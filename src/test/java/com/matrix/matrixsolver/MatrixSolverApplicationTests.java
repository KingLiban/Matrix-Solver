package com.matrix.matrixsolver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class MatrixSolverApplicationTests {

    private Matrix matrix;

    @BeforeEach
    void setUp() {
        matrix = new Matrix(new double[][] {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        });
    }

    @Test
    void testGetMatrix() {
        double[][] result = matrix.getMatrix();
        assertArrayEquals(new double[]{1, 2, 3}, result[0]);
        assertArrayEquals(new double[]{4, 5, 6}, result[1]);
        assertArrayEquals(new double[]{7, 8, 9}, result[2]);
    }

    @Test
    void testGetRowEchelonForm() {
        double[][] result = matrix.getRowEchelonForm();
        assertArrayEquals(new double[]{1, 2, 3}, result[0]);
        assertArrayEquals(new double[]{0, -3, -6}, result[1]);
        assertArrayEquals(new double[]{0, 0, 0}, result[2]);
    }

    @Test
    void testGetReducedRowEchelonForm() {
        double[][] result = matrix.getReducedRowEchelonForm();
        assertArrayEquals(new double[]{1, 0, -1}, result[0]);
        assertArrayEquals(new double[]{0, 1, 2}, result[1]);
        assertArrayEquals(new double[]{0, 0, 0}, result[2]);
    }

    @Test
    void testGetGeneralSolution() {
        List<List<Double>> result = matrix.getGeneralSolution();
        assertEquals(2, result.size());
        assertEquals(List.of(1.0, -2.0, 1.0), result.get(0));
        assertEquals(List.of(1.0, 0.0, -1.0), result.get(1));
    }

    @Test
    void testIsGeneralSolutionValid() {
        List<List<Double>> validSolution = List.of(
            List.of(1.0, 2.0, 3.0),
            List.of(4.0, 5.0, 6.0)
        );
        assertTrue(matrix.isGeneralSolutionValid(validSolution));

        List<List<Double>> invalidSolution = List.of(
            List.of(0.0, 0.0, 0.0),
            List.of(0.0, 0.0, 0.0)
        );
        assertFalse(matrix.isGeneralSolutionValid(invalidSolution));
    }

    @Test
    void testBuildGeneralSolution() {
        String result = matrix.buildGeneralSolution();
        assertEquals("<1.000000, -2.000000, 1.000000> + s_1<1.000000, 0.000000, -1.000000>", result);
    }
}