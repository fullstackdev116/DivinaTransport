    <div class="container-fluid">
        <div class="row clearfix">
            <div class="col-lg-3 col-md-6 col-sm-12">
                <div class="widget">
                    <div class="widget-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div class="state">
                                <h6>Drivers</h6>
                                <h2><?php echo $driverCnt;?></h2>
                            </div>
                            <div class="icon">
                                <i class="ik ik-users"></i>
                            </div>
                        </div>
                        <medium class="text-medium mt-10 d-block">Total drivers</medium>
                    </div>
                    <div class="progress progress-sm">
                        <div class="progress-bar bg-danger" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%;"></div>
                    </div>
                </div>
            </div>
            <div class="col-lg-3 col-md-6 col-sm-12">
                <div class="widget">
                    <div class="widget-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div class="state">
                                <h6>Passengers</h6>
                                <h2><?php echo $passengerCnt;?></h2>
                            </div>
                            <div class="icon">
                                <i class="ik ik-users"></i>
                            </div>
                        </div>
                        <medium class="text-medium mt-10 d-block">Total passengers</medium>
                    </div>
                    <div class="progress progress-sm">
                        <div class="progress-bar bg-warning" role="progressbar" aria-valuenow="31" aria-valuemin="0" aria-valuemax="100" style="width: 100%;"></div>
                    </div>
                </div>
            </div>
            <div class="col-lg-3 col-md-6 col-sm-12">
                <div class="widget">
                    <div class="widget-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div class="state">
                                <h6>Riding / SOS</h6>
                                <h2><?php echo $rideCnt;?> / <?php echo $sosCnt;?></h2>
                            </div>
                            <div class="icon">
                                <i class="ik ik-corner-up-right"></i>
                            </div>
                        </div>
                        <medium class="text-medium mt-10 d-block">Total riding&nbsp;/&nbsp;SOS events</medium>
                    </div>
                    <div class="progress progress-sm">
                        <div class="progress-bar bg-success" role="progressbar" aria-valuenow="78" aria-valuemin="0" aria-valuemax="100" style="width: 100%;"></div>
                    </div>
                </div>
            </div>
            
            <div class="col-lg-3 col-md-6 col-sm-12">
                <div class="widget">
                    <div class="widget-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div class="state">
                                <h6>Rewards</h6>
                                <h2><?php echo $points;?></h2>
                            </div>
                            <div class="icon">
                                <i class="ik ik-award"></i>
                            </div>
                        </div>
                        <medium class="text-medium mt-10 d-block">Total points archied by drivers</medium>
                    </div>
                    <div class="progress progress-sm">
                        <div class="progress-bar bg-info" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100" style="width: 100%;"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-4">
                <div class="card" style="min-height: 422px;">
                    <div class="card-header"><h3>Payments (<?php echo $total_price;?> XOF)</h3></div>
                    <div class="card-body">
                        <div id="c3-donut-chart"></div>
                    </div>
                </div>
            </div>
            <div class="col-md-8">
                <div class="card">
                    <div class="card-header">
                        <h3>Payments per Month</h3>
                    </div>
                    <div class="card-block">
                        <div id="placeholder" class="demo-placeholder" style="height:300px;"></div>
                    </div>
                </div>
            </div>
        </div> 
        <div class="row">
            <div class="col-md-12 mb-4 pl-0 pr-0">
                <div class="card">
                    <div class="card-header">
                        <h3>Top Rated Drivers</h3>
                    </div>
                    <div class="owl-container">
                        <div class="owl-carousel basic">
                            <?php for ($i = 0; $i < count($array_top_rate); $i++) { ?>
                            <div class="card flex-row">
                                <div class="w-50 position-relative">
                                    <img class="card-img-left rounded-circle" src="<?php echo $array_top_rate[$i]['photo'];?>" alt="Card image cap" style="width:100px;">
                                    <span class="badge badge-pill badge-primary position-absolute badge-top-left" style="right: -80px; position: relative !important; top: -100px">Top</span>
                                    <i class="ik ik-star-on"></i>&nbsp;<?php echo $array_top_rate[$i]['rate'];?>
                                </div>
                                <div class="w-50 position-absolute" style="left: 100px;">
                                    <div class="card-body">
                                        <h6 class="mb-4"><?php echo $array_top_rate[$i]['name'];?></h6>

                                        <footer>
                                            <p class="text-muted text-small mb-0 font-weight-light">+<?php echo $array_top_rate[$i]['phone'];?></p>
                                            <p class="text-muted text-small mb-0 font-weight-light"><?php echo $array_top_rate[$i]['email'];?></p>

                                        </footer>
                                    </div>
                                </div>
                            </div>
                            <?php } ?>
                            
                        </div>
                        <div class="slider-nav text-center">
                            <a href="#" class="left-arrow owl-prev">
                                <i class="ik ik-chevron-left"></i>
                            </a>
                            <div class="slider-dot-container"></div>
                            <a href="#" class="right-arrow owl-next">
                                <i class="ik ik-chevron-right"></i>
                            </a>
                        </div>
                    </div>
                </div>

            </div>
        </div>                     
    </div>
</div>
<script type="text/javascript">
  $(document).ready(function() {
    $(window).on('resize', function() {
        categoryChart();
    });
    categoryChart();

    /*categories chart*/
    function categoryChart() {
        var data = [
            ["Jan", <?php echo $array_payment[0];?>],
            ["Feb", <?php echo $array_payment[1];?>],
            ["Mar", <?php echo $array_payment[2];?>],
            ["Apr", <?php echo $array_payment[3];?>],
            ["May", <?php echo $array_payment[4];?>],
            ["Jun", <?php echo $array_payment[5];?>],
            ["Jul", <?php echo $array_payment[6];?>],
            ["Aug", <?php echo $array_payment[7];?>],
            ["Sep", <?php echo $array_payment[8];?>],
            ["Oct", <?php echo $array_payment[9];?>],
            ["Nov", <?php echo $array_payment[10];?>],
            ["Dec", <?php echo $array_payment[11];?>],
        ];
        $.plot("#placeholder", [data], {
            series: {
                bars: {
                    show: true,
                    barWidth: 1,
                    align: "center",
                }
            },
            xaxis: {
                mode: "categories",
                tickLength: 0,
                tickColor: '#f5f5f5',
            },
            colors: ["#01C0C8", "#83D6DE"],
            labelBoxBorderColor: "red",
        });
    };

    var c3DonutChart = c3.generate({
    bindto: '#c3-donut-chart',
    data: {
      columns: [
        ["EdiaPay", 0],
        ["Cache", 0],
      ],
      type: 'donut',
      onclick: function(d, i) {
        console.log("onclick", d, i);
      },
      onmouseover: function(d, i) {
        console.log("onmouseover", d, i);
      },
      onmouseout: function(d, i) {
        console.log("onmouseout", d, i);
      }
    },
    color: {
      pattern: ['rgba(237,28,36,0.6)', 'rgba(88,216,163,1)']
    },
    padding: {
      top: 0,
      right: 0,
      bottom: 30,
      left: 0,
    },
    donut: {
      title: "<?php echo $total_price;?> XOF"
    }
  });

  setTimeout(function() {
    c3DonutChart.load({
      columns: [
        ["EdiaPay", <?php echo $ediapay;?>],
        ["Cache", <?php echo $cache;?>],
      ]
    });
  }, 1500);
    
  });
</script>