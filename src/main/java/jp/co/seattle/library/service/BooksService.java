package jp.co.seattle.library.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jp.co.seattle.library.dto.BookDetailsInfo;
import jp.co.seattle.library.dto.BookInfo;
import jp.co.seattle.library.rowMapper.BookDetailsInfoRowMapper;
import jp.co.seattle.library.rowMapper.BookInfoRowMapper;

/**
 * 書籍サービス
 * 
 *  booksテーブルに関する処理を実装する
 */
@Service
public class BooksService {
    final static Logger logger = LoggerFactory.getLogger(BooksService.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 書籍リストを取得する
     *
     * @return 書籍リスト
     */
    public List<BookInfo> getBookList() {

        // TODO 取得したい情報を取得するようにSQLを修正
        //書籍名,著者名,出版社名,出版日,サムネイルの画像,書籍ID
        List<BookInfo> getedBookList = jdbcTemplate.query(
                "select id,title,author,publisher,publish_date,thumbnail_url from books order by title asc",
                new BookInfoRowMapper());

        return getedBookList;
    }

    /**
     * 書籍IDに紐づく書籍詳細情報を取得する
     *
     * @param bookId 書籍ID
     * @return 書籍情報
     */
    public BookDetailsInfo getBookInfo(int bookId) {

        // JSPに渡すデータを設定する
        String sql = "SELECT * FROM books where id ="
                + bookId;

        BookDetailsInfo bookDetailsInfo = jdbcTemplate.queryForObject(sql, new BookDetailsInfoRowMapper());

        return bookDetailsInfo;
    }

    //SQLをinsertする
    public void rentBook(int bookId) {

        String sql = "INSERT INTO rentBook(books_id) VALUES(" + bookId + ");";
        jdbcTemplate.update(sql);
    }
    //SQLをdereteする
    public void returnBook(int bookId) {
        String sql = "DELETE FROM rentBook where books_id =" + bookId + ";";

        jdbcTemplate.update(sql);
    }
    public int countBook(int bookId) {
        String sql = "SELECT COUNT(*)FROM rentBook where books_id =" + bookId + ";";
        int count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count;
    }
    /**
     * 書籍を登録する
     *
     * @param bookInfo 書籍情報
     */
    public void registBook(BookDetailsInfo bookInfo) {
        String sql = "INSERT INTO books (title,description, author,publisher,thumbnail_name,thumbnail_url,publish_date,isbn,reg_date,upd_date) VALUES ('"
                + bookInfo.getTitle() + "','" + bookInfo.getDescription() + "','" + bookInfo.getAuthor() + "','"
                + bookInfo.getPublisher() + "','"
                + bookInfo.getThumbnailName() + "','"
                + bookInfo.getThumbnailUrl() + "','"
                + bookInfo.getPublishDate() + "','"
                + bookInfo.getIsbn() + "',"
                + "sysdate(),"
                + "sysdate())";

        jdbcTemplate.update(sql);
    }
    /**
     * 書籍を詳細画面に表示する
     * 
     */

    public int getNewId() {
        String sql = "select max(id) from books";
        int bookId = jdbcTemplate.queryForObject(sql, Integer.class);
        return bookId;
    }

    /**
     * 書籍を消去する
     * @param bookId
     */
    public void deleteBookInfo(int bookId) {
        String sql = "delete from books where Id =" + bookId + ";";
        jdbcTemplate.update(sql);
    }
    /**
     * 編集情報を変更でする
     * 
     */
    public void editBookInfo(BookDetailsInfo bookInfo) {
        String sql = "UPDATE books SET title ='" + bookInfo.getTitle() + "',author='" + bookInfo.getAuthor() 
            +  "',publisher='"  + bookInfo.getPublisher() + "',thumbnail_name='" + bookInfo.getThumbnailName() 
            + "',thumbnail_url='" + bookInfo.getThumbnailUrl() + "',publish_Date='" + bookInfo.getPublishDate() 
                + "',isbn='" + bookInfo.getIsbn() + "',description='" + bookInfo.getDescription()
                + "',upd_date=" + "sysdate() " + "WHERE Id = " + bookInfo.getBookId() + ";";
        jdbcTemplate.update(sql);
    }
}

