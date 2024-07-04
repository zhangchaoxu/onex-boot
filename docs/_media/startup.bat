@echo off
echo 处理文本显示乱码
chcp 65001
echo 关闭cmd快速编辑模式
reg add HKEY_CURRENT_USER\Console /v QuickEdit /t REG_DWORD /d 00000000 /f
java -Dfile.encoding=utf-8 -jar portal-api.jar --spring.profiles.active=prod --server.port=8891 --server.servlet.context-path=/portal-api
pause
