#!/bin/bash

proxy="http://localhost:13081"
http_proxy=$proxy
httpWebSite="http://securitasMachina.com"
httpsWebSite="https://securitasMachina.com"
results=""
testVirus(){
    if wget -qO- $1 | grep -i "Blocked"
        then
                echo "Success $2"
        else
                echo "Fail $2"
        fi
}
declare -a StringArray=("$httpWebSite" "$httpsWebSite")
# Read the array values with space
for val in "${StringArray[@]}"; do
	testVirus $val $val &
done
declare -a StringArray=("http://www.eicar.org/download/eicar.com" \
"https://secure.eicar.org/eicar.com" \
"https://secure.eicar.org/eicar_com.zip" \
"https://secure.eicar.org/eicarcom2.zip" \
"http://wildfire.paloaltonetworks.com/publicapi/test/pe" \
"https://wildfire.paloaltonetworks.com/publicapi/test/pe")
for val in "${StringArray[@]}"; do
	testVirus $val $val &
done
while read NAME
do
    value1="http://securitasmachina.com/sampleViruses/Malz/$NAME"
	testVirus $value1 $NAME &
done < /usr/local/bin/virusNames.lst
wait
