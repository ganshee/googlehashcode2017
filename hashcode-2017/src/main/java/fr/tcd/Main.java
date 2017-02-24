package fr.tcd;

import fr.tcd.result.ResultWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    private static int coupleIndex = 0;
    public static InputData INPUT_DATA;

    public static void main(String[] args) throws IOException {
        try {
            for (String file : args) {
                initData(file);
                compute();
                generateResult(file);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected static void generateResult(String file) throws IOException {
        System.out.println("generateResult");

        ResultWriter.write(file, INPUT_DATA.caches);
    }

    private static void compute() {
        System.out.println("Compute");

        computeByEnpointCacheCouples();
    }

    protected static void initData(final String filename) throws FileNotFoundException {
        System.out.println("initData");

        final Scanner in = new Scanner(ClassLoader.getSystemResourceAsStream(filename));
        final int nbVideos = in.nextInt();
        final int nbEndpoints = in.nextInt();
        final int nbRequestDescriptions = in.nextInt();
        final int nbCaches = in.nextInt();
        final int cacheSize = in.nextInt();

        System.out.printf("nbVideos : %d \n", nbVideos);
        System.out.printf("nbEndpoints : %d \n", nbEndpoints);
        System.out.printf("nbRequestDescriptions : %d \n", nbRequestDescriptions);
        System.out.printf("nbCaches : %d \n", nbCaches);
        System.out.printf("cacheSize : %d \n", cacheSize);

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

            for (int i = 0; i < numberConnectedCaches; i++) {
                final int cacheId = in.nextInt();
                final int cacheLatency = in.nextInt();
                System.out.println("cacheId: " + cacheId);
                System.out.println("cacheLatency: " + cacheLatency);
                caches.stream()
                        .filter((c) -> c.id == cacheId)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Cache with id " + cacheId + " not found"))
                        .addEnPoint(endpoint, cacheLatency);
            }

            endpoints.add(endpoint);
        }

        final List<Request> requests = new ArrayList<>();
        for (int requestId = 0; requestId < nbRequestDescriptions; requestId++) {
            System.out.println("requestId: " + requestId + "/" + nbRequestDescriptions);

            int videoId = in.nextInt();
            int endpointId = in.nextInt();
            int nbRequest = in.nextInt();

            final Video video = videos.stream()
                    .filter((v) -> v.id == videoId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Video with id " + videoId + " not found"));

            final Endpoint endpoint = endpoints.stream()
                    .filter((e) -> e.id == endpointId)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Endpoint with id " + endpointId + " not found"));
            final Request request = new Request(requestId, video, endpoint, nbRequest);
            requests.add(request);

        }

        INPUT_DATA = new InputData(
                nbVideos,
                nbEndpoints,
                nbRequestDescriptions,
                nbCaches,
                cacheSize,
                videos,
                caches,
                Collections.unmodifiableList(endpoints),
                Collections.unmodifiableList(requests));
        System.out.println("initData END");
    }

    public static void computeByEnpointCacheCouples() {
        List<Cache> caches = INPUT_DATA.caches;

        List<CacheEnpointCouple> cacheEnpointCouples = new ArrayList<>();
        caches.forEach(cache -> cache.endpoints.forEach((endpoint, cacheEnpointLatency) -> cacheEnpointCouples
                .add(new CacheEnpointCouple(cache, endpoint, cacheEnpointLatency))));

        cacheEnpointCouples.sort(Comparator.comparingInt(c -> -c.latencyGain));

        int cacheEnpointCouplesSize = cacheEnpointCouples.size();
        for (CacheEnpointCouple cacheEnpointCouple : cacheEnpointCouples) {
            coupleIndex++;
            System.out.println("Remaining couples: " + (cacheEnpointCouplesSize - coupleIndex));
            storeVideosInCache(cacheEnpointCouple);
        }
    }

    private static void storeVideosInCache(CacheEnpointCouple cacheEnpointCouple) {
        List<Request> requests = INPUT_DATA.requests;
        Cache cacheServer = cacheEnpointCouple.cache;
        Endpoint endpoint = cacheEnpointCouple.endpoint;

        System.out.println("Cache:" + cacheEnpointCouple.cache.id);
        System.out.println("Endpoint:" + endpoint.id);

        System.out.println("Remaining requests:" + requests.size());

        if (cacheServer.getAvailableSpace(INPUT_DATA.cacheSize) != 0) {
            final List<Request> filteredRequests = requests.stream()
                    .filter(request -> request.endpoint.id == endpoint.id)
                    .collect(Collectors.toList());
            while (true) {
                Optional<Request> requestToCache = filteredRequests.stream()
                        .filter(request -> cacheServer.videos.stream().mapToInt(video -> video.id)
                                .noneMatch(value -> value == request.video.id))
                        .filter(request -> request.video.weight <= cacheServer.getAvailableSpace(INPUT_DATA.cacheSize))
                        .sorted(Comparator.comparingInt(r -> r.nbRequest * r.video.weight))
                        .findFirst();

                if (!requestToCache.isPresent()) {
                    break;
                }

                System.out.println("Nb request: " + requestToCache.get().nbRequest + " | " + "Video weight: " + requestToCache.get().video.weight);

                // System.out.println("Request:" + requestToCache.get().id);

                // Add video in cacheServer
                cacheServer.videos.add(requestToCache.get().video);

                // Request has been caches => we remove it
                requests.remove(requestToCache.get());
            }
        }
    }
}
