var exec = require('cordova/exec');

exports.getToken = function (success, error) {
    exec(success, error, 'Upush', 'getToken', []);
};
