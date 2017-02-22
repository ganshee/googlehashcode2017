package fr.tcd;

public class Pizza {
    public int col;
    public int row;
    public int minIngredients;
    public int maxSize;
    public Cell[][] cells;

    public Pizza(int row, int col, int minIngredients, int maxSize) {
        this.row = row;
        this.col = col;
        this.minIngredients = minIngredients;
        this.maxSize = maxSize;
        cells = new Cell[row][col];
    }

    public void addCell(int r, int c, Ingredient ingredient) {
        cells[r][c] = new Cell(ingredient);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(col + " " + row + "\n");
        for (int rowNum = 0; rowNum < row; rowNum++) {
            for (int colNum = 0; colNum < col; colNum++) {
                sb.append(cells[rowNum][colNum].ingredient);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
