# GEO地理坐标相关

1. 搜索周边范围
ROUND(6378.138*2*ASIN(SQRT(POW(SIN((${lat}*PI()/180-lat*PI()/180)/2),2)+COS(${lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((${lng}*PI()/180-lng*PI()/180)/2),2)))*1000) as distance