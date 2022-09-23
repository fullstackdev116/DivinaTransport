  <div class="main-content">
    <div class="container-fluid">
        <div class="page-header">
            <div class="row align-items-end">
                <div class="col-lg-8">
                    <div class="page-header-title">
                        <i class="ik ik-users bg-blue"></i>
                        <div class="d-inline">
                            <h5>Drivers</h5>
                            <span><?php echo count($data);?> drivers have been joined.</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="row">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header"><h3>Driver List</h3></div>
                    <div class="card-body">
                        <div class="dt-responsive">
                            <table id="simpletable" class="table table-striped table-bordered nowrap">
                                <thead>
                                    <tr>
                                        <th>No</th>
                                        <th class="nosort" >Avatar</th>
                                        <th>Contact</th>
                                        <th>Points</th>
                                        <th>Rate</th>
                                        <th>Accepts/Rejects</th>
                                        <th>Info</th>
                                        <th>State</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php $i = 0; foreach($data as $key=>$value) { $i++;?>
                                    <tr style="background: <?php if ($value['state'] == 3) {
                                            echo 'white'; } else if ($value['state'] == 2) {
                                            echo 'orange';} else if ($value['state'] == 1) {
                                            echo 'gray';}?>;">
                                        <td><?php echo $i;?></td>
                                        <td><img src="<?php echo $value['photo'];?>" onerror="this.onerror=null; this.src='<?php echo base_url();?>assets/img/avatar.png'" class="rounded-circle" style="width:80px; height:auto;" alt=""><br><?php echo $value['name'];?></td>
                                        <td>+<?php echo $value['phone'];?><br><?php echo $value['email'];?></td>
                                        <td><?php echo $value['point'];?></td>
                                        <td><i class="ik ik-star-on"></i>&nbsp;<?php echo $value['rate'];?></td>
                                        <td><?php echo $value['rides'];?>&nbsp;/&nbsp;<?php echo $value['rejects'];?></td>
                                        <td>
                                            <a href="" class="btn_dl btn btn-primary">Driving License</a>&nbsp;
                                            <?php if($value['state'] > 1) {?>
                                            <a href="" class="btn_car btn btn-success">Car</a>
                                            <?php }?>
                                        </td>
                                        <td>
                                            <?php if ($value['state'] == 3) {?>
                                            <a href="<?php echo base_url();?>home/driverDisable/<?php echo $key;?>" style="color:gray;">Disable</a>
                                            <?php } else if ($value['state'] == 2) {?>
                                            <a href="<?php echo base_url();?>home/driverEnable/<?php echo $key;?>" style="color:blue;">Enable</a>
                                            <?php }?>
                                            <p class="data-uid" style="display:none;"><?php echo $key;?></p>
                                        </td>
                                        
                                    </tr>
                                    <?php } ?>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
                        <div class="modal fade" id="DLModal" tabindex="-1" role="dialog" aria-labelledby="DLModelLabel" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered" role="document">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title" id="DLModelLabel">Driving License</h5>
                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    </div>
                                    <div class="modal-body" style="padding:20px;">
                                        <medium class="text-muted d-block">Photo </medium>
                                        <img id="dl_photo" class="img-fluid rounded" src="<?php echo base_url();?>assets/img/default.png" style="width:300px; height: auto;margin-left: 50px;"><br><br>
                                        <div class="row">
                                            <div class="col-md-6">
                                                <medium class="text-muted d-block">First Name </medium>
                                                <h6 id="firstname">John</h6> 
                                            </div>
                                            <div class="col-md-6">
                                                <medium class="text-muted d-block">Last Name </medium>
                                                <h6 id="lastname">Dae</h6> 
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-6">
                                                <medium class="text-muted d-block">Gender </medium>
                                                <h6 id="gender">M</h6> 
                                            </div>
                                            <div class="col-md-6">
                                                <medium class="text-muted d-block">Birth Date </medium>
                                                <h6 id="birthdate">03/01/2000</h6> 
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-6">
                                                <medium class="text-muted d-block">Issue Date </medium>
                                                <h6 id="issuedate">03/01/2020</h6> 
                                            </div>
                                            <div class="col-md-6">
                                                <medium class="text-muted d-block">Expiry Date </medium>
                                                <h6 id="expirydate">03/01/2023</h6> 
                                            </div>
                                        </div>
                                        <medium class="text-muted d-block">Number </medium>
                                        <h6 id="number">A12345879</h6> 
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="modal fade" id="CarModal" tabindex="-1" role="dialog" aria-labelledby="CarModelLabel" aria-hidden="true">
                            <div class="modal-dialog modal-dialog-centered" role="document">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title" id="CarModelLabel">Car Information</h5>
                                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                                    </div>
                                    <div class="modal-body" style="padding:20px;">
                                        <medium class="text-muted d-block">Photo </medium>
                                        <img id="car_photo" class="img-fluid rounded" src="<?php echo base_url();?>assets/img/default.png" style="width:300px; height: auto;margin-left: 50px;"><br><br>
                                        <medium class="text-muted d-block">Type </medium>
                                        <h6 id="car_type">John</h6> 
                                        <medium class="text-muted d-block">Seats </medium>
                                        <h6 id="car_seats">Dae</h6> 
                                        <medium class="text-muted d-block">Price(per mile) </medium>
                                        <h6 id="car_price">M</h6> 
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                                    </div>
                                </div>
                            </div>
                        </div>
<script type="text/javascript">
    $(document).ready(function() {
        $(".btn_dl").click(function(e){
            e.preventDefault();
            var uid = $(this).closest('tr').children('td').children('p.data-uid').text();
            var url = '<?php echo base_url();?>home/getDL/'+uid;
            $.ajax({
                type: "GET",
                url: url,
                data: null,
                success: function(res) {
                    var data = jQuery.parseJSON(res);
                    var gender = data.gender;
                    console.log(gender);
                    $("#dl_photo").attr('src', data.photo);
                    $("#firstname").html(data.first_name);
                    $("#lastname").html(data.last_name);
                    $("#gender").html(data.gender);
                    $("#birthdate").html(data.birth_date);
                    $("#issuedate").html(data.issue_date);
                    $("#expirydate").html(data.expiry_date);
                    $("#number").html(data.number);
                    $('#DLModal').modal('show');
                }
            });
        }); 
        $(".btn_car").click(function(e){
            e.preventDefault();
            var uid = $(this).closest('tr').children('td').children('p.data-uid').text();
            var url = '<?php echo base_url();?>home/getCar/'+uid;
            $.ajax({
                type: "GET",
                url: url,
                data: null,
                success: function(res) {
                    var data = jQuery.parseJSON(res);
                    var gender = data.gender;
                    console.log(gender);
                    $("#car_photo").attr('src', data.photo);
                    $("#car_type").html(data.type);
                    $("#car_seats").html(data.seats);
                    $("#car_price").html(data.price);
                    $('#CarModal').modal('show');
                }
            });
        }); 
        
    
    });
</script>