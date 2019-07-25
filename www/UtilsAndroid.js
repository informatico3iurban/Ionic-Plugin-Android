var exec = require('cordova/exec');

module.exports.printTicket = function (arg0,success, error){
    exec(success, error, 'UtilsAndroid', 'printTicket', [arg0]);
}

module.exports.getMacAddress = function (success, error){
    exec(success, error, 'UtilsAndroid', 'getMacAddress');
}