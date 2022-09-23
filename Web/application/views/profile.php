  <div class="main-content">
    <div class="container-fluid">
        <div class="page-header">
        <div class="row align-items-end">
            <div class="col-lg-8">
                <div class="page-header-title">
                    <i class="ik ik-users bg-blue"></i>
                    <div class="d-inline">
                        <h5>Admin Profile Setting</h5>
                        <span>You can update email and password.</span>
                    </div>
                </div>
            </div>
        </div>
        </div>
        <div class="card-body">
            <form id="form" class="form-horizontal" action="<?php echo base_url();?>login/update_profile" method="post">
                <div class="form-group">
                    <label for="example-email">Email</label>
                    <input type="email" name="email" placeholder="johnathan@admin.com" class="form-control" name="example-email" id="example-email" value="<?php echo $_SESSION[SITE]['email'];?>" required>
                </div>
                <div class="form-group">
                    <label for="example-password">Password</label>
                    <input id="password" type="password" name="password" value="<?php echo $_SESSION[SITE]['password'];?>" class="form-control" name="example-password" id="example-password" required>
                </div>
                <div class="form-group">
                    <label for="example-password">Confirm Password</label>
                    <input id="password_confirm" type="password" name="password_confirm" value="" class="form-control" name="example-password" id="example-password" required>
                </div>
                
                <button class="btn btn-success" id="update" type="submit">Update Profile</button>
            </form>
        </div>
    </div>
</div>
                        
<script type="text/javascript">
    $(document).ready(function() {
        
        $('form').submit(function(){
            $password = $("#password").val();
            $password_confirm = $("#password_confirm").val();
            if ($password != $password_confirm) {
                alert("Password doesn't match");
                return false;
            }
            return true;
        });
    
    });
</script>