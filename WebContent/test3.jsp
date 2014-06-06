<%@ page contentType="text/html; charset=UTF-8"%>
<html debug="true">

<head>
<script src="js/jquery-1.4.4.min.js"></script>
<script src="js/jquery-ui-1.8.9.min.js"></script>
<script src="js/jquery.cookie.js"></script>
<script type="text/javascript" src="js/tree.jquery.js"></script>
<link type="text/css" rel="stylesheet" href="css/jqtree.css"/>

</head>


<body>
<div id="tree1">트리입니다. </div>

<Script>
var data = [
            {
                label: 'node1',
                children: [
                    { label: 'child1' },
                    { label: 'child2' }
                ]
            },
            {
                label: 'node2',
                children: [
                    { label: 'child3' }
                ]
            }
        ];
        $('#tree1').tree({
        	data: data
        	,saveState: true
        	,autoOpen: true	
        });
        
        $('#tree1').bind(
       	    'tree.click',
       	    function(event) {
       	        // The clicked node is 'event.node'
       	        var node = event.node;
       	        alert(node.name);
       	    }
       	);
</Script>

</body>

</html>