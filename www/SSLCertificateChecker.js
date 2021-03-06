"use strict";
var exec = require('cordova/exec');

var DEFAULT_ALGORITHM = "SHA-1";

function SSLCertificateChecker() {
}

SSLCertificateChecker.prototype.check = function (successCallback, errorCallback, serverURL, allowedSHA1FingerprintOrArray, algorithm) {
  if (typeof errorCallback != "function") {
    console.log("SSLCertificateChecker.find failure: errorCallback parameter must be a function");
    return
  }

  if (typeof successCallback != "function") {
    console.log("SSLCertificateChecker.find failure: successCallback parameter must be a function");
    return
  }

  // if an array is not passed, transform the input into one
  var fpArr = [];
  if (allowedSHA1FingerprintOrArray !== undefined) {
      if (typeof allowedSHA1FingerprintOrArray == "string") {
          fpArr.push(allowedSHA1FingerprintOrArray);
      } else {
          fpArr = allowedSHA1FingerprintOrArray.slice(0);
      }
  }
  
  algorithm = algorithm || DEFAULT_ALGORITHM;

  exec(successCallback, errorCallback, "SSLCertificateChecker", "check", [serverURL, false, fpArr, algorithm]);
};

SSLCertificateChecker.prototype.getFingerprint = function (successCallback, errorCallback, serverURL, algorithm) {
  if (typeof errorCallback != "function") {
    console.log("SSLCertificateChecker.get failure: errorCallback parameter must be a function");
    return
  }

  if (typeof successCallback != "function") {
    console.log("SSLCertificateChecker.get failure: successCallback parameter must be a function");
    return
  }
  
  algorithm = algorithm || DEFAULT_ALGORITHM;

  exec(successCallback, errorCallback, "SSLCertificateChecker", "getFingerprint", [serverURL, algorithm]);
};

SSLCertificateChecker.prototype.checkInCertChain = function (successCallback, errorCallback, serverURL, allowedSHA1FingerprintOrArray, allowedSHA1FingerprintAlt) {
  if (typeof errorCallback != "function") {
    console.log("SSLCertificateChecker.find failure: errorCallback parameter must be a function");
    return
  }
  errorCallback("This function has been removed in versions higher than 4.0.0 because it's considered too insecure.");
  /*
  if (typeof successCallback != "function") {
    console.log("SSLCertificateChecker.find failure: successCallback parameter must be a function");
    return
  }
  // if an array is not passed, transform the input into one
  var fpArr = [];
  if (allowedSHA1FingerprintOrArray !== undefined) {
    if (typeof allowedSHA1FingerprintOrArray == "string") {
      fpArr.push(allowedSHA1FingerprintOrArray);
    } else {
      fpArr = allowedSHA1FingerprintOrArray.slice(0);
    }
  }
  if (allowedSHA1FingerprintAlt !== undefined) {
    fpArr.push(allowedSHA1FingerprintAlt);
  }
  cordova.exec(successCallback, errorCallback, "SSLCertificateChecker", "check", [serverURL, true, fpArr]);
  */
};

var sslCertificateChecker = new SSLCertificateChecker();
module.exports = sslCertificateChecker;
