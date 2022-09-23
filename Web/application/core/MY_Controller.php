<?php
defined('BASEPATH') OR exit('No direct script access allowed');

use Kreait\Firebase\Factory;

class MY_Controller extends CI_Controller {
	public $firebase, $database;

	function __construct()
    {
        parent::__construct();
		$this->load->library('session');
		$this->load->helper('url');
		$this->factory = (new Factory())->withDatabaseUri(FB_URL);
		$this->database = $this->factory->createDatabase();
    }
    public function getValueByKey($key) {
    	return $this->database->getReference($key)->getValue();
    }
    public function setValueWithKey($key, $value) {
    	return $this->database->getReference($key)->set($value);
    }
    public function getOrderByChild($tbl, $key, $value) {
    	$dd = $this->database->getReference($tbl)->orderByChild($key)->equalTo($value)->getSnapshot();
    	var_dump($dd);
    	return $this->database->getReference($tbl)->orderByChild($key)->equalTo($value)->getSnapshot();
    }

	




    function getValuesByChild($tbl, $childKey) {
		return $database->getReference($tbl)->orderByChild($childKey)->getSnapshot();
    }
 //    function configureFireBase()
	// {
	//     $config = new Configuration();
	//     $config->setAuthConfigFile(__DIR__ . '/firebase-adminsdk-1gte1-f6f7bd5003.json');
	//     $firebase = new Firebase(FB_URL, $config);
	//     return $firebase;
	// }
	function getUserById($userId)
	{
	    $firebase = configureFireBase();
	    $query = new Query();

	    $query->orderByChildKey('user_id')->equalTo((int)$userId)->limitToFirst(1);
	    $nodeGetContent = $firebase->users->query($query);
	    if (count($nodeGetContent) > 0) {
	        $nodeGetContent = array_values($nodeGetContent)[0];
	        return $nodeGetContent;
	    } else {
	        return false;
	    }
	}
	public function index()
	{
		
		
	}

	
	public function isSessionAvailable() {
		if ($this->session->userdata(SITE)) {
			return true;
		}
		return false;
	}
}
