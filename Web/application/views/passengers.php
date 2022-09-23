  <div class="main-content">
    <div class="container-fluid">
        <div class="page-header">
            <div class="row align-items-end">
                <div class="col-lg-8">
                    <div class="page-header-title">
                        <i class="ik ik-users bg-blue"></i>
                        <div class="d-inline">
                            <h5>Passengers</h5>
                            <span><?php echo count($data);?> drivers have been joined.</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="row">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header"><h3>Passenger List</h3></div>
                    <div class="card-body">
                        <div class="dt-responsive">
                            <table id="simpletable" class="table table-striped table-bordered nowrap">
                                <thead>
                                    <tr>
                                        <th>No</th>
                                        <th class="nosort" >Avatar</th>
                                        <th>Contact</th>
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
                                        <td><img src="<?php echo $value['photo'];?>" onerror="this.onerror=null; this.src='<?php echo base_url();?>assets/img/avatar.png'" class="rounded-circle" style="width:50px; height:auto;" alt=""><br><?php echo $value['name'];?></td>
                                        <td>+<?php echo $value['phone'];?><br><?php echo $value['email'];?></td>
                                        <td>
                                            <?php if ($value['state'] == 3) {?>
                                            <a href="<?php echo base_url();?>home/passengerDisable/<?php echo $key;?>" style="color:gray;">Disable</a>
                                            <?php } else if ($value['state'] == 2) {?>
                                            <a href="<?php echo base_url();?>home/passengerEnable/<?php echo $key;?>" style="color:blue;">Enable</a>
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
                        
<script type="text/javascript">
    $(document).ready(function() {
        
    
    });
</script>