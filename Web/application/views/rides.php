    <div class="container-fluid">
        <div class="page-header">
            <div class="row align-items-end">
                <div class="col-lg-8">
                    <div class="page-header-title">
                        <i class="ik ik-corner-up-right bg-blue"></i>
                        <div class="d-inline">
                            <h5>Rides</h5>
                            <span><?php echo count($data);?> riding events have been taken.</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <div class="row">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-header"><h3>Ride List</h3></div>
                    <div class="card-body">
                        <div class="dt-responsive">
                            <table id="simpletable" class="table table-striped table-bordered nowrap">
                                <thead>
                                    <tr>
                                        <th>No</th>
                                        <th>Driver</th>
                                        <th>Passenger</th>
                                        <th>From.To.Date.</th>
                                        <th>Payment(XOF)</th>
                                        <th>Review</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php $i = 0; foreach($data as $key=>$value) { $i++;?>
                                    <tr >
                                        <td><?php echo $i;?></td>
                                        <td><img src="<?php echo $value['driver']['photo'];?>" onerror="this.onerror=null; this.src='<?php echo base_url();?>assets/img/avatar.png'" class="rounded-circle" style="width:50px; height:auto;" alt=""><br><?php echo $value['driver']['name'];?><br>
                                            +<?php echo $value['driver']['phone'];?>
                                        </td>
                                        <td><img src="<?php echo $value['passenger']['photo'];?>" onerror="this.onerror=null; this.src='<?php echo base_url();?>assets/img/avatar.png'" class="rounded-circle" style="width:50px; height:auto;" alt=""><br><?php echo $value['passenger']['name'];?><br>
                                            +<?php echo $value['passenger']['phone'];?><br><?php echo $value['passenger']['email'];?>
                                        </td>
                                        <td>
                                            From: <?php echo $value['from_address'];?><br>
                                            To:&nbsp;&nbsp;&nbsp;&nbsp; <?php echo $value['to_address'];?><br><br>
                                            <div class="row">
                                                <div class="col-md-4"><p style="color:red;"><?php echo $value['dateStr'];?><p></div>
                                                <?php if ($value['isSOS']) { ?>
                                                <div class="col-md-4"><label class="badge badge-danger">SOS<label></div>
                                                <?php } ?>
                                            </div>
                                        </td>
                                        <td>
                                            <?php if ($value['transaction_id'] == "") echo $value['price'].'<br>(Cache)'; else echo $value['price'].'<br>(EdiaPay: '.$value['transaction_id'].')';?>
                                        </td>
                                        <td>
                                            <i class="ik ik-star-on"></i>&nbsp;<?php echo $value['rate'];?><br>
                                            <?php echo $value['review'];?>
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