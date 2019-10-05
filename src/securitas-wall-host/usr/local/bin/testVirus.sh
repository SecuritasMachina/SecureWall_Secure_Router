export proxyServer=172.17.0.2

echo "Test http://$proxyServer:3128 http://ackdev.com/"
if curl -s --proxy http://$proxyServer:3128 http://ackdev.com/ | grep "Welcome.action"
then
echo "Success http://ackdev.com/ !"
else
echo "Fail !"
fi

echo "Test http://$proxyServer:3128 https://ackdev.com/"
if curl -s --proxy http://$proxyServer:3128 https://ackdev.com/ | grep "Welcome.action"
then
echo "Success https://ackdev.com/ !"
else
echo "Fail !"
fi

echo "Test http://$proxyServer:8080 https://ackdev.com/"
if curl -k -s --proxy http://$proxyServer:8080 https://ackdev.com/ | grep "Welcome.action"
then
echo "Success! - http://$proxyServer:8080 https://ackdev.com/"
else
echo "Fail! - http://$proxyServer:8080 https://ackdev.com/"
fi

echo "Test http://$proxyServer:3128 http://www.eicar.org/download/eicar.com"
if curl -s --proxy http://$proxyServer:3128 http://www.eicar.org/download/eicar.com | grep "EICAR-STANDARD-ANTIV
IRUS-TEST-FILE"
then
echo "Expected: Fail! http://$proxyServer:3128 http://www.eicar.org/download/eicar.com"
else
echo "Success - Blocked virus!"
fi

echo "http://$proxyServer:3128 https://secure.eicar.org/eicar.com"
if curl -s -k --proxy http://$proxyServer:3128 https://secure.eicar.org/eicar.com | grep "EICAR-STANDARD-ANTIVI"
then
echo "Failure Expected, not using ssl port - http://$proxyServer:3128 https://secure.eicar.org/eicar.com"
else
echo "Success - Blocked virus!"
fi

echo "http://$proxyServer:8080 https://secure.eicar.org/eicar.com"
if curl -s -k --proxy http://$proxyServer:8080 https://secure.eicar.org/eicar.com | grep "EICAR-STANDARD-ANTIVI"
then
echo "Fail! - http://$proxyServer:8080 https://secure.eicar.org/eicar.com"
else
echo "Success - Blocked virus!"
fi

#https://secure.eicar.org/eicar_com.zip
#https://secure.eicar.org/eicarcom2.zip
echo "http://$proxyServer:8080 https://secure.eicar.org/eicar_com.zip"
if curl -s -k --proxy http://$proxyServer:8080 https://secure.eicar.org/eicar_com.zip | grep "ANTIVI"
then
echo "Fail! - http://$proxyServer:8080 https://secure.eicar.org/eicar.com"
else
echo "Success - Blocked virus!"
fi

echo "http://$proxyServer:8080 https://secure.eicar.org/eicarcom2.zip"
if curl -s -k --proxy http://$proxyServer:8080 https://secure.eicar.org/eicarcom2.zip | grep "ANTIVI"
then
echo "Fail! - http://$proxyServer:8080 https://secure.eicar.org/eicar.com"
else
echo "Success - Blocked virus!"
fi

