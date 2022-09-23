<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Search extends CI_Controller {

	function __construct()
    {
        parent::__construct();
		$this->load->database();
		$this->load->model('model');
		$this->load->library('session');
		$this->load->helper('url');
		if (session_status() == PHP_SESSION_NONE) {
			
			// session_id(SITE);
		    session_start();
		}
    }

	public function index()
	{
		$this->search();
	}

	function getUserID() {
		if ($this->session->userdata(SITE)) {
			return $this->session->userdata(SITE)['id'];
		}
		return -1;
	}

	function search()
	{
		$query = "";
		$input_params=$this->input->get();
		if (count($input_params)>0) {
			$query = $input_params['q'];
		}

		$total_cnt = $this->model->get_total_cnt();
		$individual_cnt = -1; $ref_cnt = -1;
		if ($this->getUserID() > 0) {
			$individual_cnt = $this->model->get_individual_cnt($this->getUserID());
			$ref_cnt = $this->model->get_refer_cnt($this->getUserID());
		}
		$this->load->view('header', array('active' => 'search', 'ref_cnt' => $ref_cnt));
		$this->load->view('search', array('total_cnt' => $total_cnt, 'individual_cnt' => $individual_cnt, 'query' => $query));
		$this->load->view('footer');
	}

	function search_cnt() {
		$result = $this->model->search_cnt($this->getUserID());
		echo 200;
	}
}
