import org.example.crawler.web.CrawlerUrl;

import java.net.URL;

public class Application {
    public static void main(String[] args) throws Exception {
        System.out.println(new CrawlerUrl(Integer.parseInt(args[1])).top100words(new URL(args[0])));
    }
}
