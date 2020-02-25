#set -x

proxy="http://localhost:13081"
http_proxy=$proxy
httpWebSite="http://securitasMachina.com"
httpsWebSite="https://securitasMachina.com"
results=""
step1="none"
# Declare a string array with type
declare -a StringArray=("$httpWebSite" \
"$httpsWebSite")
 
# Read the array values with space
for val in "${StringArray[@]}"; do
	if curl -s -k --proxy $proxy $val | grep -i "ProxyTest1979"
	then
		results="$results Success $val\n"
	else
		results="$results Fail $val\n"
	fi
done


# Declare a string array with type
declare -a StringArray=("http://www.eicar.org/download/eicar.com" \
"https://secure.eicar.org/eicar.com" \
"https://secure.eicar.org/eicar_com.zip" \
"https://secure.eicar.org/eicarcom2.zip" \
"http://wildfire.paloaltonetworks.com/publicapi/test/pe" \
"https://wildfire.paloaltonetworks.com/publicapi/test/pe")
 
# Read the array values with space
for val in "${StringArray[@]}"; do
	if wget -qO- $val | grep -i "Blocked"
	then
		results="$results Success $val\n"
	else
		results="$results Fail $val\n"
	fi
done

echo "Testing Virus Malware groups"
while read NAME
do
    value1="http://securitasmachina.com/sampleViruses/Malz/$NAME"

    if wget -qO- $value1 | grep -i "Blocked"
	then
		results="$results Success $NAME\n"
	else
		results="$results Fail $NAME\n"
	fi
done < /usr/local/bin/virusNames.lst

echo -e $results
