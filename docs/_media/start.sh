process=`ps -fe|grep "api.jar" |grep -ivE "grep|cron" |awk '{print $2}'`
if [ !$process ];
then
	echo "stop rest process $process ....."
	kill -9 $process
	sleep 1
fi

echo "start rest process....."
nohup java -Dspring.profiles.active=common,onex,prod -jar api.jar 2>&1 | cronolog ./log/%Y-%m-%d_%H.out >> /dev/null &
echo "start rest success!"
