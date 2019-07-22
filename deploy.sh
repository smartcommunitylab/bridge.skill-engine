#!/bin/bash
set +x
#ssh key
cat $key > sshkey
chmod 600 sshkey
statusCode=1
APP="bridge.skill-engine"
TSTAMP=$(date +%Y.%m.%d-%H.%M.%S)
TSSRV="$TSTAMP $APP:"
RELEASE=$(sed -E -n '/<artifactId>(skill-engine)<\/artifactId>.*/{n;p}' pom.xml | grep -Eo '[0-9]\.[0-9]')
Msg="$TSSRV Deploy in corso"
URL="https://api.telegram.org/bot${TG_TOKEN}/sendMessage"
CHAT="chat_id=${CHAT_ID}"
curl -s -X POST $URL -d $CHAT -d "text=$Msg"
ssh -i sshkey -o "StrictHostKeyChecking no" $USR@$INTIP "bash /home/dev/bridge.skill-engine/bridge.sh"
if [[ $? -eq 0 ]]; then
  statusCode=0
  Msg="$TSSRV Deploy ok"
  curl -s -X POST $URL -d $CHAT -d "text=$Msg"
fi
rm sshkey
echo $statusCode
exit $statusCode
