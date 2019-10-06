# SecureWall Secure Router

![alt text](https://github.com/adam-p/markdown-here/raw/master/src/common/images/icon48.png "Logo Title Text 1")


## Debian &amp; Raspberry Raspbian Secure Router (Bridge with Transparent Proxy)

Minimum System Requirements:
- 2GB Memory
- 16GB Disk Space
- Bionic or Buster

### Privacy Protection
Ad blocking or ad filtering to remove online advertising in your web browser.

### Parental Web Filtering
Web filtering to prevent engagement with websites and social networking blogs.

### Web Protection
Leveraging the vast, continuously-updated database of known malicious web sites, Secure Wall ensures you never connect to compromised, dangerous, or otherwise blacklisted sites.
ClamAV AntiVirus & Malware Filtering



# Installation
## Dependencies

 sudo apt-get install \
    apt-transport-https \
    ca-certificates \
    curl \
    gnupg2 \
    software-properties-common
    

## Install gpg key
curl -fsSL https://www.securitasmachina.com/gpg | sudo apt-key add -
## Raspbian
sudo add-apt-repository \
   "deb [arch=armhf] https://updates.securitasmachina.com/repos/apt/raspbian \
   $(lsb_release -cs) \
   stable"
## Debian
sudo add-apt-repository \
   "deb [arch=amd64] https://updates.securitasmachina.com/repos/apt/debian \
   $(lsb_release -cs) \
   stable"
## Ubuntu
sudo add-apt-repository \
   "deb [arch=amd64] https://updates.securitasmachina.com/repos/apt/ubuntu \
   $(lsb_release -cs) \
   stable"
