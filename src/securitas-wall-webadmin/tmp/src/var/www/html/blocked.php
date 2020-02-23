<?php
header('HTTP/1.0 403 Forbidden');
?>
<html>
<head><title>Blocked</title>
</head>
<body>
<?php
    echo 'Blocked URL: ' . $_GET['url'] . '<br/>';
    echo 'Client: ' . $_GET['clientaddr'] . '<br/>';
    echo 'Rule: ' . $_GET['destinationgroup'] . '<br/>';
?>
</body>
</html>