package fr.tcd.result;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResultWriter {
    public static void main(String[] args) throws IOException {
        ResultWriter.write("test", new Result());
    }

    public static void write(String dataset, Result result) throws IOException {
        Collection<String> lines = new ArrayList<>();

        lines.add(String.valueOf(result.cacheServers.size()));

        result.cacheServers.forEach(cache -> {
            List<String> items = new ArrayList<>();
            items.add(String.valueOf(cache.id));

            cache.videos.stream()
                    .map(video -> String.valueOf(video.id))
                    .forEach(items::add);
            lines.add(String.join(" ", items));
        });


        FileUtils.writeLines(new File(dataset + ".out"), lines);
    }
}
