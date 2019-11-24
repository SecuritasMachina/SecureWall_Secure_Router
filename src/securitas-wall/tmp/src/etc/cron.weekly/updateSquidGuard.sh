echo "Download Black & White lists"
wget -O /tmp/mesd_blacklists.tgz http://squidguard.mesd.k12.or.us/blacklists.tgz
wget -O /tmp/securitasmachina.tgz https://github.com/SecuritasMachina/SecuritasMachina-Distrib/raw/master/distrib/blacklists/securitasmachina.tgz
wget -O /var/lib/squid/bumpdomains.lst https://github.com/SecuritasMachina/SecuritasMachina-Distrib/raw/master/distrib/whitelists/bumpdomains.lst

echo "Extracting"
tar -xzf /tmp/mesd_blacklists.tgz -C /var/lib/squidguard/db
tar -xzf /tmp/securitasmachina.tgz -C /var/lib/squidguard/db/blacklists
wget -O /var/lib/squidguard/db/blacklists/trackers/urls https://raw.githubusercontent.com/SecuritasMachina/SecuritasMachina-Distrib/master/sources/blackLists/squidguard/trackers/urls
wget -O /var/lib/squidguard/db/blacklists/trackers/domains https://raw.githubusercontent.com/SecuritasMachina/SecuritasMachina-Distrib/master/sources/blackLists/squidguard/trackers/domains


chown -R proxy:proxy /var/lib/squidguard/db/*

rm -f /tmp/mesd_blacklists.tgz
rm -f /tmp/securitasmachina.tgz

echo "Reload squidGuard"
sudo -u proxy squidGuard -C all

echo "Reload squid"

squid -k reconfigure

