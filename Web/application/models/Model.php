<?php

class Model extends CI_Model
{

	private $DATA_ARRAY = array('data' => array(), 'error' => '');
	private $RES_ARRAY = array('res' => array(), 'reason' => '');

    public function __construct()
    {
        parent::__construct();
    }
	
	function signup($email, $firstname, $lastname, $usercode, $ref_code, &$data_array)
	{
		$data_array = $this->DATA_ARRAY;
		$result = $this->db->get_where('tbl_user', array('email' => $email))->result_array();
		if (count($result) == 0) {
			$this->db->insert('tbl_user', array('firstname' => $firstname, 'lastname' => $lastname, 'email' => $email, 'usercode' => $usercode, 'ref_code' => $ref_code));
			$data_array['data'] = $this->db->get_where('tbl_user', array('email' => $email))->result_array()[0];
			return 200;
		}
		return 400;
	}

	function token_reister($email, &$token)
	{
		$result = $this->db->get_where('tbl_user', array('email' => $email))->result_array();
		if (count($result) > 0) {
			$token = $this->randomTokenString(10);
			$token_created = time();
			$this->db->where('email', $email);
			$this->db->update('tbl_user', array('token' => $token, 'token_created' => $token_created));
			return 200;
		}
		return 400;
	}

	function token_verify($token, &$data_array)
	{
		$data_array = $this->DATA_ARRAY;
		$result = $this->db->get_where('tbl_user', array('token' => $token))->result_array();
		if (count($result) > 0) {
			$time = time();
			$token_created = (int)$result[0]['token_created'];
			$data_array['data'] = $result[0];
			if ($token_created <= $time + 60*5) {
				return 200;
			}
		}
		return 400;
	}

	function randomTokenString($length) {
		$string= "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    return substr(str_shuffle( $string), 0, $length);
	}

	function get_individual_cnt($userid) {
		$result = $this->db->get_where('tbl_user', array('id' => $userid))->result_array();
		if (count($result)>0) {
			return $result[0]['search_cnt'];
		}
		return 0;
	}

	function get_refer_cnt($userid) {
		$usercode = $this->db->get_where('tbl_user', array('id' => $userid))->result_array()[0]['usercode'];
		$result = $this->db->get_where('tbl_user', array('ref_code' => $usercode))->result_array();
		return count($result);
	}

	function get_last_usercode() {
		$result = $this->db->get('tbl_user')->result_array();
		if (count($result)>0) {
			return $result[count($result)-1]['usercode'];
		}
		return '';
	}

	function get_total_cnt() {
		$result = $this->db->get('tbl_global')->result_array();
		return $result[0]['search_cnt'];
	}

	function search_cnt($userid) {
		$result = $this->db->get('tbl_global')->result_array();
		$new_cnt = $result[0]['search_cnt']+1;
		// $this->db->where('search_cnt', $cnt);
		$this->db->update('tbl_global', array('search_cnt' => $new_cnt));
		if ($userid > 0) {
			$result = $this->db->get_where('tbl_user', array('id' => $userid))->result_array();
			$new_user_cnt = $result[0]['search_cnt']+1;
			$this->db->where('id', $userid);
			$this->db->update('tbl_user', array('search_cnt' => $new_user_cnt));
		}
	}
	
}