set -x

proxy="http://localhost:3128"
httpWebSite="http://securitasMachina.com"
httpsWebSite="https://securitasMachina.com"

# Declare a string array with type
declare -a StringArray=("$httpWebSite" \
"$httpsWebSite")
 
# Read the array values with space
for val in "${StringArray[@]}"; do
	if curl -s -k --proxy $proxy $val | grep -i "ProxyTest1979"
	then
		step= "Success $val"
	else
		step= "Fail $val !"
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
	if curl -s -k --proxy $proxy $val | grep -i "block"
	then
		step= "Success - Blocked virus!"
	else
		step= "Fail!"
	fi
done

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




