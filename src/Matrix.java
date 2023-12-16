import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Matrix {

    private double[][] matrix;
    
    public Matrix(String path) {
        File file = new File("src/matrix.txt");
        createMatrix(file);
    }

    private void createMatrix(File file) {
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

            matrix = new double[rows][cols];
            

            for (int i = 0; i < rows; i++) {
                double[] currentRow = tempMatrix.get(i);
                for (int j = 0; j < cols; j++) {
                    matrix[i][j] = currentRow[j];
                }
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
    }    
    
    private void swap(int i, int j) {
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
    
    private void toRowEchelonForm() {
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i][i] == 0) {
                for (int j = i + 1; j < matrix.length; j++) {
                    if (matrix[j][i] != 0) {
                        swap(i, j);
                        break;
                    }
                }
            }
            
            if (matrix[i][i] == 0) {
                continue;
            }
            
            for (int j = i + 1; j < matrix.length; j++) {
                if (matrix[j][i] != 0) {
                    double scalar = -matrix[j][i] / matrix[i][i];
                    matrix[j] = addMultipleOfRow(matrix[i], matrix[j], scalar);
                }
            }
            
        }

        moveZeros();    
    }

    private void moveZeros() {
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

    private void toReducedRowEchelonForm() {
        toRowEchelonForm();

        for (int i = 0; i < matrix.length; i++) {
            int pivot = 0;
            while (pivot < matrix[i].length && matrix[i][pivot] == 0) {
                pivot++;
            }
        
            if (pivot < matrix[i].length) {
                double pivotValue = matrix[i][pivot];
                for (int j = 0; j < matrix[i].length; j++) {
                    matrix[i][j] /= pivotValue;
                }
        
                for (int j = 0; j < i; j++) {
                    if (matrix[j][pivot] != 0) {
                        double scalar = - matrix[j][pivot];
                        matrix[j] = addMultipleOfRow(matrix[i], matrix[j], scalar);
                    }
                }
            }
        }
        

    }

    private List<List<Double>> getGeneralSolution() {
        boolean[] variables = new boolean[matrix[0].length - 1];
        
        for (int i = 0; i < matrix.length; i++) {
            int pivot = 0;
            while (pivot < matrix[i].length - 1 && matrix[i][pivot] == 0) {
                pivot++;
            }
            if (pivot < matrix[i].length - 1) {
                variables[pivot] = true;
            }
        }

        List<Double> starterVector = new ArrayList<>();
        for (int i = 0; i < variables.length; i++) {
            if (variables[i]) {
                starterVector.add(matrix[i][matrix[0].length - 1]);
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
                        vector.add(-matrix[pivotRow][i]);
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

    public void printMatrix(){
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

    public void printRowEchelonForm() {
        toRowEchelonForm();
        printMatrix();
    }

    public void printReducedRowEchelonForm() {
        toReducedRowEchelonForm();
        printMatrix();
    }

    public void printGeneralSolution() {
        List<List<Double>> vectors = getGeneralSolution();
        List <Double> firstVector = vectors.get(0);
        boolean noSolution = true;

        for (double d : firstVector) {
            if (d != 0) {
                noSolution = false;
                break;
            }
        }

        if (noSolution) {
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
    
    public static void main(String[] args) {
        Matrix matrix = new Matrix("src/matrix.txt");
        System.out.println("Original matrix:");
        matrix.printMatrix();
        System.out.println();
        System.out.println("Row echelon form:");
        matrix.printRowEchelonForm();
        System.out.println();
        System.out.println("Reduced row echelon form:");
        matrix.printReducedRowEchelonForm();
        System.out.println();
        System.out.println("General solution:");
        matrix.printGeneralSolution();
    }
    
}   
