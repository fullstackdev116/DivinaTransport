<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Ripples extends CI_Controller {

	function __construct()
    {
        parent::__construct();
		$this->load->database();
		$this->load->model('model');
		$this->load->library('session');
		$this->load->helper('url');
		if (session_status() === PHP_SESSION_NONE) {
		    session_start();
		}
    }

	public function index()
	{
		$this->ripples();
	}

	function thankyou()
	{
		$usercode = $this->model->get_last_usercode();
		$this->load->view('header', array('active' => ''));
		$this->load->view('thankyou', array('usercode' => $usercode));
		$this->load->view('footer');
	}

	function ripples()
	{
		$ref_code = "";
		$input_params=$this->input->get();
		if (count($input_params)>0) {
			$ref_code = $input_params['ref'];
		}
		$this->load->view('header', array('active' => 'ripples'));
		$this->load->view('ripples', array('ref_code' => $ref_code));
		$this->load->view('footer');
	}

	function randomString() {
		$length = 8;
		$string= "0123456789abcdefghijklmnopqrstuvwxyz!@#$%&*()";
	    return substr(str_shuffle( $string), 0, $length);
	}

	function do_signup() {
		$email = $_POST['email'];
		$firstname = $_POST['firstname'];
		$lastname = $_POST['lastname'];
		$ref_code = $_POST['ref_code'];
		$usercode = $this->randomString();
		//if ($this->do_post($email, $firstname, $lastname, $usercode)) {
			$result = $this->model->signup($email, $firstname, $lastname, $usercode, $ref_code, $data_array);
			if ($result == 200) {
				$this->session->set_userdata(SITE, $data_array['data']);
			}
			echo $result;
		//} else {
		//	echo 400;
		//}
	}

	function do_post($email, $firstname, $lastname, $usercode)
	{
	  $ch = curl_init();

		curl_setopt($ch, CURLOPT_URL, 'https://emailoctopus.com/api/1.6/lists/a1413e24-ec09-11ec-9258-0241b9615763/contacts');
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_POSTFIELDS, "{\"api_key\":\"ae74f7af-b97a-4504-8e6c-e2a50b24d067\",\"email_address\":\"".$email."\",\"fields\":{\"FirstName\":\"".$firstname."\",\"LastName\":\"".$lastname."\",\"Usercode\":\"".$usercode."\"},\"tags\":[\"prelaunch\"],\"status\":\"SUBSCRIBED\"}");

		$headers = array();
		$headers[] = 'Content-Type: application/json';
		curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

		$result = curl_exec($ch);
		if (curl_errno($ch)) {
		    echo 'Error:' . curl_error($ch);
		}
		curl_close($ch);
		$res_arr = json_decode($result,true);
		if (isset($res_arr['error'])) {
			return false;
		}
		return true;
	}

	function do_login() {
		$email = $_POST['email'];
		$result = $this->model->token_reister($email, $token);
		if ($result == 200) {
			// ------------------send email-------------------
			// $this->sendMail($email, $token);
		}
		echo $result;
	}
  	function sendMail(string $email, string $token)
  	{
      	$activation_link = base_url()."login?token=".$token;
        $config = Array(
          'protocol' => 'smtp',
          'smtp_host' => 'hostm.ripplesearch.com',
          'smtp_port' => 25,// default port
          'smtp_user' => 'no-reply@ripplesearch.com',
          'smtp_pass' => 'no-reply',
          'mailtype' => 'html',
          'charset' => 'iso-8859-1',
          'wordwrap' => TRUE
        );

        $message = "
	            Hi, Please click the following link to verify your token:<br>
	            $activation_link
	            ";
        $this->load->library('email', $config);
        $this->email->set_newline("\r\n");
        $this->email->from('no-reply@ripplesearch.com', 'RippleSearch'); // change it to yours
        $this->email->to($email);// change it to yours
        $this->email->subject('Please verify your token');
        $this->email->message($message);
        if($this->email->send())
       	{
        	//echo 'Email sent.';
       	}
       	else {
         	show_error($this->email->print_debugger());
        }
  	}
	
	function logout() {
		$this->session->set_userdata(SITE);
		$this->session->sess_destroy();
		redirect(base_url().'search');
	}
}
