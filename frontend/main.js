var app = require('app');  // Module to control application life.
var BrowserWindow = require('browser-window');  // Module to create native browser window.
//var WebSocketClient = require('websocket').client;
//var ws = require("nodejs-websocket");
var WebSocket = require('ws');

// Report crashes to our server.
//require('crash-reporter').start();

// Keep a global reference of the window object, if you don't, the window will
// be closed automatically when the javascript object is GCed.
var mainWindow = null;

// Quit when all windows are closed.
app.on('window-all-closed', function() {
  if (process.platform != 'darwin')
    app.quit();
});

// This method will be called when Electron has done everything
// initialization and ready for creating browser windows.
app.on('ready', function() {
  // Create the browser window.
  mainWindow = new BrowserWindow({width: 800, height: 600});

  // and load the index.html of the app.
  mainWindow.loadUrl('file://' + __dirname + '/index.html');

  // Open the devtools.
  mainWindow.openDevTools();
  
  //connectToServer();
 
  // Emitted when the window is closed.
  mainWindow.on('closed', function() {
    // Dereference the window object, usually you would store windows
    // in an array if your app supports multi windows, this is the time
    // when you should delete the corresponding element.
    mainWindow = null;
  });
});

function connectToServer() {
   /*var connection = new WebSocket('ws://192.168.43.98:10000');
  
  connection.onopen = function () {
    connection.send('Ping'); // Send the message 'Ping' to the server
  };

  // Log errors
  connection.onerror = function (error) {
    console.log('WebSocket Error ' + error);
  };
  
  // Log messages from the server
  connection.onmessage = function (e) {
    console.log('Server: ' + e.data);
  };*/
  
  var client = new WebSocketClient();
  
  client.on('connectFailed', function(error) {
      console.log('Connect Error: ' + error.toString());
  });
   
  client.on('connect', function(connection) {
      console.log('WebSocket Client Connected');
      connection.on('error', function(error) {
          console.log("Connection Error: " + error.toString());
      });
      connection.on('close', function() {
          console.log('echo-protocol Connection Closed');
      });
      connection.on('message', function(message) {
          if (message.type === 'utf8') {
              console.log("Received: '" + message.utf8Data + "'");
          }
      });
      
      function sendNumber() {
          if (connection.connected) {
              var number = Math.round(Math.random() * 0xFFFFFF);
              connection.sendUTF(number.toString());
              setTimeout(sendNumber, 1000);
          }
      }
      sendNumber();
  });
   
  client.connect('ws://192.168.43.98:10000', 'echo-protocol');
}
