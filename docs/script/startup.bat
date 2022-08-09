@echo off
chcp 65001
C:\openjdk\jdk-11\bin\java.exe -Dfile.encoding=utf-8 -jar portal-api.jar --spring.profiles.active=common,onex,prod --server.port=8890 --server.servlet.context-path=/portal-api
pause
