package io.java.betterreads.book;


import io.java.betterreads.userbooks.UserBooks;
import io.java.betterreads.userbooks.UserBooksPrimaryKey;
import io.java.betterreads.userbooks.UserBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class BookController {



    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserBooksRepository userBooksRepository;

    private final String COVER_IMAGE_ROOT = "http://covers.openlibrary.org/b/id/";

    private final String NO_IMAGE_ROOT="https://via.placeholder.com/150?text=No+Image+Available";

    @GetMapping("/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal){
        Optional<Book> optionalBook = bookRepository.findById(bookId);

        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            String coverImageUrl=NO_IMAGE_ROOT;
            if(book.getCoverIds()!=null && book.getCoverIds().size()>0){
                coverImageUrl= COVER_IMAGE_ROOT+ book.getCoverIds().get(0)+"-L.jpg";
            }
            model.addAttribute("coverImage",coverImageUrl);
            model.addAttribute("book",book);

            if(principal!=null&& principal.getAttribute("login")!=null){
                String userId = principal.getAttribute("login");
                model.addAttribute("loginId", userId);

                UserBooksPrimaryKey userBooksPrimaryKey=new UserBooksPrimaryKey();
                userBooksPrimaryKey.setUserId(userId);
                userBooksPrimaryKey.setBookId(bookId);

                Optional<UserBooks> userBooks = userBooksRepository.findById(userBooksPrimaryKey);
                if(userBooks.isPresent()){
                    model.addAttribute("userBooks",userBooks.get());
                }
                else{
                    model.addAttribute("userBooks",new UserBooks());
                }
            }
            return "book";
        }

        return "book-not-found";

    }
}
