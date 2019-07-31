var exec = require('cordova/exec');

module.exports.printTicket = function (arg0,success, error){
    exec(success, error, 'UtilsAndroid', 'printTicket', [arg0]);
}

module.exports.getMacAddress = function (success, error){
    exec(success, error, 'UtilsAndroid', 'getMacAddress');
}

module.exports.connectToWifi = function (arg0, success, error){
    exec(success, error, 'UtilsAndroid', 'connectToWifi', [arg0]);
}

module.exports.enableWifi = function (success, error){
    exec(success, error, 'UtilsAndroid', 'enableWifi');
}