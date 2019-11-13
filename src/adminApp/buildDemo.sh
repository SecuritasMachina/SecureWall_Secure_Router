appName=dataDictionary
cwd=$(pwd)
buildDate=$(date '+%A %b %_e, %Y')
fullBuildDate=$(date)
echo Rsyncing $appName Demo
cd client
npm install
if [ $? -ne 0 ]; then
    exit 1 # terminate and indicate error
fi

cd $cwd
#rm -rf /tmp/build/$appName/client
mkdir -p /tmp/build/$appName/client
rsync -a $cwd/client /tmp/build/$appName --delete --quiet
cd /tmp/build/$appName/client/src
echo Search and replace $fullBuildDate
sed -i'' -e 's/<base href="\/">/<base href="\/dataDictionary\/">/g' index.html
sed -i'' -e "s/\[buildDate\]/$buildDate/g" index.html
sed -i'' -e "s/\[fullBuildDate\]/$fullBuildDate/g" index.html

#sed -i'' -e 's/Welcome to Jump - your private, secure URL Shortener!/Welcome to the Public Demo URL Shortener - data refreshed hourly/g' app/shared/url-edit/url-edit.component.html
#sed -i'' -e 's/[hidden]="true"/[hidden]="false"/g' app/shared/url-edit/url-edit.component.html
echo Build Client JS
cd /tmp/build/$appName/client
mkdir -p $cwd/src/main/resources/static
ng build --prod --output-path $cwd/src/main/resources/static --deploy-url=/dataDictionary/
if [ $? -eq 0 ]; then
    echo ng build success
else
    exit 1 # terminate and indicate error
fi

cd $cwd/src/main/resources/static
sed -i'' -e 's/C:\/Program Files\/Git//g' index.html

cd $cwd
#./gradlew clean
#./gradlew -DORG_GRADLE_PROJECT_TOMCAT_DEST=$ORG_GRADLE_PROJECT_TOMCAT_DEST test copyForDemo bootWar deployDataDictionary
# cp $cwd/build/libs/
#rsync -av -e "ssh -i $ssh_key_path" $ORG_GRADLE_PROJECT_TOMCAT_DEST/dataDictionary.war jaxtrx@ackdev.com:/tmp/4/webapps &
#rsync -avzz -e "ssh -i ~/Documents/ssh/id_rsa" jaxtrx@ackdev.com:/home/jaxtrx/*.sql /c/data
