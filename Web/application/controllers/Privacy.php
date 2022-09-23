<?php
class Privacy extends CI_Controller {

	function __construct()
    {
        parent::__construct();
		$this->load->database();
		$this->load->model('model');
		$this->load->library('session');
		$this->load->helper('url');
		if (session_status() == PHP_SESSION_NONE) {
			
			session_id(SITE);
		    session_start();
		}
    }

	public function index()
	{
		$this->load->view('header', array('active' => 'privacy'));
		$this->load->view('privacy');
		$this->load->view('footer');
	}
}
?>