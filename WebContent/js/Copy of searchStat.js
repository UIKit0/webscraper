/*
 * Copyright (C) 2011 WebSquared Inc. http://websqrd.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */ 		
	// declaring variables
	var SYSTEM_DATA_SIZE = 60;
	var KEYWORD_LOG_SIZE = 100;
	var FAIL_COUNT_LIMIT = 10;
	var FAIL_COMMENT = "검색엔진 접속에 실패했습니다.";
	var SE_UPDATE_FAIL_COMM = "이벤트내역 업데이트에 실패했습니다.";
	//메모리 차트
    var memChart;
    var memData = [];
    var maxMemory;
    //cpu 차트
    var cpuChart;
    var cpuData = [];
    //load average
    var avgLoadChart;
    var avgLoadData = [];
    //search query
    var hitChart;
    var hitData = [];
    //average response time
    var responseTimeChart;
    var responseTimeData = [];
    //실패쿼리차트
    var failHitChart;
    var failHitData = [];
    
    //전체색인정보 차트
    var fullIndexChart;
    var incIndexChart;
    var indexDocChart;
    
    var jvm_cpu_support;
	var load_avg_support;
	var jvm_memory_support;
    
	var lastIndexInfoUpdateTime = 0;
	var lastPopularListUpdateTime = 0;
	var lastEventUpdateTime  = 0;
	var system_url = "/monitoring/system/detail";
    var search_url = "/monitoring/search/detail";
	var indexing_info_url = "/monitoring/indexing";
	var keyword_url = "/monitoring/keywordList";
	var popular_url = "/keyword/popular";
	var search_event_url = "/monitoring/eventList";
	
	var IS_TEST = "false"; //테스트데이터를 자동생성한다. 디버그용. true이면 테스트데이터 생성.
	
	var cpuColor = "#339900";
	var memoryColor = "#330066";
	var loadColor = "#330066";
	var hitColor = "#3366cc";
	var timeColor = "#666600";
	var failHitColor = "#ff9900";
	var deleteColor = "#C72C95";
	var updateColor = "#666600";
	var insertColor = "#3366cc";
	var docColor = "#999999";
	var logColorA = "#e3e3e3";
	var logColorB = "#cecece";
	
	
	
	var logkeywordIntervalId;
	var pollingDataIntervalId;
	var popularIntervalId;
	var indexIntervalId;
	var searchEventIntervalId;
	
	var failCount = 0;
	
	var pollingType = '';
	
    // this method called after all page contents are loaded
    window.onload = function() {
        checkAvailable();
        //처음엔 무조건 색인상태와 인기검색어를 한번 출력한다.
        $(document).ready(function(){
			showNow();
			refreshChart(typeTag);
		});                                    
    }
    
    function showNow(){
    	var dt = new Date();
		var year = dt.getFullYear();
		var mon = dt.getMonth() + 1;
		var day = dt.getDate() -1;
		var hour = dt.getHours();
		var min = dt.getMinutes();
		var dayCount = new Date(dt.getFullYear(), (dt.getMonth()+1), 0).getDate();
		var dayList = "";
		
		for (var i=1; i <= dayCount; i++) {
			dayList += "<option value="+i+" >"+i+"일</option>";
		}
		$('#timeHour').html(dayList);
		
		$('#timeMin option:eq('+hour+')').attr('selected', true);
		$('#timeHour option:eq('+day+')').attr('selected', true);

		$('#hit_selectMin option:eq('+min+')').attr('selected', true);
		$('#hit_selectHour option:eq('+hour+')').attr('selected', true);
		$('#hit_selectMin_2 option:eq('+min+')').attr('selected', true);
		$('#hit_selectHour_2 option:eq('+hour+')').attr('selected', true);
		$('#hit_selectMin_3 option:eq('+min+')').attr('selected', true);
		$('#hit_selectHour_3 option:eq('+hour+')').attr('selected', true);
		
		$('#time_selectMin option:eq('+min+')').attr('selected', true);
		$('#time_selectHour option:eq('+hour+')').attr('selected', true);
		$('#time_selectMin_2 option:eq('+min+')').attr('selected', true);
		$('#time_selectHour_2 option:eq('+hour+')').attr('selected', true);
		$('#time_selectMin_3 option:eq('+min+')').attr('selected', true);
		$('#time_selectHour_3 option:eq('+hour+')').attr('selected', true);
		
		$('#fail_selectMin option:eq('+min+')').attr('selected', true);
		$('#fail_selectHour option:eq('+hour+')').attr('selected', true);
		$('#fail_selectMin_2 option:eq('+min+')').attr('selected', true);
		$('#fail_selectHour_2 option:eq('+hour+')').attr('selected', true);
		$('#fail_selectMin_3 option:eq('+min+')').attr('selected', true);
		$('#fail_selectHour_3 option:eq('+hour+')').attr('selected', true);
    }
    
	function nowStart(count){
		var d = new Date();
  		var curr_date = d.getDate();
  		var curr_month = makeTwoDigits(d.getMonth() + 1); 
  		var curr_year = makeTwoDigits(d.getFullYear());
  		var curr_hour = makeTwoDigits(d.getHours() + count);
  		var curr_minute = makeTwoDigits(d.getMinutes());
  		var curr_second = makeTwoDigits(d.getSeconds());
  		return curr_year + "-" + curr_month + "-" + curr_date + " " + curr_hour + ":00:00";
	}
	function nowEnd(count){
		var d = new Date();
  		var curr_date = d.getDate();
  		var curr_month = makeTwoDigits(d.getMonth() + 1); 
  		var curr_year = makeTwoDigits(d.getFullYear());
  		var curr_hour = makeTwoDigits(d.getHours() + count);
  		var curr_minute = makeTwoDigits(d.getMinutes());
  		var curr_second = makeTwoDigits(d.getSeconds());
  		return curr_year + "-" + curr_month + "-" + curr_date + " " + curr_hour + ":59:59";
	}
	function makeTwoDigits(i){
		if(i < 10) return "0"+i;
		else return i;
	}

    function checkAvailable(){
  	 	createHitChart(2);
  	 	createFailHitChart(2);
  	 	createResponseTimeChart(2);
    }
    
    // method which loads external data
    function pollingData(start, end, collection, type) {
    	pollingType = type;
    	clearChart();
    	pollingSearchData(start, end, collection, type);
    }
    
    function pollingSystemData(start, end, type){
    	$.ajax({
		    url: system_url,
		    data: {test: IS_TEST, simple:"", start:start, end:end, type:type},
		    success: updateSystemData
    	});
    }
    
    function pollingSearchData(start, end, collection, type){
    	$.ajax({
		    url: search_url,
		    data: {test: IS_TEST, simple:"",start:start, end:end, collection:collection, type:type},
		    success: updateSearchData
		 });
    }
    
    function updateSystemData(data){
		if(data){
	    	$.each(
				data,
				function(i, entity) {
					var id = entity.id;
					var cpu = entity.cpu;
					var mem = entity.mem;
					var load = entity.load;
					var time = entity.time.substring(11,16);
					
					//memory
			       	memData.shift();
			      	var memObject = {index:time, used: mem, used2:mem-1000, used3:mem-2000};
			      	memData.push(memObject);
			    	
			    	//cpu
			   		cpuData.shift();
			      	var cpuObject = {index:time, cpu: cpu+30, cpu2:cpu+50, cpu3:cpu+70};
			      	cpuData.push(cpuObject);
					 
			       	avgLoadData.shift();
			      	var loadObject = {index:time, load:load+2, load2:load+3, load3:load+5};
			      	avgLoadData.push(loadObject);
				
				}
	    	);
	    	memChart.validateData();
	    	cpuChart.validateData();
	    	avgLoadChart.validateData();
		}
		
    }
    
    function updateSearchData(data){
		if(data){
	    	$.each(
				data,
				function(i, entity) {
					var id = entity.id;
					var hit = entity.hit;
					var fail = entity.fail;
					var ave = entity.ave;
					var max = entity.max;
					var time = entity.time.substring(11,16);
					
					var hitObject = {index:time, hit: hit+3, hit2:hit+5, hit3:hit+7};
					hitData.shift();
					hitData.push(hitObject);
					
					var failObject = {index:time, fail: fail+3, fail2:fail+5, fail3:fail+7};
					failHitData.shift();
					failHitData.push(failObject);
					
					var timeObject = {index:time, mean: ave+3, max: max+4, mean2:ave+5, max2:max+6, mean3:ave+7, max3:max+8};
					responseTimeData.shift();
					responseTimeData.push(timeObject);
				}
	    	);
	    	hitChart.validateData();
			failHitChart.validateData();
			responseTimeChart.validateData();
		}
		
    }
    
    // create cpu chart
    function createCPUChart(count){
    	// SERIAL CHART    
        cpuChart = new AmCharts.AmSerialChart();
        cpuChart.dataProvider = cpuData;
        cpuChart.categoryField = "index";
        cpuChart.addTitle("CPU(%)", 10);

        // category
        var categoryAxis = cpuChart.categoryAxis;
        categoryAxis.gridAlpha = 0.1;
        categoryAxis.axisColor = "#DADADA";
        categoryAxis.startOnAxis = true;
        // GRAPH
         var graph = new AmCharts.AmGraph();
        graph.valueField = "cpu";
        graph.title = "현재";
        graph.balloonText = "[[value]]%";
        graph.lineThickness  = 2;
        graph.fillAlphas = -1; 
        cpuChart.addGraph(graph);
    	 // Value
        var valueAxis = new AmCharts.ValueAxis();
        valueAxis.maximum = 100;
        cpuChart.addValueAxis(valueAxis);

        if(count == 1){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "cpu2";
	        graph.title = "비교1";
	        graph.balloonText = "[[value]]%";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        cpuChart.addGraph(graph);
        }else if(count == 2){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "cpu2";
	        graph.title = "비교1";
	        graph.balloonText = "[[value]]%";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        cpuChart.addGraph(graph);
	        var graph = new AmCharts.AmGraph();
	        graph.valueField = "cpu3";
	        graph.title = "비교2";
	        graph.balloonText = "[[value]]%";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        cpuChart.addGraph(graph);
        }

        // CURSOR
        var chartCursor = new AmCharts.ChartCursor();
        chartCursor.zoomable = false; // as the chart displayes not too many values, we disabled zooming
        chartCursor.cursorAlpha = 0.3;
        
        chartCursor.zoomable = true;
        cpuChart.addChartCursor(chartCursor);
		cpuChart.pathToImages = "/admin/js/amcharts/images/";
         // SCROLLBAR
        var chartScrollbar = new AmCharts.ChartScrollbar();
        chartScrollbar.graph = graph;
        chartScrollbar.scrollbarHeight = 40;
        chartScrollbar.color = "#FFFFFF";
        chartScrollbar.autoGridCount = true;
        cpuChart.addChartScrollbar(chartScrollbar);
        cpuChart.addListener("dataUpdated", zoomChart);
        // LEGEND
        var legend = new AmCharts.AmLegend();
        cpuChart.addLegend(legend);
		
        // WRITE
        cpuChart.write("chartJCPUDiv");
        for (var i=0; i < SYSTEM_DATA_SIZE; i++) {
      		var cpuObject = {index: "", java: 0};
	  		cpuData.push(cpuObject);
    	};
    	cpuChart.validateData();
    }
    
    function zoomChart() {
                // different zoom methods can be used - zoomToIndexes, zoomToDates, zoomToCategoryValues
                hitChart.zoomToIndexes(cpuData.length - 40, cpuData.length - 10);
    } 
      
    // create memory chart
    function createMemChart(count){
    	// SERIAL CHART
        memChart = new AmCharts.AmSerialChart();
        memChart.dataProvider = memData;
        memChart.categoryField = "index";
        memChart.addTitle("메모리(MB)", 10);
        memChart.addListener("dataUpdated", zoomChart);
        
        // Category
        var categoryAxis = memChart.categoryAxis;
        categoryAxis.gridAlpha = 0.07;
        categoryAxis.axisColor = "#DADADA";
        categoryAxis.startOnAxis = true;

        // Value
        var valueAxis = new AmCharts.ValueAxis();
        valueAxis.gridAlpha = 0.07;
        memChart.addValueAxis(valueAxis);

        // use graph
        var graph = new AmCharts.AmGraph();
        graph.valueField = "used";
        graph.title = "현재";
        graph.balloonText = "사용 [[value]]MB ([[percents]]%)";
        graph.lineThickness  = 2;
        graph.fillAlphas = -1; // setting fillAlphas to > 0 value makes it area graph 
        memChart.addGraph(graph);

        if(count == 1){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "used2";
	        graph.title = "비교1";
	        graph.balloonText = "사용 [[value]]MB ([[percents]]%)";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        memChart.addGraph(graph);
        }else if(count == 2){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "used2";
	        graph.title = "비교1";
	        graph.balloonText = "사용 [[value]]MB ([[percents]]%)";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        memChart.addGraph(graph);
	        var graph = new AmCharts.AmGraph();
	        graph.valueField = "used3";
	        graph.title = "비교2";
	        graph.balloonText = "사용 [[value]]MB ([[percents]]%)";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        memChart.addGraph(graph);
        }

        // CURSOR
        var chartCursor = new AmCharts.ChartCursor();
        chartCursor.zoomable = false; // as the chart displayes not too many values, we disabled zooming
        chartCursor.cursorAlpha = 0.3;
        
        chartCursor.zoomable = true;
        memChart.addChartCursor(chartCursor);
		memChart.pathToImages = "/admin/js/amcharts/images/";
         // SCROLLBAR
        var chartScrollbar = new AmCharts.ChartScrollbar();
        chartScrollbar.graph = graph;
        chartScrollbar.scrollbarHeight = 40;
        chartScrollbar.color = "#FFFFFF";
        chartScrollbar.autoGridCount = true;
        memChart.addChartScrollbar(chartScrollbar);
        memChart.addListener("dataUpdated", zoomChart);
        // LEGEND
        var legend = new AmCharts.AmLegend();
        memChart.addLegend(legend);
        
        
        // WRITE
        memChart.write("chartMemDiv");
        
        for (var i=0; i < SYSTEM_DATA_SIZE; i++) {
          var memObject = {index: "", used: 0};
		  memData.push(memObject);
        };
        
        memChart.validateData();
    }  
    
    // create load avg chart
    function createLoadChart(count){
    	// SERIAL CHART    
        avgLoadChart = new AmCharts.AmSerialChart();
        avgLoadChart.dataProvider = avgLoadData;
        avgLoadChart.categoryField = "index";
        if(load_avg_support){
        	avgLoadChart.addTitle("서버부하", 10);
        }else{
        	avgLoadChart.addTitle("[미지원]서버부하", 10);
        }

        // category
        var categoryAxis = avgLoadChart.categoryAxis;
        categoryAxis.gridAlpha = 0.07;
        categoryAxis.axisColor = "#DADADA";
        categoryAxis.startOnAxis = true;

        // GRAPH
        var graph = new AmCharts.AmGraph();
        graph.valueField = "load";
        graph.title = "현재";
        graph.balloonText = "[[value]]";
        graph.lineThickness  = 2;
        graph.fillAlphas = -1; 
        avgLoadChart.addGraph(graph);
        
    	 // Value
        var valueAxis = new AmCharts.ValueAxis();
        valueAxis.minimum = 0;
        valueAxis.maximum = 5;
        avgLoadChart.addValueAxis(valueAxis);

        if(count == 1){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "load2";
	        graph.title = "비교1";
	        graph.balloonText = "[[value]]";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        avgLoadChart.addGraph(graph);
        }else if(count == 2){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "load2";
	        graph.title = "비교1";
	        graph.balloonText = "[[value]]";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        avgLoadChart.addGraph(graph);
	        var graph = new AmCharts.AmGraph();
	        graph.valueField = "load3";
	        graph.title = "비교2";
	        graph.balloonText = "[[value]]";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        avgLoadChart.addGraph(graph);
        }

        // CURSOR
        var chartCursor = new AmCharts.ChartCursor();
        chartCursor.zoomable = false; // as the chart displayes not too many values, we disabled zooming
        chartCursor.cursorAlpha = 0.3;
        
        chartCursor.zoomable = true;
        avgLoadChart.addChartCursor(chartCursor);
		avgLoadChart.pathToImages = "/admin/js/amcharts/images/";
         // SCROLLBAR
        var chartScrollbar = new AmCharts.ChartScrollbar();
        chartScrollbar.graph = graph;
        chartScrollbar.scrollbarHeight = 40;
        chartScrollbar.color = "#FFFFFF";
        chartScrollbar.autoGridCount = true;
        avgLoadChart.addChartScrollbar(chartScrollbar);
        avgLoadChart.addListener("dataUpdated", zoomChart);
        // LEGEND
        var legend = new AmCharts.AmLegend();
        avgLoadChart.addLegend(legend);

        // WRITE
        avgLoadChart.write("chartLoadDiv");
    	for (var i=0; i < SYSTEM_DATA_SIZE; i++) {
  			var loadObject = {index: "", load: 0};
  			avgLoadData.push(loadObject);
		}
	
		avgLoadChart.validateData();
    } 
    
    // create hit chart
    function createHitChart(count){
    	// SERIAL CHART    
        hitChart = new AmCharts.AmSerialChart();
        hitChart.dataProvider = hitData;

        hitChart.categoryField = "index";
        hitChart.addTitle("검색처리수(개)", 10);

        // category
        var categoryAxis = hitChart.categoryAxis;
        categoryAxis.gridAlpha = 0.07;
        categoryAxis.axisColor = "#DADADA";
        categoryAxis.startOnAxis = true;

		 // GRAPH
        var graph = new AmCharts.AmGraph();
        graph.type = "step";
        graph.valueField = "hit";
        graph.title = "현재";
        graph.balloonText = "[[value]]개";
        graph.lineThickness  = 2;
        graph.fillAlphas = -1; 
        hitChart.addGraph(graph);
        
    	 // Value
        var valueAxis = new AmCharts.ValueAxis();
        valueAxis.integersOnly = true;
        hitChart.addValueAxis(valueAxis);

        if(count == 1){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "hit2";
	        graph.title = "비교1";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        hitChart.addGraph(graph);
        }else if(count == 2){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "hit2";
	        graph.title = "비교1";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        hitChart.addGraph(graph);
	        var graph = new AmCharts.AmGraph();
	        graph.valueField = "hit3";
	        graph.title = "비교2";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        hitChart.addGraph(graph);
        }

        // CURSOR
        var chartCursor = new AmCharts.ChartCursor();
        chartCursor.zoomable = false; // as the chart displayes not too many values, we disabled zooming
        chartCursor.cursorAlpha = 0.3;
        
        chartCursor.zoomable = true;
        hitChart.addChartCursor(chartCursor);
		hitChart.pathToImages = "/admin/js/amcharts/images/";
         // SCROLLBAR
        var chartScrollbar = new AmCharts.ChartScrollbar();
        chartScrollbar.graph = graph;
        chartScrollbar.scrollbarHeight = 40;
        chartScrollbar.color = "#FFFFFF";
        chartScrollbar.autoGridCount = true;
        hitChart.addChartScrollbar(chartScrollbar);
        hitChart.addListener("dataUpdated", zoomChart);
        // LEGEND
        var legend = new AmCharts.AmLegend();
        hitChart.addLegend(legend);

        // WRITE
        hitChart.write("chartSearchActDiv");
        for (var i=0; i < SYSTEM_DATA_SIZE; i++) {
      		var hitObject = {index: "", hit: 0, fail: 0};
	  		hitData.push(hitObject);
    	};
    	
    	hitChart.validateData();
    }  
    
    // 평균 응답시간
    function createResponseTimeChart(count){
    	// SERIAL CHART    
        responseTimeChart = new AmCharts.AmSerialChart();
        responseTimeChart.dataProvider = responseTimeData;
        responseTimeChart.categoryField = "index";
        responseTimeChart.addTitle("검색응답시간(ms)", 10);

        // category
        var categoryAxis = responseTimeChart.categoryAxis;
        categoryAxis.gridAlpha = 0.07;
        categoryAxis.axisColor = "#DADADA";
        categoryAxis.startOnAxis = true;
        
        // 최대응답시간 GRAPH
        var graph = new AmCharts.AmGraph();
        graph.type = "step";
        graph.valueField = "max";
        graph.title = "현재1";
        graph.balloonText = "최대 [[value]]ms";
        graph.lineThickness  = 2;
        graph.fillAlphas = -1; 
        responseTimeChart.addGraph(graph);
        
        // 평균응답시간 GRAPH
        var graph = new AmCharts.AmGraph();
        graph.type = "step";
        graph.valueField = "mean";
        graph.title = "현재1";
        graph.balloonText = "평균 [[value]]ms";
        graph.lineThickness  = 2;
        graph.fillAlphas = -1; 
        responseTimeChart.addGraph(graph);
        
    	// Value
        var valueAxis = new AmCharts.ValueAxis();
        valueAxis.integersOnly = true;
        responseTimeChart.addValueAxis(valueAxis);

        if(count == 1){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "max2";
	        graph.title = "비교11";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        responseTimeChart.addGraph(graph);
	        var graph = new AmCharts.AmGraph();
	        graph.valueField = "mean2";
	        graph.title = "비교12";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        responseTimeChart.addGraph(graph);
        }else if(count == 2){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "max2";
	        graph.title = "비교11";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        responseTimeChart.addGraph(graph);
	        var graph = new AmCharts.AmGraph();
	        graph.valueField = "mean2";
	        graph.title = "비교12";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        responseTimeChart.addGraph(graph);
	         
	        var graph = new AmCharts.AmGraph();
	        graph.valueField = "max3";
	        graph.title = "비교21";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        responseTimeChart.addGraph(graph);
	        var graph = new AmCharts.AmGraph();
	        graph.valueField = "mean3";
	        graph.title = "비교22";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        responseTimeChart.addGraph(graph);
        }

        // CURSOR
        var chartCursor = new AmCharts.ChartCursor();
        chartCursor.zoomable = false; // as the chart displayes not too many values, we disabled zooming
        chartCursor.cursorAlpha = 0.3;
        
        chartCursor.zoomable = true;
        responseTimeChart.addChartCursor(chartCursor);
		responseTimeChart.pathToImages = "/admin/js/amcharts/images/";
         // SCROLLBAR
        var chartScrollbar = new AmCharts.ChartScrollbar();
        chartScrollbar.graph = graph;
        chartScrollbar.scrollbarHeight = 40;
        chartScrollbar.color = "#FFFFFF";
        chartScrollbar.autoGridCount = true;
        responseTimeChart.addChartScrollbar(chartScrollbar);
        responseTimeChart.addListener("dataUpdated", zoomChart);
        // LEGEND
        var legend = new AmCharts.AmLegend();
        responseTimeChart.addLegend(legend);

        // WRITE
        responseTimeChart.write("chartSearchTimeDiv");
        for (var i=0; i < SYSTEM_DATA_SIZE; i++) {
      		var timeObject = {index: "", mean: 0};
	  		responseTimeData.push(timeObject);
    	};
    	
    	responseTimeChart.validateData();
    }  
    
    // 실패쿼리
    function createFailHitChart(count){
    	// SERIAL CHART    
        failHitChart = new AmCharts.AmSerialChart();
        failHitChart.dataProvider = failHitData;
        failHitChart.categoryField = "index";
        failHitChart.addTitle("검색실패수(개)", 10);

        // category
        var categoryAxis = failHitChart.categoryAxis;
        categoryAxis.gridAlpha = 0.07;
        categoryAxis.axisColor = "#DADADA";
        categoryAxis.startOnAxis = true;
 
        // GRAPH
        var graph = new AmCharts.AmGraph();
        graph.type = "step";
        graph.valueField = "fail";
        graph.title = "현재";
        graph.balloonText = "[[value]]개";
        graph.lineThickness  = 2;
        graph.fillAlphas = -1; 
        failHitChart.addGraph(graph);
        
    	 // Value
        var valueAxis = new AmCharts.ValueAxis();
        valueAxis.integersOnly = true;
        failHitChart.addValueAxis(valueAxis);

        if(count == 1){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "fail2";
	        graph.title = "비교1";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        failHitChart.addGraph(graph);
        }else if(count == 2){
        	var graph = new AmCharts.AmGraph();
	        graph.valueField = "fail2";
	        graph.title = "비교1";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        failHitChart.addGraph(graph);
	        var graph = new AmCharts.AmGraph();
	        graph.valueField = "fail3";
	        graph.title = "비교2";
	        graph.balloonText = "[[value]]개";
	        graph.lineThickness  = 2;
	        graph.fillAlphas = -1; 
	        failHitChart.addGraph(graph);
        }

        // CURSOR
        var chartCursor = new AmCharts.ChartCursor();
        chartCursor.zoomable = false; // as the chart displayes not too many values, we disabled zooming
        chartCursor.cursorAlpha = 0.3;
        
        chartCursor.zoomable = true;
        failHitChart.addChartCursor(chartCursor);
		failHitChart.pathToImages = "/admin/js/amcharts/images/";
         // SCROLLBAR
        var chartScrollbar = new AmCharts.ChartScrollbar();
        chartScrollbar.graph = graph;
        chartScrollbar.scrollbarHeight = 40;
        chartScrollbar.color = "#FFFFFF";
        chartScrollbar.autoGridCount = true;
        failHitChart.addChartScrollbar(chartScrollbar);
        failHitChart.addListener("dataUpdated", zoomChart);
        // LEGEND
        var legend = new AmCharts.AmLegend();
        failHitChart.addLegend(legend);

        // WRITE
        failHitChart.write("chartSearchFailDiv");
    	for (var i=0; i < SYSTEM_DATA_SIZE; i++) {
      		var failObject = {index: "", fail: 0};
	  		failHitData.push(failObject);
    	};
    	failHitChart.validateData();
    } 
    

	function refreshChart(type){
		var selectVal;
		if("minute" == type){
			selectVal = $("#timeMin").find("option:selected").val();
		}else{
			selectVal = $("#timeHour").find("option:selected").val();
		}
		var selectCollection = $("#collection").find("option:selected").val();
		if(selectVal.length == 1){
			selectVal = "0" + selectVal;
		}
		var dt = new Date();
		var year = dt.getFullYear();
		var mon = dt.getMonth() + 1;
		var day = dt.getDate();
		if(mon < 10){
			mon = "0" + mon;
		}
		if(day < 10){
			day = "0" + mon;
		}
		if("minute" == type){
			pollingData(year+'-'+mon+'-'+day+' '+selectVal+':00:00',year+'-'+mon+'-'+day+' '+selectVal+':59:59', selectCollection, 'minute');
		}else{
			pollingData(year+'-'+mon+'-'+selectVal+' 00:00:00',year+'-'+mon+'-'+selectVal+' 23:59:59', selectCollection, 'hour');
		}
	}
	
	
	function clearChart(){
		if(pollingType == "hour"){
			responseTimeData = [];
			hitData = [];
			failHitData = [];
			cpuData = [];
			memData = [];
			avgLoadData = [];
			
			 for (var i=0; i < 24; i++) {
	      		var timeObject = {index: "", mean: 0};
		  		responseTimeData.push(timeObject);
		  		var hitObject = {index: "", hit: 0};
		  		hitData.push(hitObject);
		  		var failObject = {index: "", fail: 0};
		  		failHitData.push(failObject);
		  		var cpuObject = {index: "", java: 0};
		  		cpuData.push(cpuObject);
		  		var memObject = {index: "", used: 0};
			   	memData.push(memObject);
			   	var loadObject = {index: "", load: 0};
		  		avgLoadData.push(loadObject);
    		}
    		responseTimeChart.dataProvider = responseTimeData;
			failHitChart.dataProvider = failHitData;
			hitChart.dataProvider = hitData;
		}else{
			for (var i=0; i < SYSTEM_DATA_SIZE; i++) {
	      		var timeObject = {index: "", mean: 0};
	      		responseTimeData.shift();
		  		responseTimeData.push(timeObject);
		  		var hitObject = {index: "", hit: 0};
	      		hitData.shift();
		  		hitData.push(hitObject);
		  		var failObject = {index: "", fail: 0};
	      		failHitData.shift();
		  		failHitData.push(failObject);
		  		var cpuObject = {index: "", java: 0};
		  		cpuData.shift();
		  		cpuData.push(cpuObject);
		  		var memObject = {index: "", used: 0};
		  		memData.shift();
			   	memData.push(memObject);
			   	var loadObject = {index: "", load: 0};
			   	avgLoadData.shift();
		  		avgLoadData.push(loadObject);
    		}
		}
		
    	
    	responseTimeChart.validateData();
    	failHitChart.validateData();
    	hitChart.validateData();
	}
	
	function addHitDiv(){
		if($("#hit_selector_2").css("display") == "none"){
			$("#hit_selector_2").css("display","block");
		}else if($("#hit_selector_3").css("display") == "none"){
			$("#hit_selector_3").css("display","block");
		}
		
	}
	
	function addTimeDiv(){
		if($("#time_selector_2").css("display") == "none"){
			$("#time_selector_2").css("display","block");
		}else if($("#time_selector_3").css("display") == "none"){
			$("#time_selector_3").css("display","block");
		}
		
	}
	
	function addFailDiv(){
		if($("#fail_selector_2").css("display") == "none"){
			$("#fail_selector_2").css("display","block");
		}else if($("#fail_selector_3").css("display") == "none"){
			$("#fail_selector_3").css("display","block");
		}
		
	}
