#!/bin/sh

keyfile_location=/etc/mongo/mongodb-keyfile.key
replicaset_Name=rs0
createUserScriptPath=/createUser.js
port=27017

apt-get update
apt-get -y install sudo
openssl rand -base64 756 > ${keyfile_location}
sudo chmod 400 ${keyfile_location}
chown 999:999 ${keyfile_location}

function_createUser() {
    sleep 20s
    mongosh admin --file ${createUserScriptPath} 
}

function_createUser & mongod --keyFile ${keyfile_location} --replSet ${replicaset_Name} --bind_ip_all --port ${port}