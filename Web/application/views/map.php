    <div class="container-fluid">
        <div class="page-header">
            <div class="row align-items-end">
                <div class="col-lg-8">
                    <div class="page-header-title">
                        <i class="ik ik-map bg-blue"></i>
                        <div class="d-inline">
                            <h5>Map</h5>
                            <span id="cnt"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row" style="text-align: end; display: block;">
            <button class="btn btn-primary" onclick="getLocation()">Go My Location</button>
            <p id="status_msg" style="color:red;"></p>
            <div style="width: 100%; height: 100%; padding:3px;background: black;">
                <div id="googleMap" style="width:100%;height:700px;"></div> 
            </div>
            
        </div>
    </div>
</div>

<!-- Firebase -->
<script src="https://www.gstatic.com/firebasejs/4.5.0/firebase.js"></script>
  <!-- GeoFire -->
  <!-- <script src="https://cdn.firebase.com/libs/geofire/2.0.0/geofire.min.js"></script> -->
<!-- <script src="http://maps.google.com/maps/api/js?sensor=false"></script> -->

<script type="text/javascript">
    var base_pos;
    var map;
    var markers_driver = [];

    function myMap() {
        base_pos = new google.maps.LatLng(5.3941,-3.9716);
        var mapProp= {
            center:base_pos,
            zoom:14,
            mapTypeId:google.maps.MapTypeId.ROADMAP,
            mapTypeControl:false,
            navigationControlOptions:{style:google.maps.NavigationControlStyle.SMALL}
        };
        map = new google.maps.Map(document.getElementById("googleMap"),mapProp);
        google.maps.event.addListener(map, 'click', function(event) {
            loadDrivers();
        });
        loadDrivers();
    }

    var x=document.getElementById("status_msg");
    function getLocation()
    {
        if (navigator.geolocation)
        {
            navigator.geolocation.getCurrentPosition(showPosition,showError);
        } else {
            x.innerHTML="Geolocation is not supported by this browser.";
        }
    }

    function showPosition(position)
    {
        lat=position.coords.latitude;
        lon=position.coords.longitude;
        latlon=new google.maps.LatLng(lat, lon)
        
        var marker=new google.maps.Marker({position:latlon,map:map,title:"You are here!"});
        var infowindow = new google.maps.InfoWindow({
            content: "This is current position."
        });
        google.maps.event.addListener(marker, 'click', (function () { 
            infowindow.open(map, marker);
        }));
        marker.setMap(map);
        map.setCenter(latlon);
    }

    function showError(error)
    {
        switch(error.code) 
        {
            case error.PERMISSION_DENIED:
                x.innerHTML="User denied the request for Geolocation."
                break;
            case error.POSITION_UNAVAILABLE:
                x.innerHTML="Location information is unavailable."
                break;
            case error.TIMEOUT:
                x.innerHTML="The request to get user location timed out."
                break;
            case error.UNKNOWN_ERROR:
                x.innerHTML="An unknown error occurred."
                break;
        }
    }

    var config = {
        apiKey: "AIzaSyAqUb92Xp8DBA7yXHOAPgtFnnf2PPn6gmc",
        authDomain: "divina-transport.firebaseapp.com",
        databaseURL: "https://divina-transport-default-rtdb.firebaseio.com",
        projectId: "divina-transport",
        storageBucket: "divina-transport.appspot.com",
        messagingSenderId: "71746424695",
        appId: "1:71746424695:web:edd5edd1f66c5188561b72",
        measurementId: "G-TXS3VBQT54"
    };
    firebase.initializeApp(config);
    var dbRef = firebase.database();
    var userRef = dbRef.ref('tbl_user');
    var geo_driver_Ref = dbRef.ref('tbl_geo_driver');

    function setMapOnAll(myMap) {
        for (var i = 0; i < markers_driver.length; i++) {
            markers_driver[i].setMap(myMap);
        }
    }

    function loadDrivers() {
        geo_driver_Ref.on('value', function(snapshot) {
            setMapOnAll(null);
            markers_driver = [];
            document.getElementById("cnt").innerHTML = snapshot.numChildren() + " drivers are working currently..";
            snapshot.forEach(function(childSnapshot) {
                console.log(childSnapshot);
                var childData = childSnapshot.val();
                var key = childSnapshot.key;
                var driverRef = userRef.child(key);
                driverRef.on('value', function(snapshot1) {
                    // console.log(snapshot1.val()['name']);
                    var user = snapshot1.val();
                    latlon=new google.maps.LatLng(childData['l'][0], childData['l'][1]);
                    var icon = "<?php echo base_url();?>assets/img/ic_car.png";
            
                    var marker=new google.maps.Marker({position:latlon,icon:icon,map:map,title:user['name']});
                    var infowindow = new google.maps.InfoWindow({
                        content: user['name']
                    });
                    google.maps.event.addListener(marker, 'click', (function () { 
                        infowindow.open(map, marker);
                    }));
                    markers_driver.push(marker);
                });
                // var name = childData.child("name").val();
                
            });
            setMapOnAll(map);

        });
    }
    
    
    userRef.on('child_added', function(snapshot) {
        snapshot.forEach(function(childSnapshot) {
            // var key = childSnapshot.key();
            var childData = childSnapshot.val();
            // console.log(childData);
        });
          //Do something with the data
          // console.log(snapshot.val());
    });


</script>
<!-- Google Map -->
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBpAFwkrIgnnIgZoGeW6Pz0BhH8UDLzOSU&callback=myMap"></script>
