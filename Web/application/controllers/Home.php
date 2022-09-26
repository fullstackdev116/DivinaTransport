<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Home extends MY_Controller {

	function __construct()
    {
        parent::__construct();
        if (!$this->isSessionAvailable()) {
			redirect('/login');
		}
    }

	public function index()
	{
		$this->dashboard();
	}

	public function dashboard() {
		$array = $this->getValueByKey(TBL_USER);
		$driverCnt = 0; $passengerCnt = 0; $points = 0;
		$array_top_rate = array();

		foreach ($array as $key => $value) {
			if ($value['type'] == 'DRIVER') {
		    	$driverCnt ++;
		    	$points += $value['point'];
		    } else {
		    	$passengerCnt ++;
		    }
		    if ($value['rate'] >= 4.5 && $value['rides'] >= 1 && $value['rejects'] == 0) {
		    	array_push($array_top_rate, $value);
		    }
		}
		$array = $this->getValueByKey(TBL_HISTORY);
		$rideCnt = 0; $sosCnt = 0; $ediapay = 0; $cache = 0;
		$array_payment = array_fill(0, 12, 0);
		foreach ($array as $key => $value) {
		    if ($value['isSOS']) {
		    	$sosCnt ++;
		    }
		    $rideCnt ++;
		    if ($value['transaction_id'] == "") {
		    	$cache += $value['price'];
		    } else {
		    	$ediapay += $value['price'];
		    }
		    $array_payment[$value['date']['month']] += $value['price'];
		}

		// usort($array_sort, function ($a, $b) {
		//     return $a['date'] <=> $b['date'];
		// });

		$this->load->view('header', array('active' => 'dashboard', 'new_driver' => $this->getNewDriver()));
		$this->load->view('dashboard', array('driverCnt' => number_format($driverCnt), 'passengerCnt' => number_format($passengerCnt), 'rideCnt' => number_format($rideCnt), 'sosCnt' => number_format($sosCnt), 'points' => number_format($points), 'cache' => $cache, 'ediapay' => $ediapay, 'total_price' => number_format($cache+$ediapay), 'array_payment' => $array_payment, 'array_top_rate' => $array_top_rate));
		$this->load->view('footer');
	}

	public function profile() {
		$this->load->view('header', array('active' => 'profile', 'new_driver' => $this->getNewDriver()));
		$this->load->view('profile');
		$this->load->view('footer');
	}

	public function drivers() {
		$array = $this->getValueByKey(TBL_USER);
		foreach ($array as $key => $value) {
			if ($value['type'] != 'DRIVER') {
		        unset($array[$key]); continue;
		    }
		}
		$this->load->view('header', array('active' => 'drivers', 'new_driver' => $this->getNewDriver()));
		$this->load->view('drivers', array('data' => $array));
		$this->load->view('footer');
	}

	public function passengers() {
		$array = $this->getValueByKey(TBL_USER);
		foreach ($array as $key => $value) {
		    if ($value['type'] != 'PASSENGER') {
		        unset($array[$key]); continue;
		    }
		} 
		$this->load->view('header', array('active' => 'passengers', 'new_driver' => $this->getNewDriver()));
		$this->load->view('passengers', array('data' => $array));
		$this->load->view('footer');
	}

	public function map() {
		$this->load->view('header', array('active' => 'map', 'new_driver' => $this->getNewDriver()));
		$this->load->view('map');
		$this->load->view('footer');
	}

	public function rides() {
		$array_history = $this->getValueByKey(TBL_HISTORY);
		$array_user = $this->getValueByKey(TBL_USER);
		foreach ($array_history as $key => $value) {
			foreach ($array_user as $key1 => $value1) {
			    if ($value['driver_id'] == $key1) {
			    	$array_history[$key]['driver'] = $value1;
			    }
			    if ($value['passenger_id'] == $key1) {
			    	$array_history[$key]['passenger'] = $value1;
			    }
			} 
			$date = (string)$value['date']['date'];
			$month = (string)($value['date']['month']+1);
			$year = (string)(2000+$value['date']['year']-100);
			$array_history[$key]['dateStr'] = $month.'/'.$date.'/'.$year;
		} 
		
		$this->load->view('header', array('active' => 'rides', 'new_driver' => $this->getNewDriver()));
		$this->load->view('rides', array('data' => $array_history));
		$this->load->view('footer');
	}

	function getDL($uid) {
		$array = $this->getValueByKey(TBL_DL); 
		$res = array();
		foreach ($array as $key => $value) {
		    if ($value['uid'] == $uid) {
		    	$res = $value;
		    	break;
		    }
		}
		echo json_encode($res);
	}

	function driverDisable($uid) {
		$this->setValueWithKey(TBL_USER.$uid.'/state', 2);
		redirect('/home/drivers');
	}

	function driverEnable($uid) {
		$this->setValueWithKey(TBL_USER.$uid.'/state', 3);
		redirect('/home/drivers');
	}

	function passengerDisable($uid) {
		$this->setValueWithKey(TBL_USER.$uid.'/state', 2);
		redirect('/home/passengers');
	}

	function passengerEnable($uid) {
		$this->setValueWithKey(TBL_USER.$uid.'/state', 3);
		redirect('/home/passengers');
	}
	function getCar($uid) {
		$array = $this->getValueByKey(TBL_CAR); 
		$res = array();
		foreach ($array as $key => $value) {
		    if ($value['uid'] == $uid) {
		    	$res = $value;
		    	break;
		    }
		}
		echo json_encode($res);
	}

	function getNewDriver() {
		$array = $this->getValueByKey(TBL_USER);
		$newDriver = 0;
		foreach ($array as $key => $value) {
		    if (!in_array('DRIVER', $value)) {
		        continue;
		    }
		    if ($value['state'] == 2) {
		    	$newDriver ++;
		    }
		}
		return $newDriver;
	}
}
