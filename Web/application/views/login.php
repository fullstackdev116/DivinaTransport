<!doctype html>
<html class="no-js" lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <title>Login | DivinaTransport Admin</title>
        <meta name="description" content="">
        <meta name="keywords" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        
        <link rel="icon" href="<?php echo base_url();?>assets/img/logo.png" type="image/x-icon" />
        
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/bootstrap/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/fontawesome-free/css/all.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/ionicons/dist/css/ionicons.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/icon-kit/dist/css/iconkit.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/perfect-scrollbar/css/perfect-scrollbar.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/dist/css/theme.min.css">
        <script src="<?php echo base_url();?>assets/src/js/vendor/modernizr-2.8.3.min.js"></script>
    </head>

    <body>
        <!--[if lt IE 8]>
            <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
        <![endif]-->

        <div class="auth-wrapper">
            <div class="container-fluid h-100">
                <div class="row flex-row h-100 bg-white">
                    <div class="col-xl-8 col-lg-6 col-md-5 p-0 d-md-block d-lg-block d-sm-none d-none">
                        <div class="lavalite-bg" style="background-image: url('<?php echo base_url();?>assets/img/login-bg.jpg')">
                            <div class="lavalite-overlay"></div>
                        </div>
                    </div>
                    <div class="col-xl-4 col-lg-6 col-md-7 my-auto p-0">
                        <div class="authentication-form mx-auto">
                            <img src="<?php echo base_url();?>assets/img/logo.png" alt="">
                            <br><br><br><br>
                            <h3>Sign In to Divina Transport Admin</h3>
                            
                            <form action="<?php echo base_url();?>login/do_login" method="post">
                                <div class="form-group">
                                    <input type="email" name="email" class="form-control" placeholder="Email" required value="<?php echo $email;?>">
                                    <i class="ik ik-user"></i>
                                </div>
                                <div class="form-group">
                                    <input type="password" name="password" class="form-control" placeholder="Password" required value="<?php echo $password;?>">
                                    <i class="ik ik-lock"></i>
                                </div>
                                <!-- <div class="row">
                                    <div class="col text-left">
                                        <label class="custom-control custom-checkbox" style="display: flex;">
                                            <input type="checkbox" class="custom-control-input" id="item_checkbox" name="item_checkbox" value="option1">
                                            <span class="custom-control-label">&nbsp;Remember Me</span>
                                        </label>
                                    </div>
                                    <div class="col text-right" style="display:none;">
                                        <a href="forgot-password.html">Forgot Password ?</a>
                                    </div>
                                </div> -->
                                <p style="color:red; text-align: center;"><?php echo $res; ?></p>
                                <div class="sign-btn text-center">
                                    <button type="submit" class="btn btn-theme">Sign In</button>
                                </div>
                            </form>
                            <div class="register" style="display: none;">
                                <p>Don't have an account? <a href="register.html">Create an account</a></p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
        <script>window.jQuery || document.write('<script src="<?php echo base_url();?>assets/src/js/vendor/jquery-3.3.1.min.js"><\/script>')</script>
        <script src="<?php echo base_url();?>assets/plugins/popper.js/dist/umd/popper.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/bootstrap/dist/js/bootstrap.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/perfect-scrollbar/dist/perfect-scrollbar.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/screenfull/dist/screenfull.js"></script>
        <script src="<?php echo base_url();?>assets/dist/js/theme.js"></script>
        <!-- Google Analytics: change UA-XXXXX-X to be your site's ID. -->
        <script>
            (function(b,o,i,l,e,r){b.GoogleAnalyticsObject=l;b[l]||(b[l]=
            function(){(b[l].q=b[l].q||[]).push(arguments)});b[l].l=+new Date;
            e=o.createElement(i);r=o.getElementsByTagName(i)[0];
            e.src='https://www.google-analytics.com/analytics.js';
            r.parentNode.insertBefore(e,r)}(window,document,'script','ga'));
            ga('create','UA-XXXXX-X','auto');ga('send','pageview');
        </script>
    </body>
</html>
