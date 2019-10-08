

docker run -p 5901:5901 -p 6901:6901 -p 8080:8080 -p 80:80 --cap-add=NET_ADMIN -it -e VNC_RESOLUTION=1800x900 -v /home/shared:/home/hostVolume SecuritasMachina/docker-headless-securitas-wall:latest --restart always

#docker run --cap-add=NET_ADMIN -it -e VNC_RESOLUTION=1800x900 -v /home/docker:/home/hostVolume ackdev/secure_proxy_securitas-wall:latest

