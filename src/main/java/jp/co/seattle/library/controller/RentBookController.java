package jp.co.seattle.library.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

@Controller //APIの入り口
public class RentBookController {

    final static Logger logger = LoggerFactory.getLogger(RentBookController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    /**書籍を借りる際
     * @param bookId
     * @param model
     * @return
     */
    @RequestMapping(value = "/rentBook", method = RequestMethod.POST) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得   
    public String rentBook(
            @RequestParam("bookId") int bookId,
            Model model) {
        model.addAttribute("lendingStatus", "貸し出し中");

        booksService.rentBook(bookId);
        model.addAttribute("rentDisabled", "disabled");
        return "details";
    }

    /**書籍を返す際
     * @param bookId
     * @param model
     * @return
     */
    @RequestMapping(value = "/returnBook", method = RequestMethod.POST) //value＝actionで指定したパラメータ
    public String returnBook(
            @RequestParam("bookId") int bookId,
            Model model) {
        model.addAttribute("lendingStatus", "貸し出し可");

        booksService.returnBook(bookId);
        model.addAttribute("returnDisabled", "disabled");
        return "details";
    }

}
