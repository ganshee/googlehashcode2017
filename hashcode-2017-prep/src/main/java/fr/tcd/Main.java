package fr.tcd;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    // public static GeneralConfig CONFIG;

    public static void main(String[] args) {
        try {
            initData("kittens.in");
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

    protected static void initData(final String filename) throws FileNotFoundException {
        final Scanner in = new Scanner(ClassLoader.getSystemResourceAsStream(filename));
        int r = in.nextInt();
        int c = in.nextInt();
        int min = in.nextInt();
        int max = in.nextInt();
        in.nextLine();
    }
}
