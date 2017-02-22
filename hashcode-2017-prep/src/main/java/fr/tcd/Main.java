package fr.tcd;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    // public static GeneralConfig CONFIG;
    private static Pizza pizza;
    public static Result RESULT;

    public static void main(String[] args) {
        try {
            initData("example.in");
            compute();
            generateResult();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected static void generateResult() {
        /*
         * StringBuilder result = CONFIG.getResultFile(); boolean firstDrone = true; for (int[] drone :
         * RESULT.getDrones()) { if (!firstDrone) { result.append("\n"); } firstDrone = false; boolean firstDeplacement
         * = true; for (int deplacement : drone) { if (firstDeplacement) { result.append(deplacement); } else {
         * result.append(" " + deplacement); } firstDeplacement = false; } result.append(" " + 0); } try {
         * Files.write(result, new File("result.txt"), StandardCharsets.UTF_8); } catch (IOException e) { // TODO
         * Auto-generated catch block e.printStackTrace(); } System.out.println(result);
         */
    }

    private static void compute() {
        // DO THE THING
    }


    protected static void initData(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(ClassLoader.getSystemResourceAsStream(filename));
        int r = in.nextInt();
        int c = in.nextInt();
        int min = in.nextInt();
        int max = in.nextInt();
        in.nextLine();
        pizza = new Pizza(r, c, min, max);
        for (int rowNum = 0; rowNum < r; rowNum++) {
            String rowValue = in.nextLine();
            for (int colNum = 0; colNum < c; colNum++) {
                pizza.addCell(rowNum, colNum, Ingredient.valueOf("" + rowValue.charAt(colNum)));
            }
        }
        System.out.println(pizza.toString());
    }
}
