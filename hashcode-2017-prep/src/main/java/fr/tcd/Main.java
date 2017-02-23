package fr.tcd;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static InputData INPUT_DATA;

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
        final int nbVideos = in.nextInt();
        final int nbEndpoints = in.nextInt();
        final int nbRequestDescriptions = in.nextInt();
        final int nbCaches = in.nextInt();
        final int cacheSize = in.nextInt();

        final List<Cache> caches = IntStream.range(0, nbCaches).mapToObj(Cache::new).collect(Collectors.toList());
        final List<Video> videos = IntStream.range(0, nbVideos)
                .mapToObj((i) -> new Video().setId(i).setWeight(in.nextInt()))
                .collect(Collectors.toList());

        final List<Endpoint> endpoints = new ArrayList<>();

        for (int endpointId = 0; endpointId < nbEndpoints; endpointId++) {
            final int datacenterLatency = in.nextInt();
            final int numberConnectedCaches = in.nextInt();
            final Endpoint endpoint = new Endpoint()
                    .setId(endpointId)
                    .setDatacenterLatency(datacenterLatency)
                    .setNumberConnectedCaches(numberConnectedCaches);
            final int cacheId = in.nextInt();
            final int cacheLatency = in.nextInt();
            caches.stream()
                    .filter((c) -> c.getId() == cacheId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Cache with id " + cacheId + " not found"))
                    .addEnPoint(endpoint, cacheLatency);
            endpoints.add(endpoint);
        }

//        for (int request; )

        INPUT_DATA = new InputData(
                nbVideos,
                nbEndpoints,
                nbRequestDescriptions,
                nbCaches,
                cacheSize,
                videos,
                caches,
                endpoints
        );
        in.nextLine();
    }
}
