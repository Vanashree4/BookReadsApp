package io.java.betterreads.userbooks;

import io.java.betterreads.book.Book;
import io.java.betterreads.book.BookRepository;
import io.java.betterreads.user.BooksByUser;
import io.java.betterreads.user.BooksByUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class UserBooksController {

    @Autowired
    private UserBooksRepository userBooksRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BooksByUserRepository booksByUserRepository;


    @PostMapping("/addUserBook")
    public ModelAndView addBookForUser(@RequestBody MultiValueMap<String,String> formData, @AuthenticationPrincipal OAuth2User principal) {

        if(principal==null||principal.getAttribute("login")==null){
            return null;
        }
        String bookId = formData.getFirst("bookId");
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (!optionalBook.isPresent()) {
            return new ModelAndView("redirect:/");
        }
        Book book = optionalBook.get();
        UserBooks userBooks=new UserBooks();
        UserBooksPrimaryKey userBooksPrimaryKey=new UserBooksPrimaryKey();
        String userId=principal.getAttribute("login");
        userBooksPrimaryKey.setUserId(userId);
        userBooksPrimaryKey.setBookId(bookId);
        userBooks.setKey(userBooksPrimaryKey);

        int rating = Integer.parseInt(formData.getFirst("rating"));
        userBooks.setStartedDate(LocalDate.parse(formData.getFirst("startDate")));
        userBooks.setCompletedDate(LocalDate.parse(formData.getFirst("completedDate")));
        userBooks.setRating(rating);
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));

        userBooksRepository.save(userBooks);

        BooksByUser booksByUser = new BooksByUser();
        booksByUser.setId(userId);
        booksByUser.setBookId(bookId);
        booksByUser.setBookName(book.getName());
        booksByUser.setCoverIds(book.getCoverIds());
        booksByUser.setAuthorNames(book.getAuthorNames());
        booksByUser.setReadingStatus(formData.getFirst("readingStatus"));
        booksByUser.setRating(rating);
        booksByUserRepository.save(booksByUser);

        return new ModelAndView("redirect:/books/"+bookId);
    }
}
