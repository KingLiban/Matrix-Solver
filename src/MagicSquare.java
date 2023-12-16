import java.util.Scanner;

public class MagicSquare {

    private int n;
    private int[][] magicSquare;
    private int magicConstant;
    
    public MagicSquare(int n) {
        this.n = n;
        magicSquare = new int[n][n];
        if (n % 2 == 1) {
            createMagicSquare(magicSquare, 1);
        } else {
            magicConstant = n * (n * n + 1) / 2;
            int[][] magicSquareTopLeft = new int[n/ 2][n / 2];
            int[][] magicSquareTopRight = new int[n / 2][n / 2];
            int[][] magicSquareBottomLeft = new int[n / 2][n / 2];
            int[][] magicSquareBottomRight = new int[n / 2][n / 2];

            createMagicSquare(magicSquareTopLeft, 1);
            createMagicSquare(magicSquareTopRight, n / 2 + 1);
            createMagicSquare(magicSquareBottomLeft, n / 2 + 1);
            createMagicSquare(magicSquareBottomRight, (n / 2) * n / 2 + 1);


            for (int i = 0; i < n / 2; i++) {
                for (int j = 0; j < n / 2; j++) {
                    magicSquare[i][j] = magicSquareTopLeft[i][j];
                    magicSquare[i][j + n / 2] = magicSquareTopRight[i][j];
                    magicSquare[i + n / 2][j] = magicSquareBottomLeft[i][j];
                    magicSquare[i + n / 2][j + n / 2] = magicSquareBottomRight[i][j];
                }
            }

            fixMagicSquare();
        }
    }

    // The Siamese method :D
    private void createMagicSquare(int[][] magicSquare, int startingCell) {
        magicSquare[0][n / 2] = 1;
        int[] currentCell = { 0, n / 2 };
        int cell = startingCell + 1;
    
        while (cell <= n * n) {
            int newRow = (currentCell[0] - 1 + n) % n;
            int newCol = (currentCell[1] + 1) % n;
    
            if (magicSquare[newRow][newCol] == 0) {
                magicSquare[newRow][newCol] = cell;
                currentCell[0] = newRow;
                currentCell[1] = newCol;
                cell++;
            } else {
                newRow = (currentCell[0] - 1 + n) % n;
                newCol = currentCell[1];
                magicSquare[newRow][newCol] = cell;
                currentCell[0] = newRow;
                currentCell[1] = newCol;
                cell++;
            }
        }
    }
    
    

    private void fixMagicSquare() {
        for (int i = 0; i < n / 2; i++) {
            int rowSum = 0;
            for (int j = 0; j < magicSquare[0].length; j++) {
                rowSum += magicSquare[i][j];
            }
            if (rowSum != magicConstant) {
                swap(i, 0, i + n / 2, 0);
            }
        }
        
        int diagonalSum = 0;
        for (int i = 0; i < magicSquare.length; i++) {
            int diagonalElement = magicSquare[i][i];
            diagonalSum += diagonalElement;
        }
        if (diagonalSum != magicConstant) {
            swap(1, 1, n / 2 + 1, n / 2 + 1);
        }
    }

    private void swap(int i, int j, int k, int l) {
        int temp = magicSquare[i][j];
        magicSquare[i][j] = magicSquare[k][l];
        magicSquare[k][l] = temp;
    }

    public void printMagicSquare() {
        for (int[] row : magicSquare) {
            for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }
    
   public static void main(String[] args) {
       Scanner scanner = new Scanner(System.in);
       System.out.println("Enter the size of the magic square: ");
       int n = scanner.nextInt();
       if (n % 4 == 2 || n % 2 == 1) {
            MagicSquare magicSquare = new MagicSquare(n);
            magicSquare.printMagicSquare();
       } else {
            System.out.println("The size of the magic square must be odd!");
       }
    
   }
}
