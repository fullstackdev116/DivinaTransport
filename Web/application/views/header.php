<!doctype html>
<html class="no-js" lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="x-ua-compatible" content="ie=edge">
        <title>Divina Transport - Admin</title>
        <meta name="description" content="">
        <meta name="keywords" content="">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        
        <link rel="icon" href="favicon.ico" type="image/x-icon" />

        
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/bootstrap/dist/css/bootstrap.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/fontawesome-free/css/all.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/icon-kit/dist/css/iconkit.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/ionicons/dist/css/ionicons.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/perfect-scrollbar/css/perfect-scrollbar.css">

        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/datatables.net-bs4/css/dataTables.bootstrap4.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/tempusdominus-bootstrap-4/build/css/tempusdominus-bootstrap-4.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/c3/c3.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/owl.carousel/dist/assets/owl.carousel.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/owl.carousel/dist/assets/owl.theme.default.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/datatables.net-bs4/css/dataTables.bootstrap4.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/owl.carousel/dist/assets/owl.carousel.min.css">
        <link rel="stylesheet" href="<?php echo base_url();?>assets/plugins/owl.carousel/dist/assets/owl.theme.default.min.css">


        <link rel="stylesheet" href="<?php echo base_url();?>assets/dist/css/theme.min.css">
        <script src="<?php echo base_url();?>assets/src/js/vendor/modernizr-2.8.3.min.js"></script>
    </head>

    <body> <!-- oncontextmenu="return false;" -->
        <!--[if lt IE 8]>
            <p class="browserupgrade">You are using an <strong>outdated</strong> browser. Please <a href="http://browsehappy.com/">upgrade your browser</a> to improve your experience.</p>
        <![endif]-->

        <div class="wrapper">
            <header class="header-top" header-theme="light">
                <div class="container-fluid">
                    <div class="d-flex justify-content-between">
                        <div class="top-menu d-flex align-items-center">
                            <button type="button" class="btn-icon mobile-nav-toggle d-lg-none"><span></span></button>
                            <button type="button" id="navbar-fullscreen" class="nav-link"><i class="ik ik-maximize"></i></button>
                        </div>
                        <div class="top-menu d-flex align-items-center">
                            <div class="dropdown">
                                <a class="dropdown-toggle" href="#" id="userDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><img class="avatar" src="<?php echo base_url();?>assets/img/avatar.png" alt=""></a>
                                <div class="dropdown-menu dropdown-menu-right" aria-labelledby="userDropdown">
                                    <a class="dropdown-item" href="<?php echo base_url();?>home/profile"><i class="ik ik-user dropdown-icon"></i> Profile</a>
                                    <a class="dropdown-item" href="<?php echo base_url();?>login/do_logout"><i class="ik ik-power dropdown-icon"></i> Logout</a>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </header>

            <div class="page-wrap">
                <div class="app-sidebar colored">
                    <div class="sidebar-header">
                        <a class="header-brand" href="index.html">
                            <div class="logo-img" style="display: none;">
                               <img src="<?php echo base_url();?>assets/src/img/brand-white.svg" class="header-brand-img" alt="lavalite"> 
                            </div>
                            <span class="text"><?php echo SITE; ?></span>
                        </a>
                        <button type="button" class="nav-toggle"><i data-toggle="expanded" class="ik ik-toggle-right toggle-icon"></i></button>
                        <button id="sidebarClose" class="nav-close"><i class="ik ik-x"></i></button>
                    </div>
                    
                    <div class="sidebar-content">
                        <div class="nav-container">
                            <nav id="main-menu-navigation" class="navigation-main">
                                <div class="nav-lavel">Navigation</div>
                                <div class="nav-item <?php if ($active == 'dashboard') echo 'active';?>">
                                    <a href="<?php echo base_url();?>home/dashboard"><i class="ik ik-bar-chart-2"></i><span>Dashboard</span></a>
                                </div>
                                <div class="nav-item <?php if ($active == 'drivers') echo 'active';?>">
                                    <a href="<?php echo base_url();?>home/drivers"><i class="ik ik-users"></i><span>Drivers</span> <span class="badge badge-success"><?php if ($new_driver > 0) echo '+'.$new_driver;?></span></a>
                                </div>
                                <div class="nav-item <?php if ($active == 'passengers') echo 'active';?>">
                                    <a href="<?php echo base_url();?>home/passengers"><i class="ik ik-users"></i><span>Passengers</span></a>
                                </div>
                                <div class="nav-item <?php if ($active == 'rides') echo 'active';?>">
                                    <a href="<?php echo base_url();?>home/rides"><i class="ik ik-corner-up-right"></i><span>Rides</span></a>
                                </div>
                                <div class="nav-item <?php if ($active == 'map') echo 'active';?>">
                                    <a href="<?php echo base_url();?>home/map"><i class="ik ik-map"></i><span>Map</span></a>
                                </div>
                                
                            </nav>
                        </div>
                    </div>
                </div>
                <div class="main-content" style="background:#efefef;">

        <script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
        <script>window.jQuery || document.write('<script src="<?php echo base_url();?>assets/src/js/vendor/jquery-3.3.1.min.js"><\/script>')</script>
        <script src="<?php echo base_url();?>assets/plugins/popper.js/dist/umd/popper.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/bootstrap/dist/js/bootstrap.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/perfect-scrollbar/dist/perfect-scrollbar.min.js"></script>

        <script src="<?php echo base_url();?>assets/plugins/screenfull/dist/screenfull.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/datatables.net/js/jquery.dataTables.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/datatables.net-bs4/js/dataTables.bootstrap4.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/datatables.net-responsive-bs4/js/responsive.bootstrap4.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/moment/moment.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/tempusdominus-bootstrap-4/build/js/tempusdominus-bootstrap-4.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/d3/dist/d3.min.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/c3/c3.min.js"></script>
        <script src="<?php echo base_url();?>assets/js/tables.js"></script>
        <script src="<?php echo base_url();?>assets/js/widgets.js"></script>
        <script src="<?php echo base_url();?>assets/js/charts.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/flot-charts/jquery.flot.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/flot-charts/jquery.flot.categories.js"></script>
        <script src="<?php echo base_url();?>assets/js/datatables.js"></script>
        <script src="<?php echo base_url();?>assets/plugins/owl.carousel/dist/owl.carousel.min.js"></script>
        <script src="<?php echo base_url();?>assets/js/carousel.js"></script>


        <script src="<?php echo base_url();?>assets/dist/js/theme.min.js"></script>