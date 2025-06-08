package io.java.betterreads.search;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SearchController {
    private final String COVER_IMAGE_ROOT = "http://covers.openlibrary.org/b/id/";
    private final String NO_IMAGE_ROOT="https://via.placeholder.com/150?text=No+Image+Available";
    private final WebClient webclient;
    public SearchController(WebClient.Builder webClientBuilder){
        this.webclient = webClientBuilder.exchangeStrategies(ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build()).baseUrl("http://openlibrary.org/search.json").build();
    }

    @GetMapping(value="/search")
    public String getSearchResults(@RequestParam String query, Model model){

        Mono<SearchResult> searchResultMono = this.webclient.get().uri("?q={query}", query).
                retrieve().bodyToMono(SearchResult.class);

        SearchResult result=searchResultMono.block();

        List<SearchResultBook> books = result.getDocs().stream().limit(10).
        map(bookResult->{
            bookResult.setKey(bookResult.getKey().replace("/works/",""));
        String coverId= bookResult.getCover_i();
        if(StringUtils.hasText(coverId)){
            coverId=COVER_IMAGE_ROOT+coverId+"-M.jpg";
        }
        else{
            coverId=NO_IMAGE_ROOT;
        }
        bookResult.setCover_i(coverId);
        return bookResult;
        }
        ).collect(Collectors.toList());

        model.addAttribute("searchResults",books);
        return "search";
    }
}
