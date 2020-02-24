set -x

proxy="http://localhost:3128"
httpWebSite="http://securitasMachina.com"
httpsWebSite="https://securitasMachina.com"

if curl -s --proxy $proxy $httpWebSite | grep "ProxyTest1979"
then
	step= "Success $httpWebSite !"
else
	step= "Fail !"
fi

if curl -s --proxy $proxy $httpsWebSite | grep "ProxyTest1979"
then
	step= "Success $httpsWebSite !"
else
	step= "Fail !"
fi

if curl -k -s --proxy $proxy $httpsWebSite | grep "ProxyTest1979"
then
	step= "Success! - $proxy $httpsWebSite"
else
	step= "Fail! - $proxy $httpsWebSite"
fi

if curl -s --proxy $proxy http://www.eicar.org/download/eicar.com | grep "EICAR-STANDARD-ANTIVIRUS-TEST-FILE"
then
	step= "Expected: Fail! $proxy http://www.eicar.org/download/eicar.com"
else
	step= "Success - Blocked virus!"
fi

if curl -s -k --proxy $proxy https://secure.eicar.org/eicar.com | grep "EICAR-STANDARD-ANTIVI"
then
	step= "Failure Expected, not using ssl port - $proxy https://secure.eicar.org/eicar.com"
else
	step= "Success - Blocked virus!"
fi

if curl -s -k --proxy $proxy https://secure.eicar.org/eicar.com | grep "EICAR-STANDARD-ANTIVI"
then
	step= "Fail! - $proxy https://secure.eicar.org/eicar.com"
else
	step= "Success - Blocked virus!"
fi

if curl -s -k --proxy $proxy https://secure.eicar.org/eicar_com.zip | grep "ANTIVI"
then
	step= "Fail! - $proxy https://secure.eicar.org/eicar.com"
else
	step= "Success - Blocked virus!"
fi

if curl -s -k --proxy $proxy https://secure.eicar.org/eicarcom2.zip | grep "ANTIVI"
then
	step= "Fail! - $proxy https://secure.eicar.org/eicar.com"
else
	step= "Success - Blocked virus!"
fi

if curl -s -k --proxy $proxy http://wildfire.paloaltonetworks.com/publicapi/test/pe | grep -i "block"
then
	step= "Success - Blocked virus!"
else
	step= "Fail!"
fi

step= "Testing Virus Malware groups"
while read NAME
do
    step "Testing $NAME"
    value = "http://securitasmachina.com/sampleViruses/Malz/$NAME"
    if curl -s -k --proxy $proxy $value | grep -i "block"
	then
		step= "Success - Blocked virus @ $value"
	else
		step= "Fail! @ $value"
	fi
    
done < virusNames.lst

#https://wildfire.paloaltonetworks.com/publicapi/test/pe




