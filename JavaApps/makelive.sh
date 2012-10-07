#!/bin/bash

echo "cp ./AuksalaqOperaClient/*.class /var/www/AuksalaqNOMADS/OperaClient"
cp ./AuksalaqOperaClient/*.class /var/www/AuksalaqNOMADS/AudienceClient
echo "cp ./AuksalaqOperaClient/*.jar /var/www/AuksalaqNOMADS/AudienceClient"
cp ./AuksalaqOperaClient/*.jar /var/www/AuksalaqNOMADS/AudienceClient

echo "cp ./AuksalaqOperaCntrl/*.class /var/www/AuksalaqNOMADS/ControlPanel"
cp ./AuksalaqOperaCntrl/*.class /var/www/AuksalaqNOMADS/ControlPanel
echo "cp ./AuksalaqOperaCntrl/*.jar /var/www/AuksalaqNOMADS/ControlPanel"
cp ./AuksalaqOperaCntrl/*.jar /var/www/AuksalaqNOMADS/ControlPanel

echo "cp ./AuksalaqOperaMain/*.class /var/www/AuksalaqNOMADS/MainDisplay"
cp ./AuksalaqOperaMain/*.class /var/www/AuksalaqNOMADS/MainDisplay
echo "cp ./AuksalaqOperaMain/*.jar /var/www/AuksalaqNOMADS/MainDisplay"
cp ./AuksalaqOperaMain/*.jar /var/www/AuksalaqNOMADS/MainDisplay