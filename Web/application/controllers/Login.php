<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Login extends MY_Controller {

	function __construct()
    {
        parent::__construct();
		
    }

	public function index($email = '', $password = '', $res = '')
	{
		if (!$this->isSessionAvailable()) {
			$this->load->view('login', array('email' => $email, 'password' => $password, 'res' => $res));
		} else {
			redirect('/home/dashboard');
		}
	}

	public function do_login() {
		$email = $_POST['email']; $password = $_POST['password'];
		$email_ = $this->getValueByKey(TBL_ADMIN.'email');
		$password_ = $this->getValueByKey(TBL_ADMIN.'password');

		if ($email_ == $email && $password_ == $password) {
			$this->session->set_userdata(SITE, array('email'=>$email, 'password' => $password));
			redirect('/home');
		} else {
			$this->index($email, $password, 'Invalid email or password.');
		}
	}
	public function update_profile() {
		$email = $_POST['email']; $password = $_POST['password'];
		$this->setValueWithKey(TBL_ADMIN.'email', $email);
		$this->setValueWithKey(TBL_ADMIN.'password', $password);
		$this->session->set_userdata(SITE, array('email'=>$email, 'password' => $password));
		redirect('/home/profile');
	}

	function do_logout() {
		$this->session->set_userdata(SITE);
		$this->session->sess_destroy();
		redirect(base_url());
	}

}
