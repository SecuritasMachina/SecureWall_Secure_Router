echo "Download Black & White lists"
wget -q -O /tmp/mesd_blacklists.tgz http://squidguard.mesd.k12.or.us/blacklists.tgz
wget -q -O /tmp/securitasmachina.tgz https://github.com/SecuritasMachina/SecuritasMachina-Distrib/raw/master/distrib/blacklists/securitasmachina.tgz
echo "Extracting"
tar -xzf /tmp/mesd_blacklists.tgz -C /var/lib/squidguard/db
tar -xzf /tmp/securitasmachina.tgz -C /var/lib/squidguard/db/blacklists

chown -R proxy:proxy /var/lib/squidguard/db/*

rm -f /tmp/mesd_blacklists.tgz
rm -f /tmp/securitasmachina.tgz

echo "Reload squidGuard"
sudo -u proxy squidGuard -C all

echo "Reload squid"

squid -k reconfigure

