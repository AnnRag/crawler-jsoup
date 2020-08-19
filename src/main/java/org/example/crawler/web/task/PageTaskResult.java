package org.example.crawler.web.task;

import lombok.Builder;
import lombok.Getter;

import java.net.URL;
import java.util.Map;
import java.util.Set;

@Builder
@Getter
public class PageTaskResult {
    private Set<URL> nextUrl;
    private Map<String, Integer> wordsCount;
    private int depth;
}
