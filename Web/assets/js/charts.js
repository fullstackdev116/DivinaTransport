(function($) {
  'use strict';
  // 
  var c3DonutChart = c3.generate({
    bindto: '#c3-donut-chart',
    data: {
      columns: [
        ["EdiaPay", 0],
        ["Cache", 0],
      ],
      type: 'donut',
      onclick: function(d, i) {
        console.log("onclick", d, i);
      },
      onmouseover: function(d, i) {
        console.log("onmouseover", d, i);
      },
      onmouseout: function(d, i) {
        console.log("onmouseout", d, i);
      }
    },
    color: {
      pattern: ['rgba(237,28,36,0.6)', 'rgba(88,216,163,1)']
    },
    padding: {
      top: 0,
      right: 0,
      bottom: 30,
      left: 0,
    },
    donut: {
      title: "20,000 XOF"
    }
  });

  setTimeout(function() {
    c3DonutChart.load({
      columns: [
        ["EdiaPay", 1800, 2400, 2000],
        ["Cache", 1500, 1100, 2000],
      ]
    });
  }, 1500);

  // setTimeout(function() {
  //   c3DonutChart.unload({
  //     ids: 'data1'
  //   });
  //   c3DonutChart.unload({
  //     ids: 'data2'
  //   });
  // }, 2500);


})(jQuery);
