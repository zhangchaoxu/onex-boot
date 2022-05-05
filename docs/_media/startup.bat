@echo off
chcp 65001
java -Dfile.encoding=utf-8 -jar portal-api.jar --spring.profiles.active=prod --server.port=8891 --server.servlet.context-path=/portal-api
pause
