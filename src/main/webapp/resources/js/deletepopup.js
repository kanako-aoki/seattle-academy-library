$(function(){
	$('.btn_deleteBook').on('click', function(e)  {
  		
 	if(!confirm('本当に削除しますか？')){
        /* キャンセルの時の処理 */
        return false;
    }
});
	let result=$('.rentDisabled').val();
 
	if(result==='貸し出し可能です。'){
		$('.btn_returnBook').prop('disabled', true);
	} else {
		$('.btn_rentBook').prop('disabled', true);
	}
	});
	