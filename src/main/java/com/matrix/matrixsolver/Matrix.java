package com.matrix.matrixsolver;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Matrix {

    private final double[][] matrix;

    public Matrix(String path) {
        File file = new File(path);
        this.matrix = createMatrix(file);
    }

    public Matrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public double[][] getMatrix() {
        return this.matrix;
    }

    private double[][] createMatrix(File file) {
        try {
            Scanner scanner = new Scanner(file);
            List<double[]> tempMatrix = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().trim().split("\\s+");
                double[] row = new double[line.length];

                for (int i = 0; i < row.length; i++) {
                    row[i] = Double.parseDouble(line[i]);
                }

                tempMatrix.add(row);
            }

            scanner.close();

            int rows = tempMatrix.size();
            int cols = tempMatrix.get(0).length;

            double[][] matrix = new double[rows][cols];

            for (int i = 0; i < rows; i++) {
                double[] currentRow = tempMatrix.get(i);
                for (int j = 0; j < cols; j++) {
                    matrix[i][j] = currentRow[j];
                }
            }
            return matrix;
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            return null;
        }
    }

    private void swap(double[][] matrix, int i, int j) {
        double[] temp = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = temp;
    }

    private double[] addMultipleOfRow(double[] row1, double[] row2, double scalar) {
        for (int i = 0; i < row2.length; i++) {
            double multiple = row1[i] * scalar;
            row2[i] += multiple;
        }

        return row2;
    }

    public double[][] getRowEchelonForm() {
        double[][] rowEchelonForm = new double[this.matrix.length][];
        for (int i = 0; i < this.matrix.length; i++) {
            rowEchelonForm[i] = Arrays.copyOf(this.matrix[i], this.matrix[i].length);
        }

        for (int i = 0; i < rowEchelonForm.length; i++) {
            if (rowEchelonForm[i][i] == 0) {
                for (int j = i + 1; j < rowEchelonForm.length; j++) {
                    if (rowEchelonForm[j][i] != 0) {
                        swap(rowEchelonForm, i, j);
                        break;
                    }
                }
            }

            if (rowEchelonForm[i][i] == 0) {
                continue;
            }

            for (int j = i + 1; j < rowEchelonForm.length; j++) {
                if (rowEchelonForm[j][i] != 0) {
                    double scalar = -rowEchelonForm[j][i] / rowEchelonForm[i][i];
                    rowEchelonForm[j] = addMultipleOfRow(rowEchelonForm[i], rowEchelonForm[j], scalar);
                }
            }
        }

        moveZeros(rowEchelonForm);
        return rowEchelonForm;
    }

    private void moveZeros(double[][] matrix) {
        int swapRow = matrix.length - 1;
        for (int i = matrix.length - 1; i >= 0; i--) {
            boolean allZero = true;
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != 0) {
                    allZero = false;
                    break;
                }
            }
            if (allZero) {
                double[] temp = matrix[i];
                matrix[i] = matrix[swapRow];
                matrix[swapRow] = temp;
                swapRow--;
            }
        }
    }

    public double[][] getReducedRowEchelonForm() {
        double[][] rowEchelonForm = getRowEchelonForm();

        for (int i = 0; i < rowEchelonForm.length; i++) {
            int pivot = 0;
            while (pivot < rowEchelonForm[i].length && rowEchelonForm[i][pivot] == 0) {
                pivot++;
            }

            if (pivot < rowEchelonForm[i].length && rowEchelonForm[i][pivot] != 0) {
                double pivotValue = rowEchelonForm[i][pivot];
                for (int j = 0; j < rowEchelonForm[i].length; j++) {
                    rowEchelonForm[i][j] /= pivotValue;
                    // Floating error fix
                    if (rowEchelonForm[i][j] == -0.0) {
                        rowEchelonForm[i][j] = 0.0;
                    }
                }

                for (int j = 0; j < i; j++) {
                    if (rowEchelonForm[j][pivot] != 0) {
                        double scalar = -rowEchelonForm[j][pivot];
                        rowEchelonForm[j] = addMultipleOfRow(rowEchelonForm[i], rowEchelonForm[j], scalar);
                    }
                }
            }
        }

        return rowEchelonForm;
    }

    public List<List<Double>> getGeneralSolution() {
        double[][] reducedRowEchelonForm = this.getReducedRowEchelonForm();
        boolean[] variables = new boolean[matrix[0].length - 1];

        for (int i = 0; i < reducedRowEchelonForm.length; i++) {
            int pivot = 0;
            while (pivot < reducedRowEchelonForm[i].length - 1 && reducedRowEchelonForm[i][pivot] == 0) {
                pivot++;
            }
            if (pivot < reducedRowEchelonForm[i].length - 1) {
                variables[pivot] = true;
            }
        }

        List<Double> starterVector = new ArrayList<>();
        for (int i = 0; i < variables.length; i++) {
            if (variables[i]) {
                starterVector.add(reducedRowEchelonForm[i][reducedRowEchelonForm[0].length - 1]);
            } else {
                starterVector.add(0.0);
            }
        }

        List<List<Double>> vectors = new ArrayList<>();
        vectors.add(starterVector);

        for (int i = 0; i < variables.length; i++) {
            int pivotRow = 0;
            if (!variables[i]) {
                List<Double> vector = new ArrayList<>();
                for (int j = 0; j < variables.length; j++) {
                    if (variables[j]) {
                        vector.add(-reducedRowEchelonForm[pivotRow][i]);
                        pivotRow++;
                    } else if (i == j) {
                        vector.add(1.0);
                    } else {
                        vector.add(0.0);
                    }
                }
                vectors.add(vector);
            }
        }

        return vectors;
    }

    public static void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            System.out.print("[");
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == -0.0) {
                    System.out.print(0.0);
                } else {
                    System.out.print(matrix[i][j]);
                }
                if (j < matrix[0].length - 1) {
                    System.out.print(" ");
                }
            }
            System.out.print("]");
            System.out.println();
        }
    }

    public boolean isGeneralSolutionValid(List<List<Double>> vectors) {
        List<Double> firstVector = vectors.get(0);
        boolean noSolution = true;

        for (double d : firstVector) {
            if (d != 0) {
                noSolution = false;
                break;
            }
        }

        return !noSolution;
    }

    public void printGeneralSolution() {
        List<List<Double>> vectors = getGeneralSolution();

        if (!isGeneralSolutionValid(vectors)) {
            System.out.println("NO SOLUTION");
            return;
        }

        for (int i = 0; i < vectors.size(); i++) {
            List<Double> vector = vectors.get(i);
            if (i != 0) {
                System.out.print(" + s_" + i);
            }
            System.out.print("<");
            for (int j = 0; j < vector.size(); j++) {
                System.out.print(vector.get(j));
                if (j < vector.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print(">");
        }
        System.out.println();
    }

    public String buildGeneralSolution() {
        List<List<Double>> vectors = getGeneralSolution();
        StringBuilder sb = new StringBuilder();
    
        if (!isGeneralSolutionValid(vectors)) {
            return "NO SOLUTION";
        }
    
        for (int i = 0; i < vectors.size(); i++) {
            List<Double> vector = vectors.get(i);
    
            if (i != 0) {
                sb.append(" + s_").append(i);
            }
            sb.append("<");
    
            for (int j = 0; j < vector.size(); j++) {
                sb.append(vector.get(j));
                if (j < vector.size() - 1) {
                    sb.append(", ");
                }
            }
            sb.append(">");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Matrix matrix = new Matrix("src/main/java/com/matrix/matrixsolver/matrix.txt");
        if (matrix.getMatrix() != null) {
            System.out.println("Original matrix:");
            printMatrix(matrix.getMatrix());
            System.out.println();

            System.out.println("Row echelon form:");
            printMatrix(matrix.getRowEchelonForm());
            System.out.println();

            System.out.println("Reduced row echelon form:");
            printMatrix(matrix.getReducedRowEchelonForm());
            System.out.println();

            System.out.println("General solution:");
            matrix.printGeneralSolution();
        } else {
            System.out.println("Failed to initialize the matrix. Please check the file path and content.");
        }
    }

}
