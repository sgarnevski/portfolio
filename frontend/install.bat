@echo off
set "PATH=C:\Program Files\nodejs;%PATH%"
cd /d D:\src\portfolio-rebalancer\frontend
if exist node_modules rmdir /s /q node_modules
call "C:\Program Files\nodejs\npm.cmd" install
