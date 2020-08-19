package org.example.crawler.web.task;

import lombok.Builder;
import lombok.Getter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class PageTask implements Callable<PageTaskResult> {

    private URL url;
    private int depth;

    private Set<URL> urlList = new HashSet<>();

    public PageTask(URL url, int depth){
        this.url = url;
        this.depth = depth;
    }
    @Override
    public PageTaskResult call() throws Exception {
        Document document = Jsoup.parse(url, 60000);
        Elements links = document.select("a[href]");
        for (Element link : links) {
            String href = link.attr("href");
             URL nextUrl = new URL(url, href);
             urlList.add(nextUrl);
        }
        return PageTaskResult.builder().nextUrl(urlList).wordsCount(wordCount(document)).depth(depth).build();
    }

    private Map<String, Integer> wordCount(Document document) {
        Map<String, Integer> wordsCount = new HashMap<>();
        String[] words = document.body().text().split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^\\w]", "");
            wordsCount.compute(words[i], (k, v) -> v == null ? 1 : v + 1);
        }
        return wordsCount;
    }
}
