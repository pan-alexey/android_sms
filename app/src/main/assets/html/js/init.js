(function($){
  $(function(){
    $('.button-collapse').sideNav();

  }); // end of document ready
})(jQuery); // end of jQuery name space



//открытие активити, с необходимыми настройками:
window.openActivityWithDenided = function(){
	//Показываем активность с необходимой информацией
	$('#ActivityWithDenided, #AppSettingButton').addClass('scale-in');
}



//открытие активити, с необходимыми настройками ():
window.openActivityWithGranted = function(){
	//Показываем активность с необходимой информацией
	$('#ActivityWithDenided, #AppSettingButton').removeClass('scale-in').one(
	'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function(){
      $(this).addClass('hidden');
    });;
	
	
	
}