package jp.co.seattle.library.controller;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.service.BooksService;
import jp.co.seattle.library.service.ThumbnailService;

@Controller //APIの入り口
public class EditBookController {
    final static Logger logger = LoggerFactory.getLogger(AddBooksController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @RequestMapping(value = "/editBook", method = RequestMethod.POST) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String edit(
            @RequestParam("bookId") int bookId,
            Model model) {
        BookDetailsInfo bookDetailsInfo = booksService.getBookInfo(bookId);
        model.addAttribute("bookDetailsInfo", bookDetailsInfo);
        return "editBook";
    }

    /**
     * 書籍情報を登録する
     * @param locale ロケール情報
     * @param title 書籍名
     * @param author 著者名
     * @param publisher 出版社
     * @param file サムネイルファイル
     * @param model モデル
     * @return 遷移先画面
     */
    @Transactional
    @RequestMapping(value = "/editUpdBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8")
    public String insertBook(Locale locale,
            @RequestParam("bookId") int bookId,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam("publisher") String publisher,
            @RequestParam("publish_date") String publishDate,
            @RequestParam("thumbnail") MultipartFile file,
            @RequestParam("isbn") String isbn,
            @RequestParam("description") String description,
            Model model) {
        logger.info("Welcome editBooks.java! The client locale is {}.", locale);

        // パラメータで受け取った書籍情報をDtoに格納する。
        BookDetailsInfo bookInfo = new BookDetailsInfo();
        bookInfo.setBookId(bookId);
        bookInfo.setTitle(title);
        bookInfo.setAuthor(author);
        bookInfo.setPublisher(publisher);
        bookInfo.setPublishDate(publishDate);
        bookInfo.setIsbn(isbn);
        bookInfo.setDescription(description);

        // クライアントのファイルシステムにある元のファイル名を設定する
        String thumbnail = file.getOriginalFilename();

        if (!file.isEmpty()) {
            try {
                // サムネイル画像をアップロード
                String fileName = thumbnailService.uploadThumbnail(thumbnail, file);
                // URLを取得
                String thumbnailUrl = thumbnailService.getURL(fileName);

                bookInfo.setThumbnailName(fileName);
                bookInfo.setThumbnailUrl(thumbnailUrl);

            } catch (Exception e) {

                // 異常終了時の処理
                logger.error("サムネイルアップロードでエラー発生", e);
                model.addAttribute("bookDetailsInfo", bookInfo);
                return "addBook";
            }
        }

        //バリデーションチェック
        //ISBNが10または13桁であるか
        boolean isValidIsbn = isbn.matches("[0-9]{10}||[0-9]{13}");

        if (!isValidIsbn) {
            model.addAttribute("error", "ISBNの桁数または半角数字が正しくありません。出版日は半角英数字のYYYYMMDD形式で入力してください。");
            return "addBook";
        }

        //日付が8桁であり、成り立つ数字かチェック
        try {
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            df.setLenient(false); // ←これで厳密にチェックしてくれるようになる
            df.format(df.parse(publishDate)); // ←df.parseでParseExceptionがThrowされる
        } catch (ParseException p) {

            model.addAttribute("error", "ISBNの桁数または半角数字が正しくありません。出版日は半角英数字のYYYYMMDD形式で入力してください。");
            return "addBook";

        }
        // 書籍情報を編集する
        booksService.editBookInfo(bookInfo);

        // TODO 編集した書籍の詳細情報を表示するように実装
        BookDetailsInfo newBookDetailsInfo = booksService.getBookInfo(bookId);
        model.addAttribute("bookDetailsInfo", newBookDetailsInfo);

        //  詳細画面に遷移する
        return "details";
    }

}