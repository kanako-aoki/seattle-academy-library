$(function(){
	$('.btn_deleteBook').on('click', function(e)  {
  		
 	if(!confirm('本当に削除しますか？')){
        /* キャンセルの時の処理 */
        return false;
    }
});
	});