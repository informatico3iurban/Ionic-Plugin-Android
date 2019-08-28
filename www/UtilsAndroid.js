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

module.exports.log = function (arg0, success, error){
    exec(success, error, 'UtilsAndroid', 'log', [arg0]);
}

module.exports.logError = function (arg0, success, error){
    exec(success, error,'UtilsAndroid', 'logError', [arg0]);
}

module.exports.exitKiosk = function (success, error){
    exec(success, error, 'UtilsAndroid', 'exitKiosk');
}

module.exports.isInKiosk = function (success, error){
    exec(success, error, 'UtilsAndroid', 'isInKiosk');
}

module.exports.enableKioskMode = function (success, error){
    exec(success, error, 'UtilsAndroid', 'enableKioskMode');
}

module.exports.setDeviceOwner = function (success, error){
    exec(success, error, 'UtilsAndroid', 'setDeviceOwner');
}

module.exports.removeDeviceOwner = function (success, error){
    exec(success, error, 'UtilsAndroid', 'removeDeviceOwner');
}
