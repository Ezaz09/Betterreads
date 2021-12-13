package io.javabrains.betterreads.book;

import io.javabrains.betterreads.userbooks.UserBooks;
import io.javabrains.betterreads.userbooks.UserBooksPrimaryKey;
import io.javabrains.betterreads.userbooks.UserBooksRepository;
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

    private final String COVER_IMAGE_ROOT = "https://covers.openlibrary.org/b/id/";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserBooksRepository userBooksRepository;

    @GetMapping(value = "/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(optionalBook.isEmpty()) {
            return "book-not-found";
        }
        Book book = optionalBook.get();
        String coverImageUrl = "/images/no-image.png";
        if(book.getCoverIds() != null && book.getCoverIds().size() > 0){
            coverImageUrl = COVER_IMAGE_ROOT + book.getCoverIds().get(0) + "-L.jpg";
        }
        model.addAttribute("coverImage", coverImageUrl);
        model.addAttribute("book", book);
        if(principal !=null && principal.getAttribute("login") != null) {
            String userId = principal.getAttribute("login");
            model.addAttribute("loginId", userId);
            UserBooksPrimaryKey key = new UserBooksPrimaryKey();
            key.setBookId(bookId);
            key.setUserId(userId);
            Optional<UserBooks> userBooks = userBooksRepository.findById(key);
            if(userBooks.isPresent()){
                model.addAttribute("userBooks", userBooks.get());
            } else {
                model.addAttribute("userBooks",new UserBooks());
            }

        }

        return "book";
    }
}
