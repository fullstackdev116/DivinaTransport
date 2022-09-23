<?php
defined('BASEPATH') OR exit('No direct script access allowed');
?>
<!DOCTYPE html><!--  Last Published: Tue Aug 02 2022 16:30:02 GMT+0000 (Coordinated Universal Time)  -->
<html data-wf-page="62e2d0635effa7791366d24d" data-wf-site="6299ab0efe185171423aaf3e">
<head>
  <meta charset="utf-8">
  <title>Ripple Search - Token Verify</title>
  <meta content="width=device-width, initial-scale=1" name="viewport">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
  	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.0.0/dist/js/bootstrap.min.js" integrity="sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl" crossorigin="anonymous"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/webfont/1.6.26/webfont.js" type="text/javascript"></script>
  <script type="text/javascript">WebFont.load({  google: {    families: ["Open Sans:300,300italic,400,400italic,600,600italic,700,700italic,800,800italic","Montserrat:100,100italic,200,200italic,300,300italic,400,400italic,500,500italic,600,600italic,700,700italic,800,800italic,900,900italic","Roboto:300,regular,500","Comfortaa:300,regular,500,600,700"]  }});</script>
  <!-- [if lt IE 9]><script src="https://cdnjs.cloudflare.com/ajax/libs/html5shiv/3.7.3/html5shiv.min.js" type="text/javascript"></script><![endif] -->
  <script type="text/javascript">!function(o,c){var n=c.documentElement,t=" w-mod-";n.className+=t+"js",("ontouchstart"in o||o.DocumentTouch&&c instanceof DocumentTouch)&&(n.className+=t+"touch")}(window,document);</script>
  <link href="<?php echo base_url().'assets/';?>images/favicon.png" rel="shortcut icon" type="image/x-icon">
  <link href="<?php echo base_url().'assets/';?>images/webclip.png" rel="apple-touch-icon">
  <script src="https://cdn.mojoauth.com/js/mojoauth.min.js">
      </script>
</head>
 <body style="background: #dfe4e7; font-family: 'Varela Round', sans-serif;text-align: center; padding: 20px;">
 	
 	<p id="msg">Token Verifying...</p>
 	<a id="link" href="<?php echo base_url();?>" style=" display: none;">Click here to login again</a>


 </body>


<script type="text/javascript">
        $(document).ready(function() {
        	console.log('ok');
        	var res = "<?php echo $res;?>";
        	setTimeout(function() {
        		if (res == '200') {
	                window.location ="<?php echo base_url();?>";
        		} else {
        			$("#msg").html('Your token has been expired!');
        			$("#link").css('display', 'block');
        		}
			}, 2000);
        });
    </script>
