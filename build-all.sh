#############################################
### Launch a virtual framebuffer X server ###
#############################################
export DISPLAY=":98"
Xvfb $DISPLAY >& Xvfb.log &
trap "kill $! || true" EXIT
sleep 10

mvn clean install