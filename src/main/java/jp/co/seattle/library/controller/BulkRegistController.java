package jp.co.seattle.library.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
public class BulkRegistController {
    final static Logger logger = LoggerFactory.getLogger(BulkRegistController.class);

    @Autowired
    private BooksService booksService;

    @Autowired
    private ThumbnailService thumbnailService;

    @RequestMapping(value = "/bulkRegist", method = RequestMethod.GET) //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String bulkRegist(Model model) {
        return "bulkRegist";
    }
    @Transactional
    @RequestMapping(value = "/bulkRegistBook", method = RequestMethod.POST, produces = "text/plain;charset=utf-8") //value＝actionで指定したパラメータ
    //RequestParamでname属性を取得
    public String bulkRegistBook(
            @RequestParam("csvFile") MultipartFile csvFile,
            Model model) {
        List<String[]> arrays = new ArrayList<String[]>();
        //エラー用リスト生成
        List<String> erorrList = new ArrayList<String>();

        String[] splitLine = new String[6];

        //文字列として読み込めるように   
        try {
            InputStream stream = csvFile.getInputStream();
            Reader reader = new InputStreamReader(stream);
            BufferedReader buf = new BufferedReader(reader);
            String line;
            while ((line = buf.readLine()) != null) {
                splitLine = line.split(",");
                arrays.add(splitLine);
                //バリデーションチェック
                //必須項目が空の時(title,author,publisher)
                if (splitLine[0].isEmpty() || splitLine[1].isEmpty() || splitLine[2].isEmpty()) {
                    erorrList.add(arrays.size() + "何行目のタイトル、著者名、出版社のどれかが空です");
                }
                //ISBNが10または13桁であるか
                boolean isValidIsbn = splitLine[4].matches("[0-9]{10}||[0-9]{13}");
                if (!isValidIsbn) {
                    erorrList.add(arrays.size() + "行目のISBNに10行または13行の数字を入れてください");
                }
                //日付が8桁であり、成り立つ数字かチェック
                try {
                    DateFormat df = new SimpleDateFormat("yyyyMMdd");
                    df.setLenient(false);
                    df.format(df.parse(splitLine[3]));
                } catch (ParseException p) {
                    erorrList.add(arrays.size() + "行目の日付が間違っています");
                }
                //↓whileの終わり
            }
            //ファイルを読み込めなかった時
        } catch (IOException e) {
        }
        //エラーリストがからではない時エラー文言
        if (!erorrList.isEmpty()) {
            model.addAttribute("erorrList", erorrList);
            return "bulkRegist";
        }
        for (int i = 0; i < arrays.size(); i++) {
        //リストを取り出す
        BookDetailsInfo bookInfo = new BookDetailsInfo();
        bookInfo.setTitle(arrays.get(i)[0]);
        bookInfo.setAuthor(arrays.get(i)[1]);
        bookInfo.setPublisher(arrays.get(i)[2]);
        bookInfo.setPublishDate(arrays.get(i)[3]);
        bookInfo.setIsbn(arrays.get(i)[4]);
        bookInfo.setDescription(arrays.get(i)[5]);
        
        booksService.registBook(bookInfo);
    }
        model.addAttribute("resultMessage", "登録完了");
        return "bulkRegist";
    }
}