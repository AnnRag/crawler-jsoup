package org.example.crawler.web;

import org.example.crawler.web.task.PageTask;
import org.example.crawler.web.task.PageTaskResult;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CrawlerUrl {

    private final int depth;

    private List<Future<PageTaskResult>> resultFuture = new ArrayList<>();

    private final static Pattern EXCLUSIONS
            = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4" +
            "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    private final HashSet<String> links = new HashSet<>();

    private final ExecutorService exc = Executors.newFixedThreadPool(12);

    public CrawlerUrl(int depth) {
        this.depth = depth;
    }

    public List<String> top100words(URL baseUrl) throws Exception {

        Future<PageTaskResult> future = visit(baseUrl, baseUrl, 0);
        if (future==null) throw new RuntimeException("Not found page");
        PageTaskResult taskResult = future.get();
        for (URL url : taskResult.getNextUrl()) {
            if (url.toString().contains("#")) {
                url = new URL(url.toString().substring(0, url.toString().indexOf("#")));
            }

            visit(url, baseUrl, taskResult.getDepth() + 1);
        }

        Map<String, Integer> wordsCount = new HashMap<>();
        for (Future<PageTaskResult> pageTaskResultFuture:resultFuture) {
            PageTaskResult res = pageTaskResultFuture.get();
            Map<String, Integer> currentWordCount = res.getWordsCount();
            currentWordCount.entrySet().stream().forEach(entry -> wordsCount.compute(entry.getKey(), (k, v) -> v == null ? entry.getValue() : v + entry.getValue()));
        }
        return wordsCount.entrySet()
                .stream()
                .sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
                .map(Map.Entry::getKey)
                .limit(100)
                .collect(Collectors.toList());

    }

    private Future<PageTaskResult> visit(URL url, URL baseUrl, int currentDepth) {
        if (shouldVisit(url, baseUrl, currentDepth)) {
            Future<PageTaskResult> futureTask = exc.submit(new PageTask(url, currentDepth));
            resultFuture.add(futureTask);
            return futureTask;
        }
        return null;
    }

    private boolean shouldVisit(URL url, URL baseUrl, int currentDepth) {
        if (currentDepth>depth) return false;
        String urlString = url.getPath().toLowerCase();
        return !EXCLUSIONS.matcher(urlString).matches()
                && urlString.startsWith(baseUrl.getPath());
    }
}
