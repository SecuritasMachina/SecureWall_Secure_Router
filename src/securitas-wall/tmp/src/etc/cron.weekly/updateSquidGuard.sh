echo "Download Black & White lists"
mkdir -p /var/lib/squid/bumpdomains.lst
wget -O /tmp/mesd_blacklists.tgz http://squidguard.mesd.k12.or.us/blacklists.tgz
wget -O /tmp/securitasmachina.tgz https://github.com/SecuritasMachina/SecuritasMachina-Distrib/raw/master/distrib/blacklists/securitasmachina.tgz
wget -O /var/lib/squid/bumpdomains.lst https://github.com/SecuritasMachina/SecuritasMachina-Distrib/raw/master/distrib/whitelists/bumpdomains.lst

echo "Extracting"
tar -xzf /tmp/mesd_blacklists.tgz -C /var/lib/squidguard/db
tar -xzf /tmp/securitasmachina.tgz -C /var/lib/squidguard/db/blacklists

mkdir -p /var/lib/squidguard/db/blacklists/trackers
mkdir -p /var/lib/squidguard/db/blacklists/trackers2

wget -O /var/lib/squidguard/db/blacklists/trackers/urls https://raw.githubusercontent.com/SecuritasMachina/SecuritasMachina-Distrib/master/sources/blackLists/squidguard/trackers/urls
wget -O /var/lib/squidguard/db/blacklists/trackers/domains https://raw.githubusercontent.com/SecuritasMachina/SecuritasMachina-Distrib/master/sources/blackLists/squidguard/trackers/domains
wget -O /var/lib/squidguard/db/blacklists/trackers/expressions https://raw.githubusercontent.com/SecuritasMachina/SecuritasMachina-Distrib/master/sources/blackLists/squidguard/trackers/expressions
wget -o /var/lib/squidguard/db/blacklists/trackers2/urls https://raw.githubusercontent.com/olbat/ut1-blacklists/master/blacklists/publicite/urls
wget -o /var/lib/squidguard/db/blacklists/trackers2/domains https://raw.githubusercontent.com/olbat/ut1-blacklists/master/blacklists/publicite/domains
wget -o /var/lib/squidguard/db/blacklists/trackers2/expressions https://raw.githubusercontent.com/olbat/ut1-blacklists/master/blacklists/publicite/expressions 

chown -R proxy:proxy /var/lib/squidguard/db/*

rm -f /tmp/mesd_blacklists.tgz
rm -f /tmp/securitasmachina.tgz

echo "Reload squidGuard"
sudo -u proxy squidGuard -C all

echo "Reload squid"

squid -k reconfigure

